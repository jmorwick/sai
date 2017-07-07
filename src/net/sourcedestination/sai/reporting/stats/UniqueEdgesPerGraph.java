package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**  TODO: add test
 *   TODO: comment / license
 */
public class UniqueEdgesPerGraph implements IndependentDBStatistic {

    private final Set<String> featureNames;
    private Set<Triple<Set<Feature>>> edgeTypes;

    public UniqueEdgesPerGraph() {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = null;
    }
    public UniqueEdgesPerGraph(Set<String> featureNames) {
        this.edgeTypes = ConcurrentHashMap.newKeySet();
        this.featureNames = featureNames;
    }

    public boolean isFeatureOfInterest(Feature f) {
        return featureNames == null ||  // all features are of interest
                featureNames.contains(f.getName());   // it's included in the feature names of interest
    }

    public void processGraph(Graph g) {
        g.getEdgeIDs()
                .map(eid -> Triple.makeTriple(
                        g.getNodeFeaturesSet(g.getEdgeSourceNodeID(eid)),
                        g.getNodeFeaturesSet(g.getEdgeTargetNodeID(eid)),
                        g.getEdgeFeaturesSet(eid)))
                .forEach(edgeTypes::add);
    }

    public double getResult() {
        return edgeTypes.size();
    }
}