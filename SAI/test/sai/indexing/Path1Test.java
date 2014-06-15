package sai.indexing;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import sai.comparison.compatibility.CompatibilityUtil;
import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.comparison.heuristics.Heuristics;
import sai.comparison.matching.MatchingGenerator;
import sai.comparison.matching.MatchingUtil;
import sai.db.BasicDBInterface;
import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.BasicGraphFactory;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.SampleGraphs;
import sai.retrieval.BasicCountRetriever;
import sai.retrieval.GraphRetriever;
import sai.retrieval.RetrievalUtil;

public class Path1Test {

	@Test
	public void testPath1Generation() throws AccessDeniedException {
		GraphFactory gf = new BasicGraphFactory();
		DBInterface db = SampleDBs.getEmptyDB(gf);
		IndexGenerator gen = new Path1IndexGenerator("test");
		db.connect();
		Set<Graph> indices = gen.generateIndices(db, gf, SampleGraphs.getSmallGraph1());
		
		assertEquals(4, indices.size());
		boolean seen1 = false;
		boolean seen2 = false;
		boolean seen3 = false;
		boolean seen4 = false;
		for(Graph i : indices) {
			assertEquals(1, i.getEdgeIDs().size());
			int eid = i.getEdgeIDs().iterator().next();
			assertEquals(1, i.getEdgeFeatures(eid).size());
			Feature ef = i.getEdgeFeatures(eid).iterator().next();
			assertEquals(1, i.getNodeFeatures(i.getEdgeSourceNodeID(eid)).size());
			Feature n1f = i.getNodeFeatures(i.getEdgeSourceNodeID(eid)).iterator().next();
			assertEquals(1, i.getNodeFeatures(i.getEdgeTargetNodeID(eid)).size());
			Feature n2f = i.getNodeFeatures(i.getEdgeTargetNodeID(eid)).iterator().next();
			assertEquals("test", ef.getName());
			assertEquals("test", n1f.getName());
			assertEquals("test", n2f.getName());

			if(!seen1 && n1f.getValue().equals("a") && n2f.getValue().equals("b")) {
				seen1 = true;
			} else if(!seen2 && n1f.getValue().equals("b") && n2f.getValue().equals("c")) {
				seen2 = true;
			} else if(!seen3 && n1f.getValue().equals("c") && n2f.getValue().equals("d")) {
				seen3 = true;
			} else if(!seen4 && n1f.getValue().equals("b") && n2f.getValue().equals("d")) {
				seen4 = true;
			} else {
				fail("unexpected edge: " + n1f + ", " + ef + ", " + n2f);
			}
		}
	}
	

	@Test
	public void testPath1Lookup() throws AccessDeniedException {
		GraphFactory gf = new BasicGraphFactory();
		GraphRetriever r = new BasicPath1IndexRetriever("test");
		BasicDBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices(gf);
		assertEquals(Sets.newHashSet(5,7,8,9),
				Sets.newHashSet(r.retrieve(db, SampleGraphs.getSmallGraph1())));
		assertEquals(Sets.newHashSet(5,6,7,8),
				Sets.newHashSet(r.retrieve(db, SampleGraphs.getSmallGraph2())));
		assertEquals(Sets.newHashSet(5),
				Sets.newHashSet(r.retrieve(db, SampleGraphs.getSmallGraph3())));
		assertEquals(Sets.newHashSet(5,6),
				Sets.newHashSet(r.retrieve(db, SampleGraphs.getSmallGraph4())));
	}

}
