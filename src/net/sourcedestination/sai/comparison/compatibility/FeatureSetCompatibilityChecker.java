package net.sourcedestination.sai.comparison.compatibility;

import java.util.Set;

import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.sai.graph.Feature;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public interface FeatureSetCompatibilityChecker extends Predicate2<Set<Feature>,Set<Feature>> {

    /** checks if each feature in set 1 is compatible with at least one
     * feature in set 2, allowing repeats.
     */
	public static FeatureSetCompatibilityChecker greedy1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return (t1s, t2s) -> { 
	        Set<Feature> t2sThreadSafe = Sets.newConcurrentHashSet(t2s);
	        return t1s.stream().allMatch(
	        		f1 -> {
	        			return Sets.newHashSet(t2sThreadSafe).stream()
	        			.anyMatch( f2 -> {
	        				if(featureComp.test(f1, f2)) {
	        					t2sThreadSafe.remove(f2);
	        					return true;
	        				}
	        				return false;
	        			});
	        		}
	        );
	    };
	}
	

    /** checks if each feature in set 1 is compatible with at least one
     * feature in set 2, allowing repeats.
     */
	public static FeatureSetCompatibilityChecker many1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return (t1s, t2s) -> { 
	        Set<Feature> t2sThreadSafe = ImmutableSet.copyOf(t2s);
	        return t1s.stream().allMatch(
	        		f1 -> t2sThreadSafe.stream()
	        			.anyMatch(f2 -> featureComp.test(f1, f2))
	        );
	    };
	}
}
