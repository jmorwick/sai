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

import java.util.Map;

import com.google.common.collect.Multimap;

import sai.Node;

/**
 *A state within a search for mappings between two graphs.
 * 
 * @author jmorwick
 * @version 2.0.0
 */
public class SearchState {
    private Map<Node, Node> m;
    private Multimap<Node,Node> possibilities;

    public SearchState(Map<Node,Node> m, Multimap<Node,Node> possibilities) {
        this.m = m;
        this.possibilities = possibilities;
    }

    public Map<Node,Node> getMap() { return m; }

    public Multimap<Node,Node> getPossibilities() { return possibilities; }

    /** potentially performs an operation that was too expensive to perform at allocation time for a child node */
    public void doOperation() {}
}