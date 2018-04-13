package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import java.util.HashSet;
import java.util.Set;

/**
 * A DB metric that can be used to compute the
 * number of unique edges for a given graph.
 * Updated by amorehead on 4/13/18.
 */
public class UniqueEdgesPerGraph implements IndependentDBMetric {

    @Override
    // This method represents the main functionality of this class.
    public double processGraph(Graph g) {
        Set<Triple<Set<Feature>>> edgeTypes = new HashSet<>();

        g.getEdgeIDs()
                .map(eid -> Triple.makeTriple(
                        g.getNodeFeaturesSet(g.getEdgeSourceNodeID(eid)),
                        g.getNodeFeaturesSet(g.getEdgeTargetNodeID(eid)),
                        g.getEdgeFeaturesSet(eid)))
                .forEach(edgeTypes::add);

        return edgeTypes.size();
    }
}