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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by jmorwick on 7/7/17.
 */
public interface GraphFilterStatistic extends DBStatistic {

    public boolean filterGraph(Graph g);

    public default Task<Double> apply(DBInterface db) {
        final Predicate<Graph> filter = this::filterGraph;
        return new Task<Double> () {
            private final AtomicInteger count = new AtomicInteger();

            public Double get() {
                GraphFactory gf = ImmutableGraph::new;
                int dbsize = db.getDatabaseSize();
                Set<Feature> features = new HashSet<>();
                long total = db.getGraphIDStream()
                        .map(id -> db.retrieveGraph(id, gf))
                        .filter(g -> {
                            count.incrementAndGet();
                            return filter.test(g);
                        })
                        .count();
                if(dbsize > 0) return (double)total / dbsize;
                return 0.0;
            }

            @Override
            public double getPercentageDone() {
                return (double)count.get() / (db.getDatabaseSize());
            }
        };
    }
}
