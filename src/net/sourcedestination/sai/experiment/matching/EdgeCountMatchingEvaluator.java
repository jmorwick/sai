package net.sourcedestination.sai.experiment.matching;

import net.sourcedestination.sai.db.graph.Graph;

/**
 * Created by jmorwick on 7/2/17.
 */
public class EdgeCountMatchingEvaluator<G extends Graph> implements MatchingEvaluator<G> {
    public Double apply(GraphMatching<G> m) {
        return (double)m.getAllEdgeMatches().count() /
                (double)m.getGraph1().getEdgeIDs().count();
    }
}
