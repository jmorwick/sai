package net.sourcedestination.sai.task;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.learning.ClassificationModel;
import org.apache.log4j.Logger;

import java.util.function.Function;
import java.util.stream.Stream;

public class ClassificationExperiment implements Task {

    private final DBInterface testSet;
    private final Function<Graph, String> model;
    private final Function<Graph,String> expectedClasses;
    private final Stream<Integer> graphIds;
    private final int ID;
    private static int nextID = 1;

    private static Logger logger = Logger.getLogger(ClassificationExperiment.class);

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
        int size = testSet.getDatabaseSize();
        int correct = (int)graphIds
                .filter(gid -> {
                    Graph g = testSet.retrieveGraph(gid);
                    String result = expectedClasses.apply(g);
                    String expected = model.apply(g);
                    logger.info(ID + ": classified " + gid + " + as " + result + " expected " + expected);
                    return expected.equals(result);
                })
                .count();
        logger.info(ID + ": test complete");
        logger.info(ID + ": classified " + correct + " out of " + size + " correctly ");
        return null;
    }
}