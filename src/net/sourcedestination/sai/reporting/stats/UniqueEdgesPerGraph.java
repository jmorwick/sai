package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A DB metric that can be used to compute the
 * number of unique edges for a given graph.
 * Created by amorehead on 2/7/18.
 */
public class UniqueEdgesPerGraph implements IndependentDBMetric {

    private final Set<String> featureNames;
    private Set<Triple<Set<Feature>>> edgeTypes;

    public UniqueEdgesPerGraph() {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = null;
    }

    // The following method is no longer needed for the current implementation of this metric class.
    public UniqueEdgesPerGraph(Set<String> featureNames) {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = featureNames;
    }

    // The following method is no longer needed for the current implementation of this metric class.
    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
                featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    // This returns the result of particular interest.
    public double getResult() {
        return edgeTypes.size();
    }

    @Override
    // This method represents the main functionality of this class.
    public double processGraph(Graph g) {
        g.getEdgeIDs()
                .map(eid -> Triple.makeTriple(
                        g.getNodeFeaturesSet(g.getEdgeSourceNodeID(eid)),
                        g.getNodeFeaturesSet(g.getEdgeTargetNodeID(eid)),
                        g.getEdgeFeaturesSet(eid)))
                .forEach(edgeTypes::add);
        return getResult();
    }
}