package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.task.Task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A DB statistic template interface that can be
 * implemented by DB statistic subclasses in order
 * to apply a customized "processGraph" method to
 * every graph in a given database.
 * Created by amorehead on 2/7/18.
 */

public interface IndependentDBStatistic extends DBStatistic {

    // The following represents a placeholder method to be overridden in subclasses.
    public double processGraph(Graph g);

    public default Task<Double> apply(DBInterface db) {
        IndependentDBStatistic self = this;
        return new Task<Double>() {
            private final AtomicInteger count = new AtomicInteger();

            public Double get() {
                int dbsize = db.getDatabaseSize();
                long total = db.getGraphIDStream()
                        .map(id -> db.retrieveGraph(id))
                        .peek(g -> count.incrementAndGet()) // This increments the progress counter.
                        .map(self::processGraph)
                        .count();
                if (dbsize > 0) return (double) total / dbsize;
                return 0.0;
            }
        };
    }
}
