package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.HashSet;
import java.util.Set;

/* A DB metric that can be used to compute the
 number of unique attributes for a given graph.
 Created by amorehead on 2/23/18. */
public class UniqueAttributesPerGraph implements IndependentDBMetric {

    private final Set<String> featureNames;

    public UniqueAttributesPerGraph() {
        this.featureNames = null;
    }

    public boolean isFeatureOfInterest(Feature f) {
        // The following indicates that all features are of interest.
        return featureNames == null ||
                // The following indicates that the metric is included in the featureNames of interest.
                featureNames.contains(f.getName());
    }

    @Override
    // This method represents the main functionality of this class.
    public double processGraph(Graph g) {
        Set<Feature> discoveredFeatures = new HashSet<>();

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

        return discoveredFeatures.size();
    }
}