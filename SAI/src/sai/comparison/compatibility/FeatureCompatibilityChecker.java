package sai.comparison.compatibility;

import sai.graph.Feature;
import info.kendall_morwick.relation.Relation2;

public interface FeatureCompatibilityChecker extends Relation2<Feature> {

	/** checks to see if features have the same name and value */
	public static boolean areLexicallyCompatible(Feature a, Feature b) {
		return a.getName().equals(b.getName()) &&
						a.getValue().equals(b.getValue());
	}
}
