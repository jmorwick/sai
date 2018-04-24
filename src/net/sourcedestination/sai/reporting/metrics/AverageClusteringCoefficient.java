package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Updated by amorehead on 4/24/18. */
public class AverageClusteringCoefficient implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        /* The following creates a double array for storing
         a running total of nodes' clustering coefficients. */
        final double[] clusteringCoefficients = {0};

        // The following creates an empty atomic set of integers for use in the proceeding forEach loop.
        AtomicReference<Set<Integer>> neighborsOfN = new AtomicReference<>();

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {

            // The following stores all of the neighboring nodes of node "n" in a set.
            neighborsOfN.set(g.getIncidentFromEdges(n).map(g::getEdgeSourceNodeID).collect(Collectors.toSet()));

            // The following finds "Kn", the degree of the current node "n".
            int degreeOfCurrentNode = neighborsOfN.get().size();

            /* The following creates an integer instance to represent "Ln",
             the number of edges between the "Kn" neighbors of node "n". */
            int numberOfNodeTriangles = 0;

            // The following finds "Ln" for a given node "n".
            for (int firstNeighborOfN : neighborsOfN.get()) {

                for (int secondNeighborOfN : neighborsOfN.get()) {

                    // The following increases the number of node triangles found at current node "n" in the given graph "g".
                    if (g.areConnectedNodes(firstNeighborOfN, secondNeighborOfN) && firstNeighborOfN != secondNeighborOfN) {
                        numberOfNodeTriangles++;
                    }

                    /* The break here is necessary to ensure that "numberOfNodeTriangles" is not
                     double-incremented (ex: [3, 2] & [2, 3] would each increment the counter otherwise). */
                    break;

                }

            }

            // The following handles a divide-by-zero error while also calculating the clustering coefficient for a given node "n".
            double clusteringCoefficient = ((degreeOfCurrentNode != 0) && (degreeOfCurrentNode - 1 != 0))
                    ? ((double) (2 * numberOfNodeTriangles)) / ((double) (degreeOfCurrentNode * (degreeOfCurrentNode - 1)))
                    : 0;

            // The following updates the total number of clustering coefficients found.
            clusteringCoefficients[0] += clusteringCoefficient;
        });

        // The following returns the average clustering coefficient of a given graph "g".
        return clusteringCoefficients[0] / (g.getNodeIDs().count());

    }

}
