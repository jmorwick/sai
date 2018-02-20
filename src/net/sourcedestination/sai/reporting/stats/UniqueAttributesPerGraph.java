package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* A DB statistic that can be used to compute the
 number of unique attributes for a given graph.
 Created by amorehead on 1/31/18. */
public class UniqueAttributesPerGraph implements IndependentDBStatistic {

    private final Set<String> featureNames;
    private Set<Feature> discoveredFeatures;

    public UniqueAttributesPerGraph() {
        this.discoveredFeatures = ConcurrentHashMap.newKeySet();
        this.featureNames = null;
    }

    // The following are no longer needed for the current implementation of this class.
    public UniqueAttributesPerGraph(Set<String> featureNames) {
        this.discoveredFeatures = ConcurrentHashMap.newKeySet();
        this.featureNames = featureNames;
    }

    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
                featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    // This returns the result we are looking for.
    public double getResult() {
        return discoveredFeatures.size();
    }

    @Override
    // This method represents the main functionality of this class.
    public double processGraph(Graph g) {
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
        return getResult();
    }
}