package net.sourcedestination.sai.experiment.compatibility;

import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.sai.db.graph.Feature;

@FunctionalInterface
public interface FeatureCompatibilityChecker extends Predicate2<Feature,Feature> {

	/** checks to see if features have the same name and value */
	public static boolean areLexicallyCompatible(Feature a, Feature b) {
		return a.getName().equals(b.getName()) &&
						a.getValue().equals(b.getValue());
	}
}
