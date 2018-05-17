package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.graph.Graph;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

/** processes and logs data about a graph in a experimental context */
public interface GraphProcessor extends Consumer<Graph> {

    final static Logger logger = Logger.getLogger(GraphProcessor.class);

    public static GraphProcessor from(GraphMetric metric, String name) {
        return g -> {
            logger.info("metric " + name + ": " + metric.apply(g));
        };
    }
}
