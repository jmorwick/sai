package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

/* A DB statistic that can be calculated by
 averaging the edges of a graph in a given database.
 Created by amorehead on 1/31/18. */
public class AverageDegreePerGraph implements IndependentDBStatistic {

    public double processGraph(Graph g) {

        // The following represents the processing of a stream of integers.
        return (g.getNodeIDs()

                /* The following finds the average number of edges in a graph by retrieving a stream
                 of integers representing a given graph's incidental edges. If the number of edges or
                 nodes in the graph is none, then the integer "0" is returned. */
                .mapToInt(nid -> (int) g.getIncidentEdges(nid).count()).average()).orElse(0);

    }

}