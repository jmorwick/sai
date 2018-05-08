package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.task.Task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/* A DB metric that can be calculated by examining
 every graph in a given database in parallel.
 Created by amorehead on 1/31/18. */
public interface ComprehensiveDBMetric extends DBMetric {

    public double processGraph(Graph g);

    public double getResult();

    public default Task<Double> apply(DBInterface db) {
        return new Task<Double>() {
            private final AtomicInteger count = new AtomicInteger();

            @Override
            public Double get() {
                int dbsize = db.getDatabaseSize();
                Set<Feature> features = new HashSet<>();
                db.getGraphIDStream()
                        .forEach(id -> {
                            Graph g = db.retrieveGraph(id);
                            processGraph(g);
                            count.incrementAndGet();
                        });
                if (dbsize > 0) return getResult() / dbsize;
                return 0.0;
            }

            @Override
            public double getPercentageDone() {
                return (double) count.get() / (db.getDatabaseSize());
            }
        };
    }
}
