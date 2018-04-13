package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/* A DB metric that computes the local average
 clustering coefficient for a given graph.
 Updated by amorehead on 4/13/18. */
public class AverageClusteringCoefficient implements IndependentDBMetric {

    // The following creates a global counter variable for later use in a for-loop.
    public int i = 0;

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

            // The following increments the number of edges between neighboring nodes of the current node "n" (if applicable).
            for (i = 0; i < neighborsOfN.size() - 1; i++) {

                // The following stores relevant streams of Integers in temporary variables.
                Stream<Integer> edgesFromI = g.getIncidentToEdges(neighborsOfN.get(i));
                Stream<Integer> edgesToI = g.getIncidentFromEdges(neighborsOfN.get(i + 1));
                Stream<Integer> edgesFromNeighbor = g.getIncidentFromEdges(neighborsOfN.get(i));
                Stream<Integer> edgesToNeighbor = g.getIncidentFromEdges(neighborsOfN.get(i));

                /* Currently, the following is checking each Integer in the first stream
                 and seeing if any are equal to the **second stream in and of itself**.
                 This needs to be revised to check to see if any edge (Integer in this case)
                 in either of the two Integer streams are equal to each other. */
                if (edgesFromI.anyMatch(eid -> eid.equals(edgesToI)) ||
                        (edgesFromNeighbor.anyMatch(eid -> eid.equals(edgesToNeighbor)))) {

                    numberOfNeighborEdgesOfInterest.incrementAndGet();
                }
            }

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
