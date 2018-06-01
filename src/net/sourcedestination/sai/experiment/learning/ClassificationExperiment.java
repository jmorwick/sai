package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.indexing.BasicIndexingDBWrapper;
import net.sourcedestination.sai.util.Task;

import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ClassificationExperiment implements Task {

    private final DBInterface testSet;
    private final Function<Graph, String> model;
    private final Function<Graph,String> expectedClasses;
    private final Stream<Integer> graphIds;
    private final int ID;
    private static int nextID = 1;

    private static Logger logger = Logger.getLogger(ClassificationExperiment.class.getCanonicalName());

    public ClassificationExperiment(DBInterface testSet,
                                    Stream<Integer> graphIds,
                                    ClassificationModel model,
                                    Function<Graph,String> expectedClasses) {
        this.testSet = testSet;
        this.model = model;
        this.graphIds = graphIds;
        this.expectedClasses = expectedClasses;
        synchronized (ClassificationExperiment.class) {
            ID = nextID++;
        }
    }

    @Override
    public Object get() {
        logger.info(ID + ": test beginning");
        var size = testSet.getDatabaseSize();
        var correct = (int)graphIds
                .filter(gid -> {
                    var g = testSet.retrieveGraph(gid);
                    var result = expectedClasses.apply(g);
                    var expected = model.apply(g);
                    logger.info(ID + ": classified " + gid + " + as " + result + " expected " + expected);
                    return expected.equals(result);
                })
                .count();
        logger.info(ID + ": test complete");
        logger.info(ID + ": classified " + correct + " out of " + size + " correctly ");
        return null;
    }
}