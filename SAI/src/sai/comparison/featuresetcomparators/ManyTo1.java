/* Copyright 2011-2013 Joseph Kendall-Morwick

This file is part of SAI: The Structure Access Interface.

SAI is free software: you can redistribute it and/or modify
it under the terms of the Lesser GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SAI is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
Lesser GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License
along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */
package sai.comparison.featuresetcomparators;

import sai.Feature;
import sai.comparison.FeatureSetComparator;

/**
 * A quadratic-worst-case feature-set comparison algorithm which allows one
 * feature to subsume unlimited features in the other feature set. 
 *
 * @author jmorwick
 * @version 2.0.0
 */
public class ManyTo1 extends FeatureSetComparator {

    /** returns true if each feature in t1s is subsumed by at least one
     * feature in t2s, allowing repeats.  
     * @param t1s
     * @param t2s
     * @return
     */
    public boolean compareFeatures(
            Set<? extends Feature> t1s,
            Set<? extends Feature> t2s) {
        for(Feature t1 : t1s) {
            boolean foundMatch = false;
            for(Feature t2 : t2s) {
                if(t1.compatible(t2)) {
                    foundMatch = true;
                    break;
                }
            }
            if(!foundMatch) return false;
        }
        return true;
    }

}