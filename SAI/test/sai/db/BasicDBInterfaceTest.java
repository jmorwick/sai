package sai.db;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Graphs;
import sai.graph.MutableGraph;
import sai.graph.SampleGraphs;
import static sai.graph.SampleGraphs.assertGraphsAreIdentical;

public class BasicDBInterfaceTest {
	
	private static Set<File> tempFiles;

	@Before
	public void setUp() throws Exception {
		tempFiles = Sets.newHashSet();
	}

	@After
	public void tearDown() throws Exception {
		//delete all temporary files
		for(File f : tempFiles)
			f.delete();
	}

	@Test
	public void testSavingToAndLoadingFromFileNoGraphs() throws AccessDeniedException {
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.connect();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		db.disconnect();
		db.connect();
		assertEquals(0, db.getDatabaseSize());
	}
	

	@Test
	public void testSavingToAndLoadingFromFileOneGraph() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.connect();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid = db.addGraph(SampleGraphs.getSmallGraph1());
		assertEquals(1, db.getDatabaseSize());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid, gf));
		
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);
		db.connect();
		assertEquals(1, db.getDatabaseSize());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid, gf));
	}

	@Test
	public void testSavingAndLoadingMultipleGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.connect();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		int gid3 = db.addGraph(SampleGraphs.getSmallGraph3());

		assertEquals(3, db.getDatabaseSize());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph3(), 
				db.retrieveGraph(gid3, gf));
		
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);
		db.connect();
		assertEquals(3, db.getDatabaseSize());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph3(), 
				db.retrieveGraph(gid3, gf));
	}

	@Test
	public void testAddingIndexToGraph() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.connect();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		MutableGraph i = new MutableGraph(SampleGraphs.getSmallGraph1());
		i.addFeature(Graphs.INDEX);
		int gid3 = db.addGraph(i);
		db.addIndex(gid1, gid3);

		assertEquals(3, db.getDatabaseSize());
		assertEquals(2, db.getDatabaseSizeWithoutIndices());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		MutableGraph rg = new MutableGraph(SampleGraphs.getSmallGraph1());
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		assertEquals(gid3, (int)db.getIndexIDIterator().next());
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid2));
		assertEquals(Sets.newHashSet(gid1), db.retrieveIndexedGraphIDs(gid3));
		assertEquals(Sets.newHashSet(gid3), db.retrieveIndexIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid2));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid3));
		Iterator<Integer> ii = db.getIndexIDIterator();
		assertTrue(ii.hasNext());
		assertEquals(gid3, (int)ii.next());
		assertTrue(!ii.hasNext());
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);
		db.connect();
		assertEquals(3, db.getDatabaseSize());
		assertEquals(2, db.getDatabaseSizeWithoutIndices());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		rg = new MutableGraph(SampleGraphs.getSmallGraph1());
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		assertEquals(gid3, (int)db.getIndexIDIterator().next());
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid2));
		assertEquals(Sets.newHashSet(gid1), db.retrieveIndexedGraphIDs(gid3));
		assertEquals(Sets.newHashSet(gid3), db.retrieveIndexIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid2));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid3));
		ii = db.getIndexIDIterator();
		assertTrue(ii.hasNext());
		assertEquals(gid3, (int)ii.next());
		assertTrue(!ii.hasNext());
	}

	@Test
	public void testAddingIndexToMultipleGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.connect();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		MutableGraph i1 = new MutableGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		i1.addFeature(Graphs.INDEX);
		int gid3 = db.addGraph(i1);
		db.addIndex(gid1, gid3);
		MutableGraph i2 = new MutableGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a"));
		i2.addFeature(Graphs.INDEX);
		int gid4 = db.addGraph(i2);
		db.addIndex(gid1, gid4);
		db.addIndex(gid3, gid4);

		assertEquals(4, db.getDatabaseSize());
		assertEquals(2, db.getDatabaseSizeWithoutIndices());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		MutableGraph rg = new MutableGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		rg = new MutableGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a"));
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid4, gf));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid2));
		assertEquals(Sets.newHashSet(gid1), db.retrieveIndexedGraphIDs(gid3));
		assertEquals(Sets.newHashSet(gid1,gid3), db.retrieveIndexedGraphIDs(gid4));
		assertEquals(Sets.newHashSet(gid3,gid4), db.retrieveIndexIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid2));
		assertEquals(Sets.newHashSet(gid4), db.retrieveIndexIDs(gid3));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid4));
		Iterator<Integer> ii = db.getIndexIDIterator();
		assertTrue(ii.hasNext());
		assertEquals(gid3, (int)ii.next());
		assertTrue(ii.hasNext());
		assertEquals(gid4, (int)ii.next());
		assertTrue(!ii.hasNext());
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);
		db.connect();
		assertEquals(4, db.getDatabaseSize());
		assertEquals(2, db.getDatabaseSizeWithoutIndices());
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(SampleGraphs.getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		rg = new MutableGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		rg = new MutableGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a"));
		rg.addFeature(Graphs.INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid4, gf));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexedGraphIDs(gid2));
		assertEquals(Sets.newHashSet(gid1), db.retrieveIndexedGraphIDs(gid3));
		assertEquals(Sets.newHashSet(gid1,gid3), db.retrieveIndexedGraphIDs(gid4));
		assertEquals(Sets.newHashSet(gid3,gid4), db.retrieveIndexIDs(gid1));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid2));
		assertEquals(Sets.newHashSet(gid4), db.retrieveIndexIDs(gid3));
		assertEquals(Sets.newHashSet(), db.retrieveIndexIDs(gid4));
	}

	@Test
	public void testGraphIterator() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		db.connect();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		int gid3 = db.addGraph(SampleGraphs.getSmallGraph3());
		int gid4 = db.addGraph(SampleGraphs.getSmallGraph4());
		
		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid2, gid3, gid4);
		Iterator<Integer> gi = db.getGraphIDIterator();
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(!gi.hasNext());
	}
	
	@Test
	public void testIndexIterator() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		db.connect();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		MutableGraph i1 = new MutableGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		i1.addFeature(Graphs.INDEX);
		int gid3 = db.addGraph(i1);
		db.addIndex(gid1, gid3);
		MutableGraph i2 = new MutableGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		i2.addFeature(Graphs.INDEX);
		int gid4 = db.addGraph(i2);
		db.addIndex(gid1, gid4);
		db.addIndex(gid3, gid4);
		
		Set<Integer> observed = Sets.newHashSet(db.getIndexIDIterator());
		assertEquals(Sets.newHashSet(gid3, gid4), observed);
	}

	@Test
	public void testHidingGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		db.connect();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		int gid3 = db.addGraph(SampleGraphs.getSmallGraph3());
		int gid4 = db.addGraph(SampleGraphs.getSmallGraph4());

		Set<Integer> observed = Sets.newHashSet(db.getGraphIDIterator());
		Set<Integer> expected = Sets.newHashSet(gid1, gid2, gid3, gid4);
		assertEquals(expected, observed);

		db.hideGraph(gid2);
		expected = Sets.newHashSet(gid1, gid3, gid4);
		observed = Sets.newHashSet(db.getGraphIDIterator());
		assertEquals(expected, observed);
		
		db.hideGraph(gid3);
		expected = Sets.newHashSet(gid1, gid4);
		observed = Sets.newHashSet(db.getGraphIDIterator());
		assertEquals(expected, observed);
		
		db.unhideGraph(gid2);
		expected = Sets.newHashSet(gid1, gid2, gid4);
		observed = Sets.newHashSet(db.getGraphIDIterator());
		assertEquals(expected, observed);
		
		db.hideGraph(gid4);
		expected = Sets.newHashSet(gid1, gid2);
		observed = Sets.newHashSet(db.getGraphIDIterator());
		assertEquals(expected, observed);
	}
	
	@Test
	public void testDeletingGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph.getFactory();
		BasicDBInterface db = new BasicDBInterface();
		db.connect();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int gid2 = db.addGraph(SampleGraphs.getSmallGraph2());
		int gid3 = db.addGraph(SampleGraphs.getSmallGraph3());
		db.deleteGraph(gid3);
		int gid4 = db.addGraph(SampleGraphs.getSmallGraph4());
		db.hideGraph(gid2);
		
		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid4);
		Iterator<Integer> gi = db.getGraphIDIterator();
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(!gi.hasNext());
	}
	
}
