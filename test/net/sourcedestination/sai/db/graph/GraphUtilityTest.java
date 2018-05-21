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
package net.sourcedestination.sai.db.graph;

import java.nio.file.AccessDeniedException;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;

import org.junit.*;

import com.google.common.collect.Sets;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static net.sourcedestination.sai.db.graph.SampleGraphs.assertGraphsAreIdentical;
import static net.sourcedestination.sai.db.graph.GraphSerializer.saiJsonEncode;
import static net.sourcedestination.sai.db.graph.GraphDeserializer.saiJsonDecode;

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
            Graph ng = g.copyWithoutEdge(MutableGraph::new, e);
            assertEquals(ng.getEdgeIDs().count(), g.getEdgeIDs().count()-1);
            assertEquals(ng.getNodeIDs().count(), g.getNodeIDs().count());
        });
    }
    
    @Test
    public void testCopyWithoutNode() throws AccessDeniedException {
        Graph g = SampleGraphs.getSmallGraph1();
        g.getNodeIDs().forEach(n-> {
            Graph ng = g.copyWithoutNode(MutableGraph::new, n);
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

    @Test
    public void testJSONCopy() throws AccessDeniedException {
        Graph g = SampleGraphs.getSmallGraph1();
    	assertGraphsAreIdentical(g, saiJsonDecode(saiJsonEncode(g)));
        g = SampleGraphs.getSmallGraph2();
    	assertGraphsAreIdentical(g, saiJsonDecode(saiJsonEncode(g)));
        g = SampleGraphs.getSmallGraph3();
    	assertGraphsAreIdentical(g, saiJsonDecode(saiJsonEncode(g)));
        g = SampleGraphs.getSmallGraph4();
    	assertGraphsAreIdentical(g, saiJsonDecode(saiJsonEncode(g)));
    }
    
}