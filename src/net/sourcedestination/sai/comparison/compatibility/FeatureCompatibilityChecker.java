package net.sourcedestination.sai.comparison.compatibility;

import net.sourcedestination.funcles.relation.Relation2;
import net.sourcedestination.sai.graph.Feature;

public interface FeatureCompatibilityChecker extends Relation2<Feature> {

	/** checks to see if features have the same name and value */
	public static boolean areLexicallyCompatible(Feature a, Feature b) {
		return a.getName().equals(b.getName()) &&
						a.getValue().equals(b.getValue());
	}
}
