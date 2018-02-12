package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

/* A DB statistic that finds the ratio of end points, those with
 degree one, to the total number of nodes in the entire graph.
 Created by amorehead on 2/12/18. */
public class PercentageOfEndPoints implements IndependentDBStatistic {

    @Override
    public double processGraph(Graph g) {
        int totalNodes = ((int) g.getNodeIDs().count());
        // The following represents the processing of a stream of integers.
        return totalNodes == 0 ? 0.0 :
                g.getNodeIDs()
                        /* The following finds the quotient of dividing the number
                        of nodes with degree one by the total number of nodes. */
                        .filter(nid -> (double) g.getIncidentEdges(nid).count() == 1)
                        .count() / (double) totalNodes;
    }
}
