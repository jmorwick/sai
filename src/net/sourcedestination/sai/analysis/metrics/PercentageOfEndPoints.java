package net.sourcedestination.sai.analysis.metrics;

import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.analysis.GraphMetric;

/* A graph metric that finds the ratio of end points, those with
 degree one, to the total number of nodes in the entire graph.
 Created by amorehead on 2/12/18. */
public class PercentageOfEndPoints implements GraphMetric {

    @Override
    public Double apply(Graph g) {
        double totalNodes = (g.getNodeIDs().count());

        // The following is a simple check to see if there are any nodes in a given graph.
        if (totalNodes == 0.0) return totalNodes;

        // The following represents the processing of a stream of integers.
        totalNodes = g.getNodeIDs()

                /* The following finds the quotient of dividing the number
                of nodes with degree one by the total number of nodes. */
                .filter(nid -> (int) g.getIncidentEdges(nid).count() == 1)
                .count() / totalNodes;

        return totalNodes;
    }
}
