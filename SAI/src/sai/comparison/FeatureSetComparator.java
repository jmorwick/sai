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

package sai.comparison;

import sai.Feature;

/**
 * This class houses a method for comparing two feature-sets for compatibility.
 * If a feature is considered to be 'consumed' when it is chosen as a compatible
 * feature in the other feature-set, then this problem involves trade-offs
 * between computational complexity and solution accuracy.  Several
 * implementations of this class exist, each with their own unique advantages.
 *
 * @author jmorwick
 * @version 0.3.0
 */
public abstract class FeatureSetComparator {

/** determines whether or not the feature set t1s is compatible with t2s
 *
 * @param t1s
 * @param t2s
 * @return
 */
    public abstract boolean compareFeatures(
            Set<? extends Feature> t1s,
            Set<? extends Feature> t2s);

    /** determines whether t1s is compatible with t2s, considering only the
     * specified feature types.
     * @param t1s
     * @param t2s
     * @param featureTypes
     * @return
     */
    public boolean compatible(
            Set<? extends Feature> t1s,
            Set<? extends Feature> t2s,
            Class<? extends Feature> ... featureTypes) {
        t1s = Util.retainOnly(t1s, featureTypes);
        t2s = Util.retainOnly(t2s, featureTypes);
        return compareFeatures(t1s, t2s);
    }

}