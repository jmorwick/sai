package net.sourcedestination.sai.task;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.learning.ClassificationModel;
import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;

import java.util.function.Function;
import java.util.stream.Stream;

public class ClassificationExperiment implements Task<Log> {

    private final DBInterface testSet;
    private final Function<Graph, String> model;
    private final Function<Graph,String> expectedClasses;
    private final Stream<Integer> graphIds;

    public ClassificationExperiment(DBInterface testSet,
                                    Stream<Integer> graphIds,
                                    ClassificationModel model,
                                    Function<Graph,String> expectedClasses) {
        this.testSet = testSet;
        this.model = model;
        this.graphIds = graphIds;
        this.expectedClasses = expectedClasses;
    }

    public Log get() {
        Log log = new Log("classification task");
        int size = testSet.getDatabaseSize();
        int correct = (int)graphIds
                .filter(gid -> {
                    Graph g = testSet.retrieveGraph(gid);
                    String result = expectedClasses.apply(g);
                    String expected = model.apply(g);
                    log.recordClassification(testSet, gid, result, expected);
                    return expected.equals(result);
                })
                .count();
        return log;
    }
}
