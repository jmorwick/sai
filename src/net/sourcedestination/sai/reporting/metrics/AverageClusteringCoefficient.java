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

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {

            // The following stores all of the neighboring nodes of node "n" in a set.
            Set<Integer> neighborsOfN = g.getIncidentFromEdges(n).map(g::getEdgeTargetNodeID).collect(Collectors.toSet());

            // The following finds "Ki", the degree of the current node "n".
            int degreeOfCurrentNode = neighborsOfN.size();

            // The following creates an empty atomic integer data structure instance.
            AtomicInteger numberOfNodeTriangles = new AtomicInteger();

            // The following finds "Li", the number of triangles formed by neighboring nodes of the current node "n".
            for (Integer firstNeighborOfN : neighborsOfN) {

                for (Integer secondNeighborOfN : neighborsOfN) {

                    // The following increases the number of node triangles found in the given graph "n".
                    if (g.areConnectedNodes(firstNeighborOfN, secondNeighborOfN))
                        numberOfNodeTriangles.incrementAndGet();

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
