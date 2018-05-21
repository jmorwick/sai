package net.sourcedestination.sai.experiment.compatibility;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.sourcedestination.sai.db.graph.Feature;

public interface FeatureSetCompatibilityCheckers {


	/** checks if each feature in set 1 is compatible with at least one
	 * feature in set 2, allowing repeats.
	 * Complexity:  O(|fs1| * |fs2|)
	 */
	public static boolean checkFeaturesGreedyManyTo1(FeatureCompatibilityChecker checker,
													 Set<Feature> fs1, Set<Feature> fs2) {
		return fs1.stream().allMatch(f1 -> fs2.stream()
				.anyMatch( f2 -> checker.apply(f1, f2)));
	}

	/** checks if each feature in set 1 is the same as at least one
	 * feature in set 2, allowing repeats.
	 * Complexity: O(|fs1| * ln(|fs2|))
	 */
	public static boolean checkFeaturesGreedyManyTo1(Set<Feature> fs1, Set<Feature> fs2) {
		return fs1.stream().allMatch(f1 -> fs2.contains(f1));
	}

	/** checks if each feature in set 1 is compatible with exactly one
	 * feature in set 2, allowing repeats.
	 * Complexity:  O(|fs1| * |fs2|)
	 */
	public static boolean checkFeaturesGreedy1To1(FeatureCompatibilityChecker checker,
												  Set<Feature> fs1, Set<Feature> fs2) {
		final Set<Feature> fs2copy = new HashSet<>(fs2);
		return fs1.stream().allMatch(f1 -> {
			Optional<Feature> res =
					fs2copy.stream().filter( f2 -> checker.apply(f1, f2)).findFirst();
			if(res.isPresent()) {
				fs2copy.remove(res.get());
			}
			return res.isPresent();
		});
	}
}
