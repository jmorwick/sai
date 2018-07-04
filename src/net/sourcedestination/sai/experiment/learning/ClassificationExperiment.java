package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.util.Task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ClassificationExperiment implements Task<Integer> {

    private final DBInterface testSet;
    private final Function<Graph, String> model;
    private final Function<Graph,String> expectedClasses;
    private final int ID;
    private final String dbname;
    private static AtomicInteger nextID = new AtomicInteger(0);

    private static Logger logger = Logger.getLogger(ClassificationExperiment.class.getCanonicalName());

    public ClassificationExperiment(DBInterface testSet,
                                    String dbname,
                                    ClassificationModel model,
                                    Function<Graph,String> expectedClasses) {
        this.testSet = testSet;
        this.model = model;
        this.dbname = dbname;
        this.expectedClasses = expectedClasses;
        synchronized (ClassificationExperiment.class) {
            ID = nextID.incrementAndGet();
        }
    }

    public int getExperimentId() { return ID; }

    @Override
    public Integer get() {
        logger.info("beginning classification experiment #" + ID);
        var size = testSet.getDatabaseSize();
        var correct = (int)testSet.getGraphIDStream()
                .filter(gid -> {
                    logger.info("in experiment #" + ID + " beginning test for graph #" + gid);
                    var g = testSet.retrieveGraph(gid);
                    logger.info("in experiment #" + ID + " retrieved test graph #" + gid + " from " + dbname);
                    var expected = expectedClasses.apply(g);
                    logger.info("in experiment #" + ID + " expecting " + expected + " for graph #" + gid);
                    var result = model.apply(g);
                    logger.info("in experiment #" + ID + " classified graph #" + gid + " as " + result);
                    return expected.equals(result);
                })
                .count();
        logger.info("experiment #" + ID + " complete with " + correct + " out of " + size +
                        " classified correctly");
        return correct;
    }
}