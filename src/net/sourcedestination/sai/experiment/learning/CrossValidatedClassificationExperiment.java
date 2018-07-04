package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.GraphHidingDB;
import net.sourcedestination.sai.util.Task;

import java.util.function.Function;
import java.util.stream.IntStream;

import static net.sourcedestination.sai.db.GraphHidingDB.wrap;

public class CrossValidatedClassificationExperiment implements Task<Integer> {

    private int folds;
    private String dbname;
    private DBInterface dataset;
    private Function<Graph,String> expectedClasses;
    private ClassificationModelGenerator gen;

    public CrossValidatedClassificationExperiment(
            int folds,
            ClassificationModelGenerator gen,
            DBInterface dataset,
            String dbname,
            Function<Graph,String> expectedClasses) {
        this.folds = folds;
        this.dataset = dataset;
        this.dbname = dbname;
        this.expectedClasses = expectedClasses;
        this.gen = gen;
    }

    /** leave one out test */
    public CrossValidatedClassificationExperiment(
            ClassificationModelGenerator gen,
            DBInterface dataset,
            String dbname,
            Function<Graph,String> expectedClasses
    ) {
        this(dataset.getDatabaseSize(), gen, dataset, dbname, expectedClasses);
    }

    @Override
    public Integer get() {
        // TODO: log epoch training/test set ranges
        var foldSize = dataset.getDatabaseSize() / folds;

        return IntStream.range(0, folds-1)
            .map( fold -> {
                GraphHidingDB trainingSet = wrap(dataset, "training-fold-"+fold);
                dataset.getGraphIDStream()
                        .skip(fold * foldSize)
                        .limit(foldSize)
                        .forEach(trainingSet::hideGraph);

                GraphHidingDB testSet = wrap(dataset, "test-fold-"+fold);
                dataset.getGraphIDStream()
                        .limit(fold * foldSize)
                        .forEach(testSet::hideGraph);
                dataset.getGraphIDStream()
                        .skip((1 + fold) * foldSize)
                        .forEach(testSet::hideGraph);

                ClassificationExperiment foldExp = new ClassificationExperiment(
                        testSet,
                        dbname,
                        gen.apply(trainingSet, expectedClasses),
                        expectedClasses);
                return foldExp.get();
            }).sum();
    }
}
