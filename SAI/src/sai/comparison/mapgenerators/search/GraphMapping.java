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

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;

import sai.graph.jgrapht.Node;

/**
 *A mapping between the components of two graphs.  This may be a 
 *partial mapping, may not be maximal, and may be annotated as an 
 *intermediate search state in a search for a more complete mapping.
 * 
 * @author jmorwick
 * @version 2.0.0
 */

//TODO: 1) remove this class, 2) extend beyond node map, 3) never map edges/etc?
//TODO: redo to base off of jgrapht's graph mapping
//TODO: look up comparison algorithms used with jgrapht- do best to make these compatible

@Deprecated public class GraphMapping extends HashMap<Node,Node> {
    private Multimap<Node,Node> possibilities;

    public GraphMapping() {
    	//leave possibilities null to signify no restrictions (all possibilities)
    }
    
    public GraphMapping(Map<Node,Node> m) {
    	super(m);
    }
    
    public GraphMapping(Multimap<Node,Node> possibilities) {
        this.possibilities = possibilities;
    }

    
    public GraphMapping(Map<Node,Node> m, Multimap<Node,Node> possibilities) {
    	super(m);
        this.possibilities = possibilities;
    }

    public Multimap<Node,Node> getPossibilities() { return possibilities; }

    /** potentially performs an operation that was too expensive to perform at allocation time for a child node */
    public void doOperation() {}
}