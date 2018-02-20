package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* A DB statistic that computes the average
 local clustering coefficient for a given graph.
 Created by amorehead on 2/20/18. */
public class AverageClusteringCoefficient implements IndependentDBStatistic {

    @Override
    public double processGraph(Graph g) {

        // The following creates an atomic double for storing the number of triangles found in a given graph.
        AtomicReference<Double> totalTriangles = new AtomicReference<>((double) 0);

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {
            // The following accumulates the local clustering coefficient of node "n".
            int degreeOfCurrentNode = ((int) g.getIncidentEdges(n).count());
            int possibleTriangles = (degreeOfCurrentNode * (degreeOfCurrentNode - 1));
            AtomicInteger actualTriangles = new AtomicInteger();

            g.getIncidentEdges(n).forEach(u -> { // This represents the first adjacent edge to the current node "n".

                g.getIncidentEdges(n).forEach(w -> { // This represents the second adjacent edge to the current node "n".

                    /* The following checks to see if a given graph contains the
                     two previously-described adjacent edges to the current node. */
                    if (g.hasEdge(u) && g.hasEdge(w)) actualTriangles.getAndIncrement();

                });

            });

            // The following updates the total number of triangles found.
            if (possibleTriangles > 0)
                totalTriangles.updateAndGet(t -> t + (1.0 * actualTriangles.get() / possibleTriangles));
        });

        // The following returns the average local clustering coefficient of a given graph.
        return totalTriangles.get() / (g.getNodeIDs().count());

    }

}
