package net.sourcedestination.sai.comparison.distance;

import net.sourcedestination.sai.comparison.matching.GraphMatching;
import net.sourcedestination.sai.comparison.matching.MatchingEvaluator;

/**
 * Created by jmorwick on 7/2/17.
 */
public class EdgeCountMatchingEvaluator implements MatchingEvaluator {
    public Double evaluateMatching(GraphMatching m) {
        return (double)m.getAllEdgeMatches().count() /
                (double)m.getGraph1().getEdgeIDs().count();
    }
}
