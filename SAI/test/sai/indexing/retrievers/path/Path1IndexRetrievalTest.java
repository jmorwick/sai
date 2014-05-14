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

package sai.indexing.retrievers.path;

import info.kendall_morwick.funcles.T2;

import java.util.List;
import java.util.Set;

import sai.DBInterface;
import sai.DBInterfaceTest;
import sai.Feature;
import sai.Graph;
import sai.comparison.subgraphcomparators.CompleteSubgraphComparator;
import sai.indexing.Index;
import sai.maintenance.IndexCompatabilityChecker;
import sai.maintenance.IndexConsolidator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;

import static sai.DBInterfaceTest.*;
import static sai.comparison.subgraphcomparators.CompleteSubgraphComparatorTest.*;
import static org.junit.Assert.*;

/**
 *
 * @author jmorwick
 */
public class Path1IndexRetrievalTest {

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
     * Test of retrieveIndices method, of class LinkIndexRetriever.
     */
    @Test
    public void testRetrieveIndices() {

        //first, load a DB with two different graphs and insure only the
        //appropriate number of indices are retrieved


        final DBInterface db = getTestDBInterface();
        db.initializeDatabase();
        DBInterfaceTest.loadBasicDiverseDB(db);

    db.addRetriever(new Path1Retriever(db, Feature.class));

        Graph g1 = db.loadStructureFromDatabase(1);
        Graph g2 = db.loadStructureFromDatabase(2);
        db.indexGraph(g1);
        db.indexGraph(g2);
        IndexCompatabilityChecker checker = getCompleteChecker(db, 10000, 3);
      //TODO: add tests for list return value
      	List<T2<Integer, Integer>> collapsed = checker.get();
        IndexConsolidator ic = new IndexConsolidator(db);
        while(!ic.isDone()) ic.nextIteration();
        Set<Index> s1 = db.getIndices(g1);
        Set<Index> s2 = db.getIndices(g2);
        assertEquals(4, s1.size());
        assertEquals(4, s2.size());
        assertEquals(8, Sets.union(s1, s2).size());  //the indices should be distinct


        final DBInterface db2 = getTestDBInterface();
        db2.initializeDatabase();
        DBInterfaceTest.loadBasicDB(db2);

        // now load a DB with two copies of the same graph and check to see that
        // the indices loaded are not distinct

    db2.addRetriever(new Path1Retriever(db2, Feature.class));


        g1 = db2.loadStructureFromDatabase(1);
        g2 = db2.loadStructureFromDatabase(2);
        db2.indexGraph(g1);
        db2.indexGraph(g2);
        checker = getCompleteChecker(db, 10000, 2);
      //TODO: add tests for list return value
      	collapsed = checker.get();
        ic = new IndexConsolidator(db2);
        while(!ic.isDone()) {
            ic.nextIteration();
        }
        s1 = db2.getIndices(g1);
        s2 = db2.getIndices(g2);
        assertEquals(4, s1.size());
        assertEquals(4, s2.size());
        assertEquals(4, Sets.union(s1, s2).size());

        db2.initializeDatabase();
    }

}