package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.metrics.GraphMetric;

import java.util.HashSet;
import java.util.Set;

/* A DB metric that can be used to compute the
 number of unique attributes for a given graph.
 Updated by amorehead on 4/13/18. */
public class UniqueAttributesPerGraph implements GraphMetric {

    private final Set<String> featureNames;

    public UniqueAttributesPerGraph() {
        this.featureNames = null;
    }

    public boolean isFeatureOfInterest(Feature f) {
        // The following indicates that a given feature is of interest.
        return featureNames == null ||
                // The following indicates that the metric is not included in the featureNames of interest.
                !(featureNames.contains(f.getName()));
    }

    @Override
    // This method represents the main functionality of this class.
    public Double apply(Graph g) {
        Set<Feature> discoveredFeatures = new HashSet<>();

        // The following returns the number of unique graph features (or attributes) in a given graph.
        g.getFeatures().filter(this::isFeatureOfInterest)
                .forEach(discoveredFeatures::add);

        // The following returns the number of unique node features (or attributes) in a given graph.
        g.getNodeIDs()
                .map(g::getNodeFeatures)
                .forEach(s -> s
                        .filter(this::isFeatureOfInterest)
                        .forEach(discoveredFeatures::add));

        // The following returns the number of unique edge features (or attributes) in a given graph.
        g.getEdgeIDs()
                .map(g::getEdgeFeatures)
                .forEach(s -> s
                        .filter(this::isFeatureOfInterest)
                        .forEach(discoveredFeatures::add));

        // The following returns the number of unique features (or attributes) in a given graph.
        return (double)discoveredFeatures.size();
    }
}