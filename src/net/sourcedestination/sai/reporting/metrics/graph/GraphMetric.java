package net.sourcedestination.sai.reporting.metrics.graph;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.metrics.db.DBMetric;
import net.sourcedestination.sai.task.Task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * A metric for individual graphs
 *
 * Created by amorehead on 2/7/18.
 * Modified by jbmorwick
 */

@FunctionalInterface
public interface GraphMetric extends Function<Graph, Double> {



    /** creates a DBMetric that averages this GraphMetric over each Graph */
    public default DBMetric createAverageDBMetric() {
        GraphMetric self = this;
        return new DBMetric() {
            @Override
            public Task<Double> apply(DBInterface db) {
                return new Task<Double>() {
                    private final AtomicInteger progressCounter = new AtomicInteger();
                    private final int size = db.getDatabaseSize();

                    public Double get() {
                        return db.getGraphIDStream()
                                .map(db::retrieveGraph)
                                // The following increments the progress counter for each metric.
                                .peek(g -> progressCounter.incrementAndGet())
                                .mapToDouble(self::apply)
                                .average().orElse(0);
                    }

                    @Override
                    public double getPercentageDone() {
                        return ((double) progressCounter.get()) / size;
                    }
                };
            }
        };
    }
}
