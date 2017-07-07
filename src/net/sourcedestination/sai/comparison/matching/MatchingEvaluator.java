package net.sourcedestination.sai.comparison.matching;

/**
 * Created by jmorwick on 7/2/17.
 */
@FunctionalInterface
public interface MatchingEvaluator<M extends GraphMatching> {
    public Double evaluateMatching(M m);
}
