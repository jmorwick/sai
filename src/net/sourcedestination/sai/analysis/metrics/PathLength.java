package net.sourcedestination.sai.analysis.metrics;

import net.sourcedestination.sai.analysis.GraphMetric;
import net.sourcedestination.sai.db.graph.Graph;

public class PathLength implements GraphMetric {
    @Override
    public Double apply(Graph g) {
        return g.allPaths()
                .mapToDouble(path -> (path.size()-1)/2)
                .average().getAsDouble();
    }
}
