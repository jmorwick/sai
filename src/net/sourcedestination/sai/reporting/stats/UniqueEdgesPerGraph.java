package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.funcles.tuple.Triple;
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

/**
 * Created by jmorwick on 6/22/17.
 */
public class UniqueEdgesPerGraph implements DBStatistic {
    private final Set<String> featureNames;

    public UniqueEdgesPerGraph() {
        this.featureNames = null;
    }
    public UniqueEdgesPerGraph(Set<String> featureNames) {
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
                Set<Triple<Set<Feature>>> edgeTypes = new HashSet<>();
                db.getGraphIDStream()
                        .forEach(id -> {
                                    Graph g = db.retrieveGraph(id, gf);
                                    g.getEdgeIDs()
                                            .map(eid -> Triple.makeTriple(
                                                    g.getNodeFeaturesSet(g.getEdgeSourceNodeID(eid)),
                                                    g.getNodeFeaturesSet(g.getEdgeTargetNodeID(eid)),
                                                    g.getEdgeFeaturesSet(eid)))
                                            .forEach(edgeTypes::add);
                        });
                count.incrementAndGet();
                if(dbsize > 0) return (double)edgeTypes.size() / dbsize;
                return 0.0;
            }

            @Override
            public double getPercentageDone() {
                return (double)count.get() / (db.getDatabaseSize());
            }
        };
    }
}
