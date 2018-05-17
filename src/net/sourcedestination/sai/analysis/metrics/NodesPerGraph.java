package net.sourcedestination.sai.analysis.metrics;

import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.analysis.GraphMetric;

/* A graph metric that computes the
 total number of nodes in a given graph.
 Updated by amorehead on 4/13/18. */
public class NodesPerGraph implements GraphMetric {

    @Override
    public Double apply(Graph g) {

        // The following initializes a double variable for later use.
        double totalNodes;

        // The following represents the processing of a stream of integers.
        totalNodes = g.getNodeIDs()

                // The following finds the total number of nodes in a given graph.
                .count();

        return totalNodes;
    }
}