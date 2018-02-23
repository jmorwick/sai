package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/* A DB metric that can be used to compute the
 number of unique attributes for a given graph.
 Created by amorehead on 1/31/18. */
public class UniqueAttributesPerGraph implements IndependentDBMetric {

    private final Set<String> featureNames;

    public UniqueAttributesPerGraph() {
        this.featureNames = null;
    }

    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
                featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    @Override
    // This method represents the main functionality of this class.
    public double processGraph(Graph g) {
        Logger log = Logger.getLogger(UniqueEdgesPerGraph.class.getName());
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

        log.info("Unique attributes per graph: " + discoveredFeatures.size()
                + " (currently not displaying in WebLab)");
        return discoveredFeatures.size();
    }
}