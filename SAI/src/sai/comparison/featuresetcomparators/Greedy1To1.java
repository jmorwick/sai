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

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Pair;
import info.kendall_morwick.funcles.T2;

import java.util.Set;

import com.google.common.collect.Sets;

import sai.graph.jgrapht.Feature;

/**
 * A quadratic-worse-case comparison algorithm which does not exhaustively check
 * every possibility for compatibility, but rather consumes the first matching it
 * comes across.  This algorithm may produce false-negatives when compatibility of
 * a fixed feature isn't commutative.
 *
 * @author jmorwick
 * @version 0.3.0
 */
public class Greedy1To1 implements BinaryRelation<Set<? extends Feature>> {

    /** returns true if each feature in t1s is compatible with at least one
     * feature in t2s, allowing repeats.
     * @param t1s
     * @param t2s
     * @return
     */
    public boolean apply(Pair<Set<? extends Feature>> args) {
    	Set<? extends Feature> t1s = args.a1();
    	Set<? extends Feature> t2s = args.a2();
        t2s = Sets.newHashSet(t2s);
        for(Feature t1 : t1s) {
            boolean foundMatch = false;
            for(Feature t2 : t2s) {
                if(t1.compatible(t2)) {
                    foundMatch = true;
                    t2s.remove(t2);
                    break;
                }
            }
            if(!foundMatch) return false;
        }
        return true;
    }

}