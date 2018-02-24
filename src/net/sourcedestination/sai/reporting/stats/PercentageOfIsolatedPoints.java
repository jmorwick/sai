package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

import java.util.logging.Logger;

/* A DB metric that finds the ratio of isolated nodes, those with
 degree zero, to the total number of nodes in the entire graph.
 Created by amorehead on 2/9/18. */
public class PercentageOfIsolatedPoints implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {
        Logger log = Logger.getLogger(UniqueEdgesPerGraph.class.getName());
        double totalNodes = (g.getNodeIDs().count());

        // The following is a simple check to see if there are any nodes in a given graph.
        if (totalNodes == 0.0) return totalNodes;

        // The following represents the processing of a stream of integers.
        totalNodes = (g.getNodeIDs()
                /* The following finds the quotient of dividing the number
                of nodes with degree zero by the total number of nodes. */
                .filter(nid -> g.getIncidentEdges(nid).count() == 0.0)
                .count() / totalNodes);

        log.info("Percentage of isolated points: " + totalNodes + " (currently not displaying in WebLab)");
        return totalNodes;
    }
}
