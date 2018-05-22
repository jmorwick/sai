package net.sourcedestination.sai.experiment.retrieval;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.indexing.GraphIndexGenerator;

@FunctionalInterface
public interface GraphSimilarityMetric extends Function2<Graph,Graph,Double> {

    static <I> GraphSimilarityMetric indexCountSimilarity(GraphIndexGenerator<I> gen) {
            return new GraphSimilarityMetric() {
                @Override
                public Double apply(Graph g1, Graph g2) {
                    Multiset<I> g2indexes = HashMultiset.create();
                    gen.apply(g2).forEach(g2indexes::add);
                    double size = g2indexes.size();
                    long found = gen.apply(g1).filter( i -> g2indexes.remove(i)).count();
                    return found / size;
                }
            };
    }
}
