package net.sourcedestination.sai.experiment.compatibility;

import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.funcles.predicate.Predicate4;
import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;

import java.util.Set;


/**
 * Created by jmorwick on 12/24/17.
 */
@FunctionalInterface
public interface EdgeCompatibilityChecker<G extends Graph> extends Predicate4<G,G,Integer,Integer> {

    public static EdgeCompatibilityChecker<Graph> useGenericFeatureChecker(
            Predicate2<Set<Feature>, Set<Feature>> featureSetChecker) {
        return (g1, g2, nid1, nid2) ->
                featureSetChecker.apply(g1.getEdgeFeaturesSet(nid1), g2.getEdgeFeaturesSet(nid2));
    }
}
