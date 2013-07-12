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
package org.dataandsearch.sai;

import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.Set;
import info.kendallmorwick.util.function.Function;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author jmorwick
 */
public class GraphTest {
    
    @Test
    public void testAddEdge_Node_Node() {
    }

    @Test
    public void testAddEdge_3args() {
    }

    @Test
    public void testSaveToDatabase() {
    }

    @Test
    public void testGetFeatures() {
    }
    
    @Test
    public void testSetAlternateID() {
    }

    @Test
    public void testGetID() {
    }

    @Test
    public void testGetNode_int() {
    }

    @Test
    public void testGetNode_String() {
    }

    @Test
    public void testAddFeature() {
    }

    @Test
    public void testGetLinkedFromNodes() {
    }

    @Test
    public void testGetLinkedToNodes() {
    }
    
    @Test
    public void testGetFringe() {
        
    }

    @Test
    public void testLinkedTo() {
    }
    
    @Test
    public void testLinkBetween() {
        
    }
    
    @Test
    public void testGetUnusedEdgeID() {
        
    }
    
    @Test
    public void testGetUnusedNodeID() {
        
    }
    
    @Test
    public void testVertexSet() {
        
    }
    
    @Test
    public void testEdgeSet() {
        
    }
    
    @Test
    public void testCopy() {
        
    }
    
    @Test
    public void testCopyWithoutNode() {
        
    }

    @Test
    public void testCopyWithoutEdge() {
        DBInterface db = DBInterfaceTest.getTestDBInterface();
        db.initializeDatabase();
        Graph g = DBInterfaceTest.getSmallGraph1(db);
        for(Edge e : g.edgeSet()) {
            Graph ng = g.copyWithoutEdge(e);
            assertEquals(ng.edgeSet().size(), g.edgeSet().size()-1);
            assertEquals(ng.vertexSet().size(), g.vertexSet().size());
        }
    }
    
    @Test
    public void testAllPairsShortestPaths_0args() {
        
    }
    
    @Test
    public void testAllPairsShortestPaths_Function() {
        
    }
    
    @Test
    public void testToString() {
        
    }
    
    @Test
    public void testEquals() {
        
    }
    
    @Test
    public void testHashCode() {
        
    }
    
    @Test
    public void testGetDB() {
        
    }
    
    @Test
    public void testGetFeature() {
        
    }
}