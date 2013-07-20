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

package sai.comparison;

import java.util.Map;

import sai.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import static org.junit.Assert.*;

/**
 *
 * @author jmorwick
 */
public class UtilTest {

    final DBInterface db = DBInterfaceTest.getTestDBInterface();

    public UtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compareFeatures method, of class Util.
     */
    @Test
    public void testCompareFeatures() {
        Graph g = new Graph(db);
        Node n1 = new Node(g, new GenericFeature("a", db));
        Node n2 = new Node(g, new GenericFeature("b", db));
        Node n3 = new Node(g, new GenericFeature("b", db));
        assertFalse(db.featureSetsCompatible(
                Sets.newHashSet(new GenericFeature("a",db)),
                Sets.newHashSet(new GenericFeature("b",db))));
        assertTrue(n1.compatible(n2));
        assertFalse(n1.compatible(n2));
        assertFalse(n1.compatible(n2));
        assertTrue(n1.compatible(n1));
        assertTrue(n2.compatible(n3));
    }


    /**
     * Test of nodeCompatibility method, of class Util.
     */
    @Test
    public void testNodeCompatibility() {
        Graph g1 = DBInterfaceTest.getSmallGraphMultiEdge(db);
        Graph g2 = DBInterfaceTest.getSmallGraphMultiEdge(db);
        Node n11 = g1.getNode(1);
        Node n21 = g2.getNode(1);
        Node n12 = g1.getNode(2);
        Node n22 = g2.getNode(2);
        Node n13 = g1.getNode(3);
        Node n23 = g2.getNode(3);
        Node n14 = g1.getNode(4);
        Node n24 = g2.getNode(4);
        Multimap<Node,Node> m = Util.nodeCompatibility(g1, g2);
        assertTrue(m.containsEntry(n11, n21));
        assertTrue(m.containsEntry(n11, n23));
        assertTrue(m.containsEntry(n12, n22));
        assertTrue(m.containsEntry(n13, n23));
        assertTrue(m.containsEntry(n13, n21));
        assertTrue(m.containsEntry(n14, n24));
        assertFalse(m.containsEntry(n11, n24));
        assertFalse(m.containsEntry(n11, n22));
        assertFalse(m.containsEntry(n12, n21));
        assertFalse(m.containsEntry(n12, n23));
        assertFalse(m.containsEntry(n13, n22));
        assertFalse(m.containsEntry(n13, n24));
        assertFalse(m.containsEntry(n14, n21));
        assertFalse(m.containsEntry(n14, n22));
        assertFalse(m.containsEntry(n14, n23));
    }

    /**
     * Test of matchedEdges method, of class Util.
     */
    @Test
    public void testMatchedEdges() {
        Graph g1 = DBInterfaceTest.getSmallGraphMultiEdge(db);
        Graph g2 = DBInterfaceTest.getSmallGraphMultiEdge(db);
        Node n11 = g1.getNode(1);
        Node n21 = g2.getNode(1);
        Node n12 = g1.getNode(2);
        Node n22 = g2.getNode(2);
        Node n13 = g1.getNode(3);
        Node n23 = g2.getNode(3);
        Node n14 = g1.getNode(4);
        Node n24 = g2.getNode(4);
        Map<Node,Node> m = Maps.newHashMap();
        m.put(n12, n22);
        m.put(n11, n21);
        assertEquals(3, Util.matchedEdges(g1, g2, n11, m, Util.completeEdgeMatchCounter));
        assertEquals(3, Util.matchedEdges(g1, g2, n12, m, Util.completeEdgeMatchCounter));
        assertEquals(0, Util.matchedEdges(g1, g2, n13, m, Util.completeEdgeMatchCounter));
        assertEquals(0, Util.matchedEdges(g1, g2, n14, m, Util.completeEdgeMatchCounter));
        assertEquals(3, Util.matchedEdges(g1, g2, m, Util.completeEdgeMatchCounter));

        m.put(n14, n24);
        m.put(n13, n23);
        assertEquals(4, Util.matchedEdges(g1, g2, n11, m, Util.completeEdgeMatchCounter));
        assertEquals(5, Util.matchedEdges(g1, g2, n12, m, Util.completeEdgeMatchCounter));
        assertEquals(1, Util.matchedEdges(g1, g2, n13, m, Util.completeEdgeMatchCounter));
        assertEquals(2, Util.matchedEdges(g1, g2, n14, m, Util.completeEdgeMatchCounter));
        assertEquals(6, Util.matchedEdges(g1, g2, m, Util.completeEdgeMatchCounter));
    }




}