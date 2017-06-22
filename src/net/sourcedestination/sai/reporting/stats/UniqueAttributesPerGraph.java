package net.sourcedestination.sai.reporting.stats;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.task.Task;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**  TODO: add test
 *   TODO: comment / license
 */
public class UniqueAttributesPerGraph implements DBStatistic {

    private final Set<String> featureNames;

    public UniqueAttributesPerGraph() {
        this.featureNames = null;
    }
    public UniqueAttributesPerGraph(Set<String> featureNames) {
        this.featureNames = featureNames;
    }

    public Task<Double> apply(DBInterface db) {
        final Predicate<Feature> retrieveSpecifiedFeatures =
                f -> featureNames == null || //  ... only filter if there is a list
                     featureNames.contains(f.getName());

        return new Task<Double> () {
            private final AtomicInteger count = new AtomicInteger();

            public Double get() {
                GraphFactory gf = ImmutableGraph::new;
                int dbsize = db.getDatabaseSize();
                Set<Feature> features = new HashSet<>();
                db.getGraphIDStream()
                        .forEach(id -> {
                            Graph g = db.retrieveGraph(id, gf);
                            g.getFeatures().filter(retrieveSpecifiedFeatures)
                                    .forEach(features::add);
                            g.getNodeIDs()
                                    .map(g::getNodeFeatures)
                                    .forEach(s -> s
                                            .filter(retrieveSpecifiedFeatures)
                                            .forEach(features::add));
                            g.getEdgeIDs()
                                    .map(g::getEdgeFeatures)

                                    .forEach(s -> s
                                            .filter(retrieveSpecifiedFeatures)
                                            .forEach(features::add));
                            count.incrementAndGet();
                        });
                if(dbsize > 0) return (double)features.size() / dbsize;
                return 0.0;
            }

            @Override
            public double getPercentageDone() {
                return (double)count.get() / (db.getDatabaseSize());
            }
        };
    }
}