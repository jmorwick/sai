package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.GraphHidingDB;
import net.sourcedestination.sai.util.Task;

import java.util.function.Function;
import java.util.stream.IntStream;

import static net.sourcedestination.sai.db.GraphHidingDB.wrap;

public class CrossValidatedClassificationExperiment implements Task {

    private int folds;
    private DBInterface dataset;
    private Function<Graph, String> model;
    private Function<Graph,String> expectedClasses;
    private ClassificationModelGenerator gen;

    public CrossValidatedClassificationExperiment(
            int folds,
            ClassificationModelGenerator gen,
            DBInterface dataset,
            ClassificationModel model,
            Function<Graph,String> expectedClasses) {
        this.folds = folds;
        this.dataset = dataset;
        this.model = model;
        this.expectedClasses = expectedClasses;
        this.gen = gen;
    }

    @Override
    public Object get() {
        var foldSize = dataset.getDatabaseSize() / folds;
        IntStream.range(0, folds-1)
            .parallel()
            .forEach( fold -> {
                GraphHidingDB trainingSet = wrap(dataset, "training-fold-"+fold);
                dataset.getGraphIDStream()
                        .skip(fold * foldSize)
                        .limit(foldSize)
                        .forEach(trainingSet::hideGraph);

                GraphHidingDB testSet = wrap(dataset, "test-fold-"+fold);
                dataset.getGraphIDStream()
                        .limit(fold * foldSize)
                        .forEach(trainingSet::hideGraph);
                dataset.getGraphIDStream()
                        .skip((1 + fold) * foldSize)
                        .forEach(trainingSet::hideGraph);

                ClassificationExperiment foldExp = new ClassificationExperiment(
                        dataset,
                        dataset.getGraphIDStream()
                                .skip(fold * foldSize)
                                .limit(foldSize),
                        gen.apply(trainingSet, expectedClasses),
                        expectedClasses);
            });
        return null;
    }
}
