package net.sourcedestination.sai.db;

import static org.junit.Assert.*;
import static net.sourcedestination.sai.graph.Graph.*;
import static net.sourcedestination.sai.graph.SampleGraphs.*;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.MutableGraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

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
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		db.disconnect();
		db = new BasicDBInterface(f);
		assertEquals(0, db.getDatabaseSize());
	}
	

	@Test
	public void testSavingToAndLoadingFromFileOneGraph() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid = db.addGraph(getSmallGraph1());
		assertEquals(1, db.getDatabaseSize());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid, gf));
		
		db.disconnect();
		
		db = new BasicDBInterface(f);
		assertEquals(1, db.getDatabaseSize());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid, gf));
	}

	@Test
	public void testSavingAndLoadingMultipleGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		int gid3 = db.addGraph(getSmallGraph3());

		assertEquals(3, db.getDatabaseSize());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		assertGraphsAreIdentical(getSmallGraph3(), 
				db.retrieveGraph(gid3, gf));
		
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);
		db = new BasicDBInterface(f);
		assertEquals(3, db.getDatabaseSize());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		assertGraphsAreIdentical(getSmallGraph3(), 
				db.retrieveGraph(gid3, gf));
	}
	
	private static Set<Integer> getIndexedGraphIDs(DBInterface db, int iid) {
		return db.retrieveGraph(iid, MutableGraph::new).getFeatures()
				// only keep index features
				.filter(f -> f.getName().equals(INDEXES_FEATURE_NAME))
				// get the id's of the indexed graphs
				.map(f -> Integer.parseInt(f.getValue()))
				.collect(Collectors.toSet()); // return them as a set of graph id's
	}

	@Test
	public void testAddingIndexToGraph() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		MutableGraph i = new MutableGraph(getSmallGraph1());
		i.addFeature(INDEX);
		int gid3 = db.addGraph(i);
		db.addIndex(gid1, gid3);

		assertEquals(3, db.getDatabaseSize());
		assertEquals(1, Sets.newHashSet(
				db.retrieveGraphsWithFeatureName(
						INDEXES_FEATURE_NAME)).size());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		MutableGraph rg = new MutableGraph(getSmallGraph1());
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		assertEquals(gid3, (int)db.retrieveGraphsWithFeatureName(
				INDEXES_FEATURE_NAME).iterator().next());
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid1));
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid2));
		assertEquals(Sets.newHashSet(gid1), getIndexedGraphIDs(db, gid3));
		assertEquals(Sets.newHashSet(gid3), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid1))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(),
				db.retrieveGraphsWithFeature(getIndexesFeature(gid2))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid3))
				.collect(Collectors.toSet()));
		Iterator<Integer> ii = db.retrieveGraphsWithFeatureName(INDEXES_FEATURE_NAME).iterator();
		assertTrue(ii.hasNext());
		assertEquals(gid3, (int)ii.next());
		assertTrue(!ii.hasNext());
		db.disconnect();
		

		db = new BasicDBInterface();
		db.setDBFile(f);

		db = new BasicDBInterface(f);
		assertEquals(3, db.getDatabaseSize());
		assertEquals(1, Sets.newHashSet(
				db.retrieveGraphsWithFeatureName(
						INDEXES_FEATURE_NAME)).size());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		rg = new MutableGraph(getSmallGraph1());
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		assertEquals(gid3, (int)db.retrieveGraphsWithFeatureName(
				INDEXES_FEATURE_NAME).iterator().next());
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid1));
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid2));
		assertEquals(Sets.newHashSet(gid1), getIndexedGraphIDs(db, gid3));
		assertEquals(Sets.newHashSet(gid3), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid1))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid2))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid3))
				.collect(Collectors.toSet()));
		ii = db.retrieveGraphsWithFeatureName(INDEXES_FEATURE_NAME).iterator();
		assertTrue(ii.hasNext());
		assertEquals(gid3, (int)ii.next());
		assertTrue(!ii.hasNext());
	}

	@Test
	public void testAddingIndexToMultipleGraphs() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		File f = new File("/tmp/sai-db-test-"+Math.random());
		tempFiles.add(f);
		if(f.exists()) f.delete();
		db.setDBFile(f);
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		MutableGraph i1 = new MutableGraph(getOneEdgeIndex("a", "b", "a"));
		i1.addFeature(INDEX);
		int gid3 = db.addGraph(i1);
		db.addIndex(gid1, gid3);
		MutableGraph i2 = new MutableGraph(getOneEdgeIndex("b", "c", "a"));
		i2.addFeature(INDEX);
		int gid4 = db.addGraph(i2);
		db.addIndex(gid1, gid4);
		db.addIndex(gid3, gid4);

		assertEquals(4, db.getDatabaseSize());
		assertEquals(2, db.retrieveGraphsWithFeatureName(
						INDEXES_FEATURE_NAME).count());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		MutableGraph rg = new MutableGraph(getOneEdgeIndex("a", "b", "a"));
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		rg = new MutableGraph(getOneEdgeIndex("b", "c", "a"));
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid4, gf));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraph(gid1).getFeatures()
				.filter(feature -> feature.getName().equals(INDEXES_FEATURE_NAME))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(),
				db.retrieveGraph(gid2).getFeatures()
				.filter(feature -> feature.getName().equals(INDEXES_FEATURE_NAME))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(getIndexesFeature(gid1)), 
				  db.retrieveGraph(gid3).getFeatures()
					.filter(feature -> feature.getName().equals(INDEXES_FEATURE_NAME))
					.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(
				getIndexesFeature(gid1),
				getIndexesFeature(gid3)),
				db.retrieveGraph(gid4).getFeatures()
				.filter(feature -> feature.getName().equals(INDEXES_FEATURE_NAME))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(gid3,gid4), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid1))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid2))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(gid4), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid3))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid4))
				.collect(Collectors.toSet()));
		Iterator<Integer> ii = db.retrieveGraphsWithFeatureName(INDEXES_FEATURE_NAME).iterator();
		assertEquals(Sets.newHashSet(gid3, gid4), Sets.newHashSet(ii));
		db.disconnect();
		
		db = new BasicDBInterface(f);
		assertEquals(4, db.getDatabaseSize());
		assertEquals(2, 
				db.retrieveGraphsWithFeatureName(
						INDEXES_FEATURE_NAME)
						.distinct().count());
		assertGraphsAreIdentical(getSmallGraph1(), 
				db.retrieveGraph(gid1, gf));
		assertGraphsAreIdentical(getSmallGraph2(), 
				db.retrieveGraph(gid2, gf));
		rg = new MutableGraph(getOneEdgeIndex("a", "b", "a"));
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid3, gf));
		rg = new MutableGraph(getOneEdgeIndex("b", "c", "a"));
		rg.addFeature(INDEX);
		assertGraphsAreIdentical(rg, 
				db.retrieveGraph(gid4, gf));
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid1));
		assertEquals(Sets.newHashSet(), getIndexedGraphIDs(db, gid2));
		assertEquals(Sets.newHashSet(gid1), getIndexedGraphIDs(db, gid3));
		assertEquals(Sets.newHashSet(gid1,gid3), getIndexedGraphIDs(db, gid4));
		assertEquals(Sets.newHashSet(gid3,gid4), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid1))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid2))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(gid4),
				db.retrieveGraphsWithFeature(getIndexesFeature(gid3))
				.collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(), 
				db.retrieveGraphsWithFeature(getIndexesFeature(gid4))
				.collect(Collectors.toSet()));
	}

	@Test
	public void testGraphIterator() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		int gid3 = db.addGraph(getSmallGraph3());
		int gid4 = db.addGraph(getSmallGraph4());
		assertEquals(4, db.getDatabaseSize());
		
		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid2, gid3, gid4);
		Iterator<Integer> gi = db.getGraphIDStream().iterator();
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
		GraphFactory gf = MutableGraph::new;
		BasicDBInterface db = new BasicDBInterface();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		MutableGraph i1 = new MutableGraph(getOneEdgeIndex("a", "b", "a"));
		i1.addFeature(INDEX);
		int gid3 = db.addGraph(i1);
		db.addIndex(gid1, gid3);
		MutableGraph i2 = new MutableGraph(getOneEdgeIndex("a", "b", "a"));
		i2.addFeature(INDEX);
		int gid4 = db.addGraph(i2);
		db.addIndex(gid1, gid4);
		db.addIndex(gid3, gid4);
		
		Set<Integer> observed = db.retrieveGraphsWithFeatureName(
						INDEXES_FEATURE_NAME).collect(Collectors.toSet());
		assertEquals(Sets.newHashSet(gid3, gid4), observed);
	}
	
	@Test
	public void testDeletingGraphs() throws AccessDeniedException {
		BasicDBInterface db = new BasicDBInterface();
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid3 = db.addGraph(getSmallGraph3());
		db.deleteGraph(gid3);
		int gid4 = db.addGraph(getSmallGraph4());

		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid4);
		Iterator<Integer> gi = db.getGraphIDStream().iterator();
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(!gi.hasNext());
	}
	
}
