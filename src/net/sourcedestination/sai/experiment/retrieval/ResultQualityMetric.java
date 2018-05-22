package net.sourcedestination.sai.experiment.retrieval;

import net.sourcedestination.sai.db.graph.Graph;

import java.util.function.Function;

@FunctionalInterface
public interface ResultQualityMetric extends Function<Graph,Double> {

    static ResultQualityMetric createForGraphQuery(GraphSimilarityMetric sim, Graph query) {
        return new ResultQualityMetric() {
            @Override public Double apply(Graph graph) {
                return sim.apply(query, graph);
            }
        };
    }

    static Function<Graph,ResultQualityMetric> createFactoryForGraphQuery(GraphSimilarityMetric sim) {
        return new Function<Graph,ResultQualityMetric>() {
            @Override public ResultQualityMetric apply(Graph query) {
                return createForGraphQuery(sim, query);
            }
        };
    }
}
