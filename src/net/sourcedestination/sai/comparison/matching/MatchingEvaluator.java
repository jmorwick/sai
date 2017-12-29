package net.sourcedestination.sai.comparison.matching;

import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.graph.Graph;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.sourcedestination.sai.util.FunctionUtil.argmax;
import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;
/**
 * Created by jmorwick on 7/2/17.
 */
@FunctionalInterface
public interface MatchingEvaluator<G extends Graph> extends Function<GraphMatching<G>, Double> {

    // TODO: add logging for reports
    public static <G extends Graph> double getMapQuality(
            Stream<GraphMatching<G>> s,
            MatchingEvaluator<G> eval) {
        GraphMatching<G> best = argmax(eval, s);
        // TODO: log map
        return eval.apply(best);
    }

    // TODO: add logging for reports
    // TODO: test
    public static <G extends Graph> Stream<G> getClosestGraphs(G query,
                                                      Stream<G> graphs,
                                                      MatchingGenerator<G> gen,
                                                      MatchingEvaluator eval,
                                                      int limit) {
        return graphs
                .map(g -> makeTuple(g, getMapQuality(gen.apply(query, g), eval))) // pair with quality
                .sorted(Comparator.comparing(Tuple2::_2)) // sort by quality
                // TODO: log result w/ quality
                .map(Tuple2::_1)  // remove quality
                .limit(limit);  // take only the top 'limit' graphs
    }
}
