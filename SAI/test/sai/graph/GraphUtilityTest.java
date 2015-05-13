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
package sai.graph;

import java.nio.file.AccessDeniedException;

import org.junit.*;

import com.google.common.collect.Sets;

import sai.db.DBInterface;
import sai.db.SampleDBs;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

/**
 *
 * @author jmorwick
 */
public class GraphUtilityTest {

    
    @Test
    public void testCopyWithoutEdge() throws AccessDeniedException {
    	DBInterface db = SampleDBs.getEmptyDB();
        Graph g = SampleGraphs.getSmallGraph1();
        g.getEdgeIDs().forEach(e-> {
            Graph ng = Graphs.copyWithoutEdge(g, MutableGraph::new, e);
            assertEquals(ng.getEdgeIDs().count(), g.getEdgeIDs().count()-1);
            assertEquals(ng.getNodeIDs().count(), g.getNodeIDs().count());
        });
    }
    
    @Test
    public void testCopyWithoutNode() throws AccessDeniedException {
    	DBInterface db = SampleDBs.getEmptyDB();
        Graph g = SampleGraphs.getSmallGraph1();
        g.getNodeIDs().forEach(n-> {
            Graph ng = Graphs.copyWithoutNode(g, MutableGraph::new, n);
            if(n == 1)
            	assertEquals(Sets.newHashSet(2, 3, 4), ng.getEdgeIDs().collect(toSet()));
            else if(n == 2)
            	assertEquals(Sets.newHashSet(3), ng.getEdgeIDs().collect(toSet()));
            else if(n == 3)
            	assertEquals(Sets.newHashSet(1, 4), ng.getEdgeIDs().collect(toSet()));
            else // node 4
            	assertEquals(Sets.newHashSet(1, 2), ng.getEdgeIDs().collect(toSet()));
            assertEquals(ng.getNodeIDs().count(), g.getNodeIDs().count()-1);
        });
    }
    
}