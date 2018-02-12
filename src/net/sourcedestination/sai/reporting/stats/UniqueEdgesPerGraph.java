package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A DB statistic that can be used to calculate the
 * number of unique edges in a given database of graphs.
 * Created by amorehead on 2/7/18.
 */
public class UniqueEdgesPerGraph implements IndependentDBStatistic {

    private final Set<String> featureNames;
    private Set<Triple<Set<Feature>>> edgeTypes;

    public UniqueEdgesPerGraph() {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = null;
    }

    // The following methods are no longer needed due to this class' current implementation.
    public UniqueEdgesPerGraph(Set<String> featureNames) {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = featureNames;
    }

    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
                featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    // This returns the result we are looking for.
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