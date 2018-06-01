package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.db.graph.Graph;
import java.util.function.Consumer;
import java.util.logging.Logger;

/** processes and logs data about a graph in a experimental context */
public interface GraphProcessor extends Consumer<Graph> {

    Logger logger = Logger.getLogger(GraphProcessor.class.getCanonicalName());

    public static GraphProcessor from(GraphMetric metric, String name) {
        return g -> {
            logger.info("metric " + name + ": " + metric.apply(g));
        };
    }
}
