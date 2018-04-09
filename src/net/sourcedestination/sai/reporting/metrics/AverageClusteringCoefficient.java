package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Created by amorehead on 4/9/18. */
public class AverageClusteringCoefficient implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        // The following creates an atomic double for storing the number of triangles found in a given graph.
        AtomicReference<Double> clusteringCoefficients = new AtomicReference<>((double) 0);

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {
            // The following accumulates the local clustering coefficient of node "n".
            int degreeOfCurrentNode = ((int) g.getIncidentEdges(n).count());
            AtomicInteger numberOfNeighborEdgesOfInterest = new AtomicInteger();

            g.getNodeIDs().forEach(u -> { // This represents the first adjacent edge to the current node "n".

                g.getNodeIDs().forEach(w -> { // This represents the second adjacent edge to the current node "n".

                    /* The following checks to see if a given graph contains an edge
                     between the two previously-described adjacent nodes to the current node. */
                    if (g.areConnectedNodes(u, w))
                        numberOfNeighborEdgesOfInterest.getAndIncrement();
                });

            });

            // The following handles a divide-by-zero error while also calculating the clustering coefficient for a given node "n".
            double clusteringCoefficient = ((degreeOfCurrentNode - 1 != 0) && (degreeOfCurrentNode != 0)
                    && (numberOfNeighborEdgesOfInterest.get() - 1 != 0) && (numberOfNeighborEdgesOfInterest.get() != 0))
                    ? ((double) (2 * numberOfNeighborEdgesOfInterest.get())) / ((double) (degreeOfCurrentNode * (degreeOfCurrentNode - 1))) : 0;

            // The following updates the total number of clustering coefficients found.
            clusteringCoefficients.getAndUpdate(t -> t + clusteringCoefficient);
        });

        // The following returns the average clustering coefficient of a given graph "g".
        return clusteringCoefficients.get() / (g.getNodeIDs().count());

    }

}
