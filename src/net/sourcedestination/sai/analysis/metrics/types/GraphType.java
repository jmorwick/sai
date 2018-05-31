package net.sourcedestination.sai.analysis.metrics.types;

import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.analysis.GraphMetric;

import java.util.function.Predicate;

/**
 * Created by jmorwick on 7/7/17.
 */
@FunctionalInterface
public interface GraphType extends GraphMetric, Predicate<Graph> {

    @Override
    public default Double apply(Graph g) {
        return test(g) ? 1.0 : 0.0;
    }
}
