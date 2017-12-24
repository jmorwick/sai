package net.sourcedestination.sai.comparison.compatibility;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.sai.graph.Feature;

public interface FeatureSetCompatibilityChecker extends Predicate2<Stream<Feature>,Stream<Feature>> {

	/** checks if each feature in set 1 is compatible with at least one
	 * feature in set 2, allowing repeats. fs2 is read and stored in memory.
	 * Complexity:  O(|fs1| * |fs2|)
	 */
	public static boolean checkFeaturesGreedyManyTo1(FeatureCompatibilityChecker checker,
													 Stream<Feature> fs1, Stream<Feature> fs2) {
		Set<Feature> fs2Set = fs2.collect(Collectors.toSet());
		return fs1.allMatch(f1 -> fs2Set.stream()
				.anyMatch( f2 -> checker.apply(f1, f2)));
	}

	/** checks if each feature in set 1 is the same as at least one
	 * feature in set 2, allowing repeats. fs2 is read and stored in memory.
	 * Complexity: O(|fs1| * ln(|fs2|))
	 */
	public static boolean checkFeaturesGreedyManyTo1(Stream<Feature> fs1, Stream<Feature> fs2) {
		Set<Feature> fs2Set = fs2.collect(Collectors.toSet());
		return fs1.allMatch(f1 -> fs2Set.contains(f1));
	}

	/** checks if each feature in set 1 is compatible with exactly one
	 * feature in set 2, allowing repeats. fs2 is read and stored in memory.
	 * Complexity:  O(|fs1| * |fs2|)
	 */
	public static boolean checkFeaturesGreedy1To1(FeatureCompatibilityChecker checker,
													 Stream<Feature> fs1, Stream<Feature> fs2) {
		Multiset<Feature> fs2Set = HashMultiset.create();
		fs2.forEach(f -> fs2Set.add(f));
		return fs1.allMatch(f1 -> {
			Optional<Feature> res =
					fs2Set.stream().filter( f2 -> checker.apply(f1, f2)).findFirst();
			if(res.isPresent()) {
				fs2Set.remove(res.get());
			}
			return res.isPresent();
		});
	}

	/** checks if each feature in set 1 is the same as exactly one
	 * feature in set 2, allowing repeats. fs2 is read and stored in memory.
	 * Complexity:  O(|fs1| * ln(|fs2|))
	 */
	public static boolean checkFeaturesGreedy1To1(Stream<Feature> fs1, Stream<Feature> fs2) {
		Multiset<Feature> fs2Set = HashMultiset.create();
		fs2.forEach(f -> fs2Set.add(f));
		return fs1.allMatch(f1 -> {
			if(fs2Set.contains(f1)) {
				fs2Set.remove(f1);
				return true;
			}
			return false;
		});
	}
}
