package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

/* A DB metric that computes the
 total number of nodes in a given graph.
 Created by amorehead on 3/2/18. */
public class TotalNodesPerGraph implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        // The following initializes a double variable for later use.
        double totalNodes;

        // The following represents the processing of a stream of integers.
        totalNodes = g.getNodeIDs()

                // The following finds the total number of nodes in a given graph.
                .count();

        return totalNodes;
    }
}