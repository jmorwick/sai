package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Updated by amorehead on 4/13/18. */
public class AverageClusteringCoefficient implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        // The following creates an atomic double for storing the number of triangles found in a given graph.
        AtomicReference<Double> clusteringCoefficients = new AtomicReference<>((double) 0);

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {

            // The following stores the degree of the current node "n".
            int degreeOfCurrentNode = ((int) g.getIncidentEdges(n).count());

            // The following creates an empty set to hold the neighbors of node "n".
            ArrayList<Integer> neighborsOfN = new ArrayList<>();

            // The following adds node neighbors to the previously-created set.
            g.getIncidentFromEdges(n).forEach(eid -> neighborsOfN.add(g.getEdgeSourceNodeID(eid)));

            // The following creates an empty atomic integer data structure instance.
            AtomicInteger numberOfNeighborEdgesOfInterest = new AtomicInteger();

            /*
            // The following increments the number of edges between neighboring nodes of the current node "n" (if applicable).
            for (int i = 0; i < neighborsOfN.size() - 1; i++) {

                if ((g.getIncidentToEdges(neighborsOfN.get(i)).anyMatch(g.getIncidentFromEdges(neighborsOfN.get(i + 1)))) ||

                        (g.getIncidentFromEdges(neighborsOfN.get(i)).anyMatch(g.getIncidentToEdges(neighborsOfN.get(i + 1))))) {

                    numberOfNeighborEdgesOfInterest.incrementAndGet();
                }
            }
            */

            // The following handles a divide-by-zero error while also calculating the clustering coefficient for a given node "n".
            double clusteringCoefficient = ((degreeOfCurrentNode != 0) && (degreeOfCurrentNode - 1 != 0))
                    ? ((double) (2 * numberOfNeighborEdgesOfInterest.get())) / ((double) (degreeOfCurrentNode * (degreeOfCurrentNode - 1)))
                    : 0;

            // The following updates the total number of clustering coefficients found.
            clusteringCoefficients.getAndUpdate(t -> t + clusteringCoefficient);
        });

        // The following returns the average clustering coefficient of a given graph "g".
        return clusteringCoefficients.get() / (g.getNodeIDs().count());

    }

}
