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

package sai;

import java.util.Iterator;
import sai.indexing.Index;
import sai.indexing.generators.path.Path1;
import sai.indexing.generators.path.Path1Lookup;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author jmorwick
 */
public class DBInterfaceTest {

    final DBInterface db = getTestDBInterface();
    

    public DBInterfaceTest() {
    }

    public static DBInterface getTestDBInterface() {
        return new DBInterface(
            "localhost",
            "sai_test_junit",
            "sai_test_user",
            "sai_test_pw");
        
    }


    public static Graph getSmallGraph1(DBInterface db) {
      Graph g1 = new Graph(db);
      Node n1 = new Node(g1, db, new GenericFeature("a",db));
      Node n2 = new Node(g1, db, new GenericFeature("b",db));
      Node n3 = new Node(g1, db, new GenericFeature("c",db));
      Node n4 = new Node(g1, db, new GenericFeature("d",db));
      g1.addEdge(n1, n2, new GenericFeature("a", db));
      g1.addEdge(n2, n3, new GenericFeature("a", db));
      g1.addEdge(n1, n4, new GenericFeature("a", db));
      g1.addEdge(n2, n4, new GenericFeature("a", db));
      return g1;
    }

    public static Graph getSmallGraph2(DBInterface db) {
      Graph g1 = new Graph(db);
      Node n1 = new Node(g1, db, new GenericFeature("e",db));
      Node n2 = new Node(g1, db, new GenericFeature("f",db));
      Node n3 = new Node(g1, db, new GenericFeature("g",db));
      Node n4 = new Node(g1, db, new GenericFeature("d",db));
      g1.addEdge(n1, n2, new GenericFeature("a", db));
      g1.addEdge(n2, n3, new GenericFeature("a", db));
      g1.addEdge(n1, n4, new GenericFeature("a", db));
      g1.addEdge(n2, n4, new GenericFeature("a", db));
      return g1;
    }


    public static Graph getSmallGraphMultiEdge(DBInterface db) {
      Graph g1 = new Graph(db);
      Node n1 = new Node(g1, db, new GenericFeature("a",db));
      Node n2 = new Node(g1, db, new GenericFeature("b",db));
      Node n3 = new Node(g1, db, new GenericFeature("a",db));
      Node n4 = new Node(g1, db, new GenericFeature("c",db));
      g1.addEdge(n1, n2, new GenericFeature("a", db));
      g1.addEdge(n1, n2, new GenericFeature("a", db));
      g1.addEdge(n1, n2, new GenericFeature("b", db));
      g1.addEdge(n2, n3, new GenericFeature("a", db));
      g1.addEdge(n1, n4, new GenericFeature("a", db));
      g1.addEdge(n2, n4, new GenericFeature("a", db));
      return g1;
    }


  public static void loadBasicDB(DBInterface db) {
      db.addIndexer(new Path1(db, GenericFeature.class));
      db.initializeDatabase();

      Graph g1 = getSmallGraph1(db);
      g1.saveToDatabase();
      g1 = getSmallGraph1(db);
      g1.saveToDatabase();
  }
  public static void loadBasicDiverseDB(DBInterface db) {
      db.addIndexer(new Path1(db, GenericFeature.class));
      db.initializeDatabase();

      Graph g1 = getSmallGraph1(db);
      g1.saveToDatabase();
      g1 = getSmallGraph2(db);
      g1.saveToDatabase();
  }
  
  public static void loadBasicDB2(final DBInterface db) {
      db.addIndexer(new Path1Lookup(db,GenericFeature.class));

      Graph g1 = getSmallGraph1(db);
      g1.saveToDatabase();
      g1 = getSmallGraph1(db);
      g1.saveToDatabase();
  }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        db.initializeDatabase();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRegisterFeatureClass() {
    }


    @Test
    public void testQuerying() {
    }

    @Test
    public void testCache() {
    }

    @Test
    public void testIgnoreStructure() {
    }

    @Test
    public void testCreateNewGraphID() {
    }

    @Test
    public void testLoadFeature() {
    }

    @Test
    public void testGetStructureIterator() {
    }

    @Test
    public void testLoadStructureFromDatabase() {
    }


    @Test
    public void testRemoveStructureFromDatabase_int() {
    }

    @Test
    public void testRemoveStructureFromDatabase_Graph() {
    }

    @Test
    public void testGetFeatureName() {
    }

    @Test
    public void testGetFeatureID() {
    }

    @Test
    public void testGetDatabaseSize() {
    }

    @Test
    public void testGetAllFeatures_Class() {
    }

    @Test
    public void testGetAllFeatures_0args() {
    }

    @Test
    public void testGetFeatureInstances() {
    }

    @Test
    public void testGetAllFeatureTypes() {
    }


    @Test
    public void testAddIndexer() {
    }

    /**
     * Test of addIndex method, of class DBInterface.
     */
    @Test
    public void testAddIndex_Graph_Index() {
    }

    /**
     * Test of addIndex method, of class DBInterface.
     */
    @Test
    public void testAddIndex_int_int() {
    }

    /**
     * Test of addRetriever method, of class DBInterface.
     */
    @Test
    public void testAddRetriever() {
    }

    /**
     * Test of findIndices method, of class DBInterface.
     */
    @Test
    public void testFindIndices() {
    }

    /**
     * Test of getIndices method, of class DBInterface.
     */
    @Test
    public void testGetIndices_Graph() {
    }

    /**
     * Test of getIndices method, of class DBInterface.
     */
    @Test
    public void testGetIndices_int() {
    }

    /**
     * Test of indexGraph method, of class DBInterface.
     */
    @Test
    public void testIndexGraph() {
    }

    /**
     * Test of indexedBy method, of class DBInterface.
     */
    @Test
    public void testIndexedBy() {
    }

    /**
     * Test of getIndexIterator method, of class DBInterface.
     */
    @Test
    public void testGetIndexIterator() {
        db.initializeDatabase();
        Index i = new Index(db);
        i.addFeature(new GenericFeature("a", db));
        i.saveToDatabase();
        Node n1 = new Node(i,db);
        n1.addFeature(new GenericFeature("b",db));
        i.saveToDatabase();
        Node n2 = new Node(i,db);
        n2.addFeature(new GenericFeature("c",db));
        i.saveToDatabase();
        i.addEdge(n1, n2, new Edge(i,new GenericFeature("c",db)));
        i.saveToDatabase();
        Iterator<Index> it = db.getIndexIterator();
        int id = 1;
        while(it.hasNext()) {
            Index ii = it.next();
            assertNotNull(ii);
            assertEquals(id, ii.getID());
            if(id == 1) assertEquals(0, ii.vertexSet().size());
            assertEquals(1, ii.getFeatures().size());
            assertEquals(new GenericFeature("a",db), ii.getFeatures().iterator().next());
            if(id == 2) assertEquals(1, ii.vertexSet().size());
            if(id > 2) assertEquals(2, ii.vertexSet().size());
            if(id < 4) assertEquals(0, ii.edgeSet().size());
            if(id == 4) assertEquals(1, ii.edgeSet().size());
            assertTrue(ii.getID() < 5);

            id++;
        }
    }


    //------------------------------------


    @Test
    public void testFindLinkedFromFeatures() {
    }

    @Test
    public void testFindLinkedToFeatures() {
    }

    @Test
    public void testFindAverageIncommingEdges() {
    }

    @Test
    public void testLikeleyhoodOfInDegree() {
    }

}