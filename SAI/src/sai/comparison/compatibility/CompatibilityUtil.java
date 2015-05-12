package sai.comparison.compatibility;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.tuple.Pair;

import java.util.Set;

import sai.graph.Feature;

import com.google.common.collect.Sets;

public class CompatibilityUtil {

	/** checks to see if features have the same name and value */
	public static boolean areLexicallyCompatible(Feature a, Feature b) {
		return a.getName().equals(b.getName()) &&
						a.getValue().equals(b.getValue());
	}
	
	
    /** checks if each feature in set 1 is compatible with at least one
     * feature in set 2, allowing repeats.
     */
	public static FeatureSetCompatibilityChecker greedy1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return (t1s, t2s) -> { // TODO: use streams below
	        t2s = Sets.newHashSet(t2s);
	        for(Feature t1 : t1s) {
	            boolean foundMatch = false;
	            for(Feature t2 : t2s) {
	                if(Funcles.apply(featureComp, t1, t2)) {
	                    foundMatch = true;
	                    t2s.remove(t2);
	                    break;
	                }
	            }
	            if(!foundMatch) return false;
	        }
	        return true;
	    };
	}
	

    /** 
     * returns a quadratic-worst-case feature-set comparison algorithm which 
     * allows one feature to subsume unlimited features in the other feature set. 
     */
	public static FeatureSetCompatibilityChecker many1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return (t1s, t2s) -> { // TODO: use streams below
	        for(Feature t1 : t1s) {
	            boolean foundMatch = false;
	            for(Feature t2 : t2s) {
	                if(Funcles.apply(featureComp, t1, t2)) {
	                    foundMatch = true;
	                    break;
	                }
	            }
	            if(!foundMatch) return false;
	        }
	        return true;
		};
	}
}
