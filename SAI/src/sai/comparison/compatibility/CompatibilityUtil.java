package sai.comparison.compatibility;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;

import java.util.Set;

import sai.graph.Feature;

import com.google.common.collect.Sets;

public class CompatibilityUtil {

	/** checks to see if features have the same name and value */
	public static boolean areLexicallyCompatible(Feature a, Feature b) {
		return a.getName().equals(b.getName()) &&
						a.getValue().equals(b.getValue());
	}
	
	/** returns a compatibility checker which simply checks to see if 
	 * two features share a name and value.
	 * @return
	 */
	public static FeatureCompatibilityChecker lexicalChecker() {
		return new FeatureCompatibilityChecker() {

			@Override
			public boolean apply(Pair<Feature> args) {
				return areLexicallyCompatible(args.a1(), args.a2());
			}
			
		};
	}
	
	
    /** checks if each feature in set 1 is compatible with at least one
     * feature in set 2, allowing repeats.
     */
	public static FeatureSetCompatibilityChecker greedy1To1Checker(
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
	public static FeatureSetCompatibilityChecker many1To1Checker(
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
