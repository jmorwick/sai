package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

/* A DB metric that finds the ratio of isolated nodes, those with
 degree zero, to the total number of nodes in the entire graph.
 Created by amorehead on 2/9/18. */
public class PercentageOfIsolatedPoints implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {
        int totalNodes = ((int) g.getNodeIDs().count());
        // The following represents the processing of a stream of integers.
        return totalNodes == 0 ? 0.0 :
                g.getNodeIDs()
                        /* The following finds the quotient of dividing the number
                        of nodes with degree zero by the total number of nodes. */
                        .filter(nid -> (double) g.getIncidentEdges(nid).count() == 0)
                        .count() / (double) totalNodes;
    }
}
