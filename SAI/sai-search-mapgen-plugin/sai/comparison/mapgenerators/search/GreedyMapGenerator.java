/* Copyright 2011 Joseph Kendall-Morwick

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
package sai.comparison.mapgenerators.search;

import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Node;

import com.google.common.collect.Multimap;

/**
 * Performs a BFS search with no backtracking. 
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class GreedyMapGenerator extends SearchMapGenerator {

    public GreedyMapGenerator(
            GraphMatchingHeuristic h,
            Class<? extends Feature>... featureTypes) {
        super(new OneNodeAtATimeQueue(h, featureTypes) {
        //super(new HeuristicPriorityQueue(h, featureTypes) {

            @Override
            public void expand(GraphMapping state, Multimap<Node, Node> possibilities) {
                super.expand(state, possibilities);

                //remove everything but the top-rated map
                if (getQueue().size() > 1) {
                    GraphMapping s = getQueue().peek();
                    getQueue().clear();
                    getQueue().add(s);
                }
            }
        });
    }
}