package net.sourcedestination.sai.analysis.metrics;

import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.analysis.GraphMetric;

import java.util.stream.Collectors;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Updated by amorehead on 4/24/18. */
public class ClusteringCoefficient implements GraphMetric {

    @Override
    public Double apply(Graph g) {

        /* The following creates a double array for storing
         a running total of nodes' clustering coefficients. */
        final double[] clusteringCoefficients = {0};

        // The following iterates through each node in a given graph.
        g.getNodeIDs().forEach(n -> {

            // The following stores all of the neighboring nodes of node "n" in a set.
            var neighborsOfN = g.getIncidentFromEdges(n).map(g::getEdgeSourceNodeID).collect(Collectors.toSet());

            // The following finds "Kn", the degree of the current node "n".
            var degreeOfCurrentNode = neighborsOfN.size();

            /* The following creates an integer instance to represent "Ln",
             the number of edges between the "Kn" neighbors of node "n". */
            var numberOfNodeTriangles = 0;

            // The following finds "Ln" for a given node "n".
            for (var firstNeighborOfN : neighborsOfN) {

                for (var secondNeighborOfN : neighborsOfN) {

                    // The following increases the number of node triangles found at current node "n" in the given graph "g".
                    if (g.areConnectedNodes(firstNeighborOfN, secondNeighborOfN)
                            && firstNeighborOfN != secondNeighborOfN) {
                        numberOfNodeTriangles++;
                    }

                }

            }

            /* The following takes into account the possibility of
             double-counting node triangles by making an adjustment (if necessary). */
            if (numberOfNodeTriangles > 0) numberOfNodeTriangles /= 2;

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
