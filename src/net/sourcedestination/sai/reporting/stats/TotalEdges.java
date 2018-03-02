package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

/* A DB metric that computes the
 total number of edges in a given graph.
 Created by amorehead on 3/2/18. */
public class TotalEdges implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        // The following initializes a double variable for later use.
        double totalEdges;

        // The following represents the processing of a stream of integers.
        totalEdges = g.getEdgeIDs()

                // The following finds the total number of edges in a given graph.
                .count();

        return totalEdges;
    }
}