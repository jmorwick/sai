package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.task.Task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A DB metric template interface that is
 * implemented by DB metric subclasses in order
 * for a customized "processGraph" method to be
 * applied to every graph in a given database.
 * Created by amorehead on 2/7/18.
 */

public interface IndependentDBMetric extends DBMetric {

    // The following represents a placeholder method to be overridden in subclasses.
    public double processGraph(Graph g);

    public default Task<Double> apply(DBInterface db) {
        IndependentDBMetric self = this;
        return new Task<Double>() {
            private final AtomicInteger progressCounter = new AtomicInteger();
            private final int size = db.getDatabaseSize();

            public Double get() {
                return db.getGraphIDStream()
                        .map(db::retrieveGraph)
                        // The following increments the progress counter for each metric.
                        .peek(g -> progressCounter.incrementAndGet())
                        .mapToDouble(self::processGraph)
                        .average().orElse(0);
            }

            @Override
            public double getPercentageDone() { return ((double)progressCounter.get()) / size; }
        };
    }
}
