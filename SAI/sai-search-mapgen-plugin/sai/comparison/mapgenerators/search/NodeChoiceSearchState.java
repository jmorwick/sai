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

import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.Node;

/**
 * An intermediate search state in which a single node to be mapped is chosen,
 * limiting the breadth of expansion. 
 *
 * @author jmorwick
 * @version 2.0.0
 */
public class NodeChoiceSearchState extends GraphMapping {
    private final Node n;

    public NodeChoiceSearchState(Map<Node,Node> m, 
            Multimap<Node,Node> possibilities, Node n) {
        super(m,possibilities);
        this.n = n;
    }

    public Node getNodeToMap() { return n; }

}