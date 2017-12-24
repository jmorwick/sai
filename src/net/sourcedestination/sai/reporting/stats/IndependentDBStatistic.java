package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.task.Task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/** A DB statistic that can be calculated by examining each graph, independently, in parallel.
 * Created by jmorwick on 7/7/17.
 */
public interface IndependentDBStatistic extends DBStatistic {

    public void processGraph(Graph g);
    public double getResult();

    public default Task<Double> apply(DBInterface db) {
        return new Task<Double> () {
            private final AtomicInteger count = new AtomicInteger();

            public Double get() {
                int dbsize = db.getDatabaseSize();
                Set<Feature> features = new HashSet<>();
                db.getGraphIDStream()
                        .forEach(id -> {
                            Graph g = db.retrieveGraph(id);
                            processGraph(g);
                            count.incrementAndGet();
                        });
                if(dbsize > 0) return getResult() / dbsize;
                return 0.0;
            }

            @Override
            public double getPercentageDone() {
                return (double)count.get() / (db.getDatabaseSize());
            }
        };
    }
}
