package sai.retrieval;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.Sets;

import static info.kendall_morwick.funcles.Funcles.apply;
import sai.comparison.compatibility.CompatibilityUtil;
import sai.comparison.heuristics.Heuristics;
import sai.comparison.matching.GraphMatching;
import sai.comparison.matching.MatchingGenerator;
import sai.comparison.matching.MatchingUtil;
import sai.db.BasicDBInterface;
import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.BasicGraphFactory;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.SampleGraphs;
import sai.indexing.BasicPath1IndexRetriever;

public class RetrievalUtilTest {

	@Test
	public void testBasicCountRetriever() throws AccessDeniedException {
		GraphFactory gf = new BasicGraphFactory();
		BasicDBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices(gf);
		IndexBasedGraphRetriever r = new BasicCountRetriever();
		Iterator<Integer> i = r.retrieve(db, Sets.newHashSet(5, 6, 7, 9));
		i = r.retrieve(db, Sets.newHashSet(5, 6, 7, 8));
		assertTrue(i.hasNext());
		assertEquals(2, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains((int)i.next()));
		assertTrue(!i.hasNext());
		
		i = r.retrieve(db, Sets.newHashSet(5, 6, 7));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertEquals(4, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertTrue(!i.hasNext());
		
	}

	private void selfTest(Graph g, MatchingGenerator gen) {
		GraphMatching m = apply(gen, g, g);
		
		assertEquals(g.getNodeIDs().size(), m.getAllNodeMatches().size());
		assertEquals(g.getEdgeIDs().size(), m.getAllEdgeMatches().size());
		for(int nid : g.getNodeIDs())
			assertEquals(nid, m.getMatchedNodeInGraph2(nid));
		for(int eid : g.getEdgeIDs())
			assertEquals(eid, m.getMatchedEdgeInGraph2(eid));
	}
	
	@Test
	public void testCompleteMatchingGeneratorAgainstSelf() {
		MatchingGenerator gen = MatchingUtil.createCompleteMatchingGenerator(
				CompatibilityUtil.greedy1To1Checker(CompatibilityUtil.lexicalChecker()), 
				Heuristics.basicEdgeCount());
		DBInterface db = SampleDBs.getEmptyDB(new BasicGraphFactory());
		selfTest(SampleGraphs.getSmallGraph1(), gen);
		selfTest(SampleGraphs.getSmallGraph2(), gen);
		selfTest(SampleGraphs.getSmallGraph3(), gen);
		selfTest(SampleGraphs.getSmallGraph4(), gen);
		selfTest(SampleGraphs.getMediumUnlabeledTreeOneSelfIso(), gen);
	}

	@Test
	public void testCompleteMatchingGeneratorAgainstSelfNonUnique() {
		//TODO: create a sample graph with a non-unique best self-mapping and look for one of them
	}

	@Test
	public void testCompleteMatchingGeneratorAgainstSimilar() {
		//TODO: try to match an assortment of graphs that don't match exactly and check for proper maximal matching
	}
	
	@Test
	public void testBuildPhase1Retriever() throws AccessDeniedException {
		GraphFactory gf = new BasicGraphFactory();
		BasicDBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices(gf);
		GraphRetriever r = RetrievalUtil.createPhase1Retriever(
				new BasicPath1IndexRetriever("test"), 
				new BasicCountRetriever());
		Iterator<Integer> i = r.retrieve(db, SampleGraphs.getSmallGraph1());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(2, (int)i.next());
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains((int)i.next()));
		assertTrue(!i.hasNext());
		
		i = r.retrieve(db, SampleGraphs.getSmallGraph2());
		assertTrue(i.hasNext());
		assertEquals(2, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(4, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertTrue(!i.hasNext());
		
		i = r.retrieve(db, SampleGraphs.getSmallGraph4());
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 4).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 4).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 4).contains((int)i.next()));
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertTrue(!i.hasNext());
	}
	
	@Test
	public void testBuild2PhasedRetriever() {
		//TODO: use test DB from first test w/ mistakes in indexing and see that complete checker corrects them
	}
	
	//TODO: create a test for the basic lookup index retriever

}
