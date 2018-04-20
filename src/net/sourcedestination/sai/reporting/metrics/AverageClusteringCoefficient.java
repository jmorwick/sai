package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Updated by amorehead on 4/20/18. */
public class AverageClusteringCoefficient implements IndependentDBMetric {

    @Override
    public double processGraph(Graph g) {

        // The following creates an atomic double for storing the number of triangles found in a given graph.
        AtomicReference<Double> clusteringCoefficients = new AtomicReference<>((double) 0);

        // The following creates an empty atomic set of integers for use in the proceeding forEach loop.
        AtomicReference<Set<Integer>> neighborsOfN = new AtomicReference<>();

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {

            // The following stores all of the neighboring nodes of node "n" in a set.
            neighborsOfN.set(g.getIncidentFromEdges(n).map(g::getEdgeSourceNodeID).collect(Collectors.toSet()));

            // The following finds "Kn", the degree of the current node "n".
            int degreeOfCurrentNode = neighborsOfN.get().size();

            /* The following creates an empty atomic integer instance to represent "Ln",
             the number of edges between the "Kn" neighbors of node "n". */
            AtomicInteger numberOfNodeTriangles = new AtomicInteger();

            // The following finds "Ln" for a given node "n".
            for (Integer firstNeighborOfN : neighborsOfN.get()) {

                for (Integer secondNeighborOfN : neighborsOfN.get()) {

                    // The following increases the number of node triangles found in the given graph "n".
                    if (g.areConnectedNodes(firstNeighborOfN, secondNeighborOfN) && !firstNeighborOfN.equals(secondNeighborOfN))
                        numberOfNodeTriangles.incrementAndGet();
                    /* The break here is necessary to ensure that "numberOfNodeTriangles" is not double-incremented
                     (ex: [3, 2] & [2, 3] would each increment the counter otherwise). */
                    break;
                }

            }

            // The following handles a divide-by-zero error while also calculating the clustering coefficient for a given node "n".
            double clusteringCoefficient = ((degreeOfCurrentNode != 0) && (degreeOfCurrentNode - 1 != 0))
                    ? ((double) (2 * numberOfNodeTriangles.get())) / ((double) (degreeOfCurrentNode * (degreeOfCurrentNode - 1)))
                    : 0;

            // The following updates the total number of clustering coefficients found.
            clusteringCoefficients.getAndUpdate(t -> t + clusteringCoefficient);
        });

        // The following returns the average clustering coefficient of a given graph "g".
        return clusteringCoefficients.get() / (g.getNodeIDs().count());

    }

}
