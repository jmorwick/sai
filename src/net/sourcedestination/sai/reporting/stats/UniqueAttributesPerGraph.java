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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**  TODO: add test
 *   TODO: comment / license
 */
public class UniqueAttributesPerGraph implements IndependentDBStatistic {

    private final Set<String> featureNames;
    private Set<Feature> discoveredFeatures;

    public UniqueAttributesPerGraph() {
        this.discoveredFeatures = ConcurrentHashMap.newKeySet();
        this.featureNames = null;
    }
    public UniqueAttributesPerGraph(Set<String> featureNames) {
        this.discoveredFeatures = ConcurrentHashMap.newKeySet();
        this.featureNames = featureNames;
    }

    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
            featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    public void processGraph(Graph g) {
        g.getFeatures().filter(this::isFeatureOfInterest)
                .forEach(discoveredFeatures::add);
        g.getNodeIDs()
                .map(g::getNodeFeatures)
                .forEach(s -> s
                        .filter(this::isFeatureOfInterest)
                        .forEach(discoveredFeatures::add));
        g.getEdgeIDs()
                .map(g::getEdgeFeatures)

                .forEach(s -> s
                        .filter(this::isFeatureOfInterest)
                        .forEach(discoveredFeatures::add));
    }

    public double getResult() {
        return discoveredFeatures.size();
    }
}