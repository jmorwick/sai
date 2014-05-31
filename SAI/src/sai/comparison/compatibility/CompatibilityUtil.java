package sai.comparison.compatibility;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;

import java.util.Set;

import sai.graph.Feature;

import com.google.common.collect.Sets;

public class CompatibilityUtil {

	/** returns a compatibility checker which simply checks to see if 
	 * two features share a name and value.
	 * @return
	 */
	public static FeatureCompatibilityChecker lexicalChecker() {
		return new FeatureCompatibilityChecker() {

			@Override
			public boolean apply(Pair<Feature> args) {
				return args.a1().getName().equals(args.a2().getName()) &&
						args.a1().getValue().equals(args.a2().getValue());
			}
			
		};
	}
	
	
    /** checks if each feature in set 1 is compatible with at least one
     * feature in set 2, allowing repeats.
     */
	public static FeatureSetCompatibilityChecker Greedy1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return new FeatureSetCompatibilityChecker() {

	    public boolean apply(Pair<Set<Feature>> args) {
	    	Set<Feature> t1s = args.a1();
	    	Set<Feature> t2s = args.a2();
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
	    }

		};
	}
	

    /** 
     * returns a quadratic-worst-case feature-set comparison algorithm which 
     * allows one feature to subsume unlimited features in the other feature set. 
     */
	public static FeatureSetCompatibilityChecker Many1To1Checker(
			final FeatureCompatibilityChecker featureComp) {
		return new FeatureSetCompatibilityChecker() {

	    public boolean apply(Pair<Set<Feature>> args) {
	    	Set<Feature> t1s = args.a1();
	    	Set<Feature> t2s = args.a2();
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

		}
	    };
	}
}
