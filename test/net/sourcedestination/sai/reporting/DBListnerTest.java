package net.sourcedestination.sai.reporting;

import static org.junit.Assert.*;
import static net.sourcedestination.sai.db.SampleDBs.*;
import static net.sourcedestination.sai.graph.SampleGraphs.*;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.MutableGraph;
import net.sourcedestination.sai.retrieval.GraphIndexBasedRetriever;

import org.junit.Test;

import com.google.common.collect.Sets;

public class DBListnerTest {

	@Test
	public void testGraphBasedRetrieverRecordingBasic() throws AccessDeniedException {
		DBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices();
		Log log = new Log("test");
		GraphIndexBasedRetriever r = GraphIndexBasedRetriever::retrieveByBasicGraphIndexCount;
		GraphIndexBasedRetriever orig = r;
		r = new GraphIndexBasedRetrieverListener(r, log);

		Set<Integer> q1Results = new HashSet<>();
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		Set<Integer> q1Indices = Sets.newHashSet(5, 6, 7, 8);
		Stream<Integer> results = r.retrieve(db, q1Indices.stream());
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q1 = log.getQueryRecords().skip(0).findFirst().get();
		assertTrue(q1.getDB() == db);
		assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q1Results.add(id);
			assertEquals(q1Indices, q1.getQuery());
			assertTrue(q1.getDB() == db);
			assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
		
		Set<Integer> q2Results = new HashSet<>();
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		Set<Integer> q2Indices = Sets.newHashSet(5, 6, 7);
		results = r.retrieve(db, q2Indices.stream());
		assertEquals(2, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q2 = log.getQueryRecords().skip(1).findFirst().get();
		assertTrue(q2.getDB() == db);
		assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q2Results.add(id);
			assertEquals(q2Indices, q2.getQuery());
			assertTrue(q2.getDB() == db);
			assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});

	}

	@Test
	public void testIndexBasedRetrieverRecordingBasic() throws AccessDeniedException {
		DBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices();
		Log log = new Log("test");
		GraphIndexBasedRetriever r = GraphIndexBasedRetriever::retrieveByBasicGraphIndexCount;
		GraphIndexBasedRetriever orig = r;
		r = new GraphIndexBasedRetrieverListener(r, log);

		Set<Integer> q1Results = new HashSet<>();
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		Set<Integer> q1Indices = Sets.newHashSet(5, 6, 7, 8);
		Stream<Integer> results = r.retrieve(db, q1Indices.stream());
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q1 = log.getQueryRecords().skip(0).findFirst().get();
		assertTrue(q1.getDB() == db);
		assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q1Results.add(id);
			assertEquals(q1Indices, q1.getQuery());
			assertTrue(q1.getDB() == db);
			assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
		
		Set<Integer> q2Results = new HashSet<>();
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		Set<Integer> q2Indices = Sets.newHashSet(5, 6, 7);
		results = r.retrieve(db, q2Indices.stream());
		assertEquals(2, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q2 = log.getQueryRecords().skip(1).findFirst().get();
		assertTrue(q2.getDB() == db);
		assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q2Results.add(id);
			assertEquals(q2Indices, q2.getQuery());
			assertTrue(q2.getDB() == db);
			assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});

	}


	@Test
	public void testFeatureNameBasedRetrievalRecording() {
		DBInterface db = smallGraphsDB();
		DBInterface orig = db;
		Log log = new Log("test");
		db = new DBListener(db, log);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		
		Stream<Integer> results = null;
		
		Set<Integer> q1Results = new HashSet<>();
		results = db.retrieveGraphsWithFeatureName("test");
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q1 = log.getQueryRecords().findFirst().get();
		assertEquals("test", q1.getQuery());
		assertTrue(q1.getDB() == orig);
		assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q1Results.add(id);
			assertEquals("test", q1.getQuery());
			assertTrue(q1.getDB() == orig);
			assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});	
	}

	@Test
	public void testFeatureBasedRetrievalRecording() {
		DBInterface db = smallGraphsDB();
		DBInterface orig = db;
		Log log = new Log("test");
		db = new DBListener(db, log);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		
		Stream<Integer> results = null;
		
		Set<Integer> q1Results = new HashSet<>();
		results = db.retrieveGraphsWithFeature(new Feature("test", "a"));
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q1 = log.getQueryRecords().findFirst().get();
		assertEquals(new Feature("test", "a"), q1.getQuery());
		assertTrue(q1.getDB() == orig);
		assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q1Results.add(id);
			assertEquals(new Feature("test", "a"), q1.getQuery());
			assertTrue(q1.getDB() == orig);
			assertEquals(q1Results, q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
		
		Set<Integer> q2Results = new HashSet<>();
		results = db.retrieveGraphsWithFeature(new Feature("test", "b"));
		assertEquals(2, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q2 = log.getQueryRecords().skip(1).findFirst().get();
		assertEquals(new Feature("test", "b"), q2.getQuery());
		assertTrue(q2.getDB() == orig);
		assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q2Results.add(id);
			assertEquals(new Feature("test", "b"), q2.getQuery());
			assertTrue(q2.getDB() == orig);
			assertEquals(q2Results, q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
		
		Set<Integer> q3Results = new HashSet<>();
		results = db.retrieveGraphsWithFeature(new Feature("test", "c"));
		assertEquals(3, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q3 = log.getQueryRecords().skip(2).findFirst().get();
		assertEquals(new Feature("test", "c"), q3.getQuery());
		assertTrue(q3.getDB() == orig);
		assertEquals(q3Results, q3.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q3Results.add(id);
			assertEquals(new Feature("test", "c"), q3.getQuery());
			assertTrue(q3.getDB() == orig);
			assertEquals(q3Results, q3.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
		
		Set<Integer> q4Results = new HashSet<>();
		results = db.retrieveGraphsWithFeature(new Feature("test", "d"));
		assertEquals(4, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q4 = log.getQueryRecords().skip(3).findFirst().get();
		assertEquals(new Feature("test", "d"), q4.getQuery());
		assertTrue(q4.getDB() == orig);
		assertEquals(q4Results, q4.getRetrievedGraphIDs().collect(Collectors.toSet()));
		results.forEach(id -> {
			q4Results.add(id);
			assertEquals(new Feature("test", "d"), q4.getQuery());
			assertTrue(q4.getDB() == orig);
			assertEquals(q4Results, q4.getRetrievedGraphIDs().collect(Collectors.toSet()));
		});
	}

	@Test
	public void testIDBasedRetrievalRecording() {
		DBInterface db = smallGraphsDB();
		DBInterface orig = db;
		Log log = new Log("test");
		db = new DBListener(db, log);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		
		Graph g1 = db.retrieveGraph(1, MutableGraph::new);
		assertEquals(1, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q1 = log.getQueryRecords().findFirst().get();
		assertEquals(1, q1.getQuery());
		assertTrue(q1.getDB() == orig);
		assertEquals(Sets.newHashSet(1), q1.getRetrievedGraphIDs().collect(Collectors.toSet()));
		
		Graph g2 = db.retrieveGraph(2, MutableGraph::new);
		assertEquals(2, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q2 = log.getQueryRecords().skip(1).findFirst().get();
		assertEquals(2, q2.getQuery());
		assertTrue(q2.getDB() == orig);
		assertEquals(Sets.newHashSet(2), q2.getRetrievedGraphIDs().collect(Collectors.toSet()));
		
		Graph g3 = db.retrieveGraph(3, MutableGraph::new);
		assertEquals(3, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q3 = log.getQueryRecords().skip(2).findFirst().get();
		assertEquals(3, q3.getQuery());
		assertTrue(q3.getDB() == orig);
		assertEquals(Sets.newHashSet(3), q3.getRetrievedGraphIDs().collect(Collectors.toSet()));
		
		Graph g4 = db.retrieveGraph(4, MutableGraph::new);
		assertEquals(4, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		assertEquals(0, log.getNumAdditionRecords());
		QueryRecord q4 = log.getQueryRecords().skip(3).findFirst().get();
		assertEquals(4, q4.getQuery());
		assertTrue(q4.getDB() == orig);
		assertEquals(Sets.newHashSet(4), q4.getRetrievedGraphIDs().collect(Collectors.toSet()));
	}
	
	
	@Test
	public void testDeletionRecording() {
		DBInterface db = smallGraphsDB();
		DBInterface orig = db;
		Log log = new Log("test");
		db = new DBListener(db, log);
		DeletionRecord dr = null;
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumDeletionRecords());
		
		db.deleteGraph(1);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(1, log.getNumDeletionRecords());
		dr = log.getDeletionRecords().findFirst().get();
		assertEquals(1, dr.getID());
		assertTrue(orig == dr.getDB());
		
		Graph g2 = getSmallGraph2();
		db.deleteGraph(2);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(2, log.getNumDeletionRecords());
		dr = log.getDeletionRecords().skip(1).findFirst().get();
		assertEquals(2, dr.getID());
		assertTrue(orig == dr.getDB());
		
		Graph g3 = getSmallGraph3();
		db.deleteGraph(3);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(3, log.getNumDeletionRecords());
		dr = log.getDeletionRecords().skip(2).findFirst().get();
		assertEquals(3, dr.getID());
		assertTrue(orig == dr.getDB());
		
		Graph g4 = getSmallGraph4();
		db.deleteGraph(4);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(4, log.getNumDeletionRecords());
		dr = log.getDeletionRecords().skip(3).findFirst().get();
		assertEquals(4, dr.getID());
		assertTrue(orig == dr.getDB());
	}

	@Test
	public void testAdditionRecording() {
		DBInterface db = getEmptyDB();
		DBInterface orig = db;
		Log log = new Log("test");
		db = new DBListener(db, log);
		AdditionRecord ar = null;
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(0, log.getNumAdditionRecords());
		
		Graph g1 = getSmallGraph1();
		db.addGraph(g1);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(1, log.getNumAdditionRecords());
		ar = log.getAdditionRecords().findFirst().get();
		assertEquals(1, ar.getID());
		assertTrue(orig == ar.getDB());
		assertTrue(g1 == ar.getGraph());
		
		Graph g2 = getSmallGraph2();
		db.addGraph(g2);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(2, log.getNumAdditionRecords());
		ar = log.getAdditionRecords().skip(1).findFirst().get();
		assertEquals(2, ar.getID());
		assertTrue(orig == ar.getDB());
		assertTrue(g2 == ar.getGraph());
		
		Graph g3 = getSmallGraph3();
		db.addGraph(g3);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(3, log.getNumAdditionRecords());
		ar = log.getAdditionRecords().skip(2).findFirst().get();
		assertEquals(3, ar.getID());
		assertTrue(orig == ar.getDB());
		assertTrue(g3 == ar.getGraph());
		
		Graph g4 = getSmallGraph4();
		db.addGraph(g4);
		assertEquals(0, log.getNumQueryRecords());
		assertEquals(4, log.getNumAdditionRecords());
		ar = log.getAdditionRecords().skip(3).findFirst().get();
		assertEquals(4, ar.getID());
		assertTrue(orig == ar.getDB());
		assertTrue(g4 == ar.getGraph());
	}

}
