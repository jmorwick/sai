package net.sourcedestination.sai.comparison.matching;

import net.sourcedestination.sai.comparison.matching.GraphMatching;
import net.sourcedestination.sai.graph.Graph;

import java.util.function.Function;

/**
 * Created by jmorwick on 7/2/17.
 */
@FunctionalInterface
public interface MatchingEvaluator<G extends Graph> extends Function<GraphMatching<G>, Double> {
}
