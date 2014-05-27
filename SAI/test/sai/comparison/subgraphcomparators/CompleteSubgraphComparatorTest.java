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
package sai.comparison.subgraphcomparators;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.T2;

import java.util.Iterator;
import java.util.List;

import sai.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Function;

import db.mysql.MySQLDBInterface;
import sai.comparison.Util;
import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;
import sai.indexing.Index;
import sai.indexing.retrievers.path.Path1Retriever;
import sai.maintenance.IndexCompatabilityChecker;
import sai.maintenance.IndexConsolidator;
import static org.junit.Assert.*;
import static sai.DBInterfaceTest.getTestDBInterface;

/**
 *
 * @author jmorwick
 */
public class CompleteSubgraphComparatorTest {



  public static IndexCompatabilityChecker getCompleteChecker(MySQLDBInterface db, long maxtime, int numThreads) {
      return new IndexCompatabilityChecker(db, maxtime, numThreads,
              new CompleteSubgraphComparator(db));
            
  }

    public CompleteSubgraphComparatorTest() {
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
    
    @Test
    public void testSmallComparison() {
        MySQLDBInterface db = getTestDBInterface();
        Graph g1 = new Graph(db);
        Node n11 = new Node(g1,db, new Feature("a",db));
        Node n12 = new Node(g1,db,new Feature("b",db));
        Graph g2 = new Graph(db);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        Node n21 = new Node(g2,db, new Feature("a",db));
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        Node n22 = new Node(g2,db,new Feature("b",db));
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        g1.addEdge(n11, n12);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        g2.addEdge(n21, n22);
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        g1.addEdge(n11, n12);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
        g2.addEdge(n21, n22);
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, Feature.class));
 
        Graph g3 = new Graph(db);
        Node n31 = new Node(g3,db);
        Node n32 = new Node(g3,db);

        assertTrue(CompleteSubgraphComparator.compare(db, g3, g2, Feature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, Feature.class));
        g3.addEdge(n31, n32);
        assertTrue(CompleteSubgraphComparator.compare(db, g3, g2, Feature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, Feature.class));
        n31.addFeature(new Feature("c",db));
        assertTrue(!CompleteSubgraphComparator.compare(db, g3, g2, Feature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, Feature.class));


        assertTrue(CompleteSubgraphComparator.compare(db,
                DBInterfaceTest.getSmallGraph1(db),
                DBInterfaceTest.getSmallGraph1(db), Feature.class));
        Graph g = DBInterfaceTest.getSmallGraph1(db);
        Node n1 = new Node(g,db);
        assertTrue(CompleteSubgraphComparator.compare(db,
                DBInterfaceTest.getSmallGraph1(db), g, Feature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g,
                DBInterfaceTest.getSmallGraph1(db), Feature.class));
    }
    
    @Test
    public void testBigComparison() {
    }

    @Test
    public void testInSystem() {
    final MySQLDBInterface db = getTestDBInterface();


    DBInterfaceTest.loadBasicDB(db);

    db.addRetriever(new Path1Retriever(db, Feature.class));

    IndexCompatabilityChecker checker = getCompleteChecker(db, 10000, 1000);
      assertEquals(2, db.getDatabaseSize());
      //TODO: add tests for list return value
      	List<T2<Integer,Integer>> collapsed = checker.get();

        Iterator<Graph> it = db.getStructureIterator();
        Graph lastGraph = null;
        while(it.hasNext()) {
            Graph g = it.next();
            db.indexGraph(g);
            lastGraph = g;
            assertEquals(4, db.findIndices(g).size());
        }
        assertEquals(10, db.getDatabaseSize());
        Iterator<Index> iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(!i.checkedForSubgraphRelationships());
            assertEquals(1, i.getIndexedGraphIDs().size());
            assertEquals(0, i.getSuperIndexIDs().size());
            assertTrue(CompleteSubgraphComparator.compare(db, i, i, Feature.class));
            assertTrue(CompleteSubgraphComparator.compare(db, i, lastGraph, Feature.class));
        }

        checker = getCompleteChecker(db, 10000, 1000);
      //TODO: add tests for list return value
      	collapsed = checker.get();
        assertEquals(10, db.getDatabaseSize());
        iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(i.checkedForSubgraphRelationships());
            assertEquals(1, i.getIndexedGraphIDs().size());
            assertEquals(1, i.getSuperIndexIDs().size());
        }

        IndexConsolidator ic = new IndexConsolidator(db);
        while(!ic.isDone()) ic.nextIteration();
        assertEquals(6, db.getDatabaseSize());
        iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(i.checkedForSubgraphRelationships());
            assertEquals(2, i.getIndexedGraphIDs().size());
            assertEquals(0, i.getSuperIndexIDs().size());
        }
    }

    @Test
    public void testInSystem2() {  //teasting the LinkedFeaturesLookup index generator
    final MySQLDBInterface db = getTestDBInterface();

    db.initializeDatabase();
    DBInterfaceTest.loadBasicDB2(db);

    IndexCompatabilityChecker checker = getCompleteChecker(db, 10000, 1000);
  //TODO: add tests for list return value
  	List<T2<Integer, Integer>> collapsed = checker.get();
        Iterator<Graph> it = db.getStructureIterator();
        while(it.hasNext()) {
            Graph g = it.next();
            db.indexGraph(g);
        }
        assertEquals(6, db.getDatabaseSize());
        Iterator<Index> iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(!i.checkedForSubgraphRelationships());
            assertEquals(2, i.getIndexedGraphIDs().size());  //indices should already be consolidated
            assertEquals(0, i.getSuperIndexIDs().size());
        }

        checker = getCompleteChecker(db, 10000, 1000);
      //TODO: add tests for list return value
      	collapsed = checker.get();
        assertEquals(6, db.getDatabaseSize());  //indices should already be consolidated
        iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(i.checkedForSubgraphRelationships());
            assertEquals(2, i.getIndexedGraphIDs().size());
            assertEquals(0, i.getSuperIndexIDs().size());
        }
        IndexConsolidator ic = new IndexConsolidator(db);
        while(!ic.isDone()) ic.nextIteration();
        assertEquals(6, db.getDatabaseSize());
        iit = db.getIndexIterator();
        while(iit.hasNext()) {
            Index i = iit.next();
            assertTrue(i.checkedForSubgraphRelationships());
            assertEquals(2, i.getIndexedGraphIDs().size());
            assertEquals(0, i.getSuperIndexIDs().size());
        }
    }


}