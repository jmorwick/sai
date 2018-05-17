package net.sourcedestination.sai.analysis.metrics.types;

import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.analysis.GraphMetric;

import java.util.function.Predicate;

/**
 * Created by jmorwick on 7/7/17.
 */
@FunctionalInterface
public interface GraphType extends Predicate<Graph> {

    public default GraphMetric toGraphMetric() {
        return g -> {
            return this.test(g) ? 1.0 : 0.0;
        };
    }
}
