package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.graph.Graph;

/* A DB statistic still in development.
 Created by amorehead on 2/13/18. */
public class AverageClusteringCoefficient implements IndependentDBStatistic {

    @Override
    public double processGraph(Graph g) {
        int totalNodes = ((int) g.getNodeIDs().count());
        // The following represents the processing of a stream of integers.
        return totalNodes == 0 ? 0.0 :
                g.getNodeIDs()
                        .filter(nid -> (double) g.getIncidentEdges(nid).count() == 0)
                        .count() / (double) totalNodes;
    }

}
