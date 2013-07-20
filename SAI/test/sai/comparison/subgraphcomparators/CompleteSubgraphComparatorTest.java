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
\
import java.util.Iterator;
import sai.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sai.comparison.Util;
import sai.indexing.Index;
import sai.indexing.retrievers.path.Path1Retriever;
import sai.maintenance.IndexConsolidator;
import static org.junit.Assert.*;

import static sai.DBInterfaceTest.getTestDBInterface;

/**
 *
 * @author jmorwick
 */
public class CompleteSubgraphComparatorTest {



  public static IndexCompatibilityChecker getCompleteChecker(final DBInterface db, long maxtime, long timeInc) {
      return new IndexCompatibilityChecker(db, maxtime, timeInc,
              new Function<SubgraphComparator,T2<Graph,Graph>>() {

            @Override
            public SubgraphComparator implementation(T2<Graph,Graph> args) {
                return new CompleteSubgraphComparator(db, args.a1(), args.a2(), GenericFeature.class);
            }
        });
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
        DBInterface db = getTestDBInterface();
        Graph g1 = new Graph(db);
        Node n11 = new Node(g1,db, new GenericFeature("a",db));
        Node n12 = new Node(g1,db,new GenericFeature("b",db));
        Graph g2 = new Graph(db);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        Node n21 = new Node(g2,db, new GenericFeature("a",db));
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        Node n22 = new Node(g2,db,new GenericFeature("b",db));
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        g1.addEdge(n11, n12);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        g2.addEdge(n21, n22);
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        g1.addEdge(n11, n12);
        assertTrue(!CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
        g2.addEdge(n21, n22);
        assertTrue(CompleteSubgraphComparator.compare(db, g1, g2, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g2, g1, GenericFeature.class));
 
        Graph g3 = new Graph(db);
        Node n31 = new Node(g3,db);
        Node n32 = new Node(g3,db);

        assertTrue(CompleteSubgraphComparator.compare(db, g3, g2, GenericFeature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, GenericFeature.class));
        g3.addEdge(n31, n32);
        assertTrue(CompleteSubgraphComparator.compare(db, g3, g2, GenericFeature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, GenericFeature.class));
        n31.addFeature(new GenericFeature("c",db));
        assertTrue(!CompleteSubgraphComparator.compare(db, g3, g2, GenericFeature.class));
        assertTrue(!CompleteSubgraphComparator.compare(db, g2, g3, GenericFeature.class));


        assertTrue(CompleteSubgraphComparator.compare(db,
                DBInterfaceTest.getSmallGraph1(db),
                DBInterfaceTest.getSmallGraph1(db), GenericFeature.class));
        Graph g = DBInterfaceTest.getSmallGraph1(db);
        Node n1 = new Node(g,db);
        assertTrue(CompleteSubgraphComparator.compare(db,
                DBInterfaceTest.getSmallGraph1(db), g, GenericFeature.class));
        assertTrue(CompleteSubgraphComparator.compare(db, g,
                DBInterfaceTest.getSmallGraph1(db), GenericFeature.class));
    }
    
    @Test
    public void testBigComparison() {
    }

    @Test
    public void testInSystem() {
    final DBInterface db = getTestDBInterface();


    DBInterfaceTest.loadBasicDB(db);

    db.addRetriever(new Path1Retriever(db, GenericFeature.class));

      IndexCompatibilityChecker checker = getCompleteChecker(db, 10000, 1000);
      assertEquals(2, db.getDatabaseSize());
        while(!checker.isDone()) {  //check db with no indices
            checker.nextIteration();
        }

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
            assertTrue(CompleteSubgraphComparator.compare(db, i, i, GenericFeature.class));
            assertTrue(CompleteSubgraphComparator.compare(db, i, lastGraph, GenericFeature.class));
        }

        checker = getCompleteChecker(db, 10000, 1000);
        while(!checker.isDone()) {  //check db with no indices
            checker.nextIteration();
        }
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
    final DBInterface db = getTestDBInterface();

    db.initializeDatabase();
    DBInterfaceTest.loadBasicDB2(db);

      IndexCompatibilityChecker checker = getCompleteChecker(db, 10000, 1000);
      assertEquals(2, db.getDatabaseSize());
        while(!checker.isDone()) {  //check db with no indices
            checker.nextIteration();
        }

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
        while(!checker.isDone()) {  //check db with no indices
            checker.nextIteration();
        }
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