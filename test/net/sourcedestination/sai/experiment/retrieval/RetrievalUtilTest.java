package net.sourcedestination.sai.experiment.retrieval;
/*
import static net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityChecker.greedy1To1Checker;
import static net.sourcedestination.sai.experiment.matching.MatchingGenerator.createCompleteMatchingGenerator;
import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Iterator;

import FeatureCompatibilityChecker;
import net.sourcedestination.sai.comparison.distance.GraphMatchingDistance;
import net.sourcedestination.sai.experiment.matching.GraphMatching;
import net.sourcedestination.sai.experiment.matching.MatchingGenerator;
import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.SampleGraphs;

import org.junit.Test;

import com.google.common.collect.Sets;
*/
public class RetrievalUtilTest {
/*
	@Test
	public void testBasicCountRetriever() throws AccessDeniedException {
		BasicDBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices();
		GraphIndexBasedRetriever r = GraphIndexBasedRetriever::retrieveByBasicGraphIndexCount;
		Iterator<Integer> i;
		i = r.retrieve(db, Sets.newHashSet(5, 6, 7, 8).stream()).iterator();
		assertTrue(i.hasNext());
		assertEquals(2, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(i.next()));
		assertTrue(!i.hasNext());
		
		i = r.retrieve(db, Sets.newHashSet(5, 6, 7).stream()).iterator();
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2).contains(i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2).contains(i.next()));
		assertTrue(i.hasNext());
		assertEquals(4, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertTrue(!i.hasNext());
		
	}

	private void selfTest(Graph g, MatchingGenerator gen) {
		GraphMatching m = gen.apply(g, g);
		
		assertEquals(g.getNodeIDs().count(), m.getAllNodeMatches().size());
		assertEquals(g.getEdgeIDs().count(), m.getAllEdgeMatches().size());
		g.getNodeIDs().forEach(nid ->
			assertEquals(nid, (Integer)m.getMatchedNodeInGraph2(nid)));
		g.getEdgeIDs().forEach(eid ->
			assertEquals(eid, (Integer)m.getMatchedEdgeInGraph2(eid)));
	}
	
	@Test
	public void testCompleteMatchingGeneratorAgainstSelf() {
		MatchingGenerator gen = createCompleteMatchingGenerator(
				greedy1To1Checker(FeatureCompatibilityChecker::areLexicallyCompatible), 
				GraphMatchingDistance::basicEdgeCount);
		selfTest(SampleGraphs.getSmallGraph1(), gen);
		selfTest(SampleGraphs.getSmallGraph2(), gen);
		selfTest(SampleGraphs.getSmallGraph3(), gen);
		selfTest(SampleGraphs.getSmallGraph4(), gen);
		selfTest(SampleGraphs.getMediumUnlabeledTreeOneSelfIso(), gen);
	}

	@Test
	public void testCompleteMatchingGeneratorAgainstSelfNonUnique() {
		MatchingGenerator gen = createCompleteMatchingGenerator(
				greedy1To1Checker(FeatureCompatibilityChecker::areLexicallyCompatible), 
				GraphMatchingDistance::basicEdgeCount);
		Graph g = SampleGraphs.getSmallSymmetricTree();
		GraphMatching m = gen.apply(g, g);
		
		assertEquals(g.getNodeIDs().count(), m.getAllNodeMatches().size());
		assertEquals(g.getEdgeIDs().count(), m.getAllEdgeMatches().size());
		assertEquals(1, m.getMatchedNodeInGraph2(1));
		assertEquals(Sets.newHashSet(2,3),
				Sets.newHashSet(
						m.getMatchedNodeInGraph2(2), 
						m.getMatchedNodeInGraph2(3)));	
		assertEquals(Sets.newHashSet(2,1),
				Sets.newHashSet(
						m.getMatchedEdgeInGraph2(2), 
						m.getMatchedEdgeInGraph2(1)));	
		}

	@Test
	public void testCompleteMatchingGeneratorAgainstSimilar() {
		MatchingGenerator gen = createCompleteMatchingGenerator(
				greedy1To1Checker(FeatureCompatibilityChecker::areLexicallyCompatible), 
				GraphMatchingDistance::basicEdgeCount);
		GraphMatching m = gen.apply(SampleGraphs.getSmallGraph1(), SampleGraphs.getSmallGraph2());
		
		assertEquals(4, m.getAllNodeMatches().size());
		assertEquals(3, m.getAllEdgeMatches().size());
		assertEquals(1, m.getMatchedNodeInGraph2(1));
		assertEquals(2, m.getMatchedNodeInGraph2(2));
		assertEquals(3, m.getMatchedNodeInGraph2(3));
		assertEquals(4, m.getMatchedNodeInGraph2(4));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(2, m.getMatchedEdgeInGraph2(2));
		assertEquals(4, m.getMatchedEdgeInGraph2(4));
	}
	
	/* bring these tests back with a graph-index based indexing system, such as GIndex, 
	 * since path1 is now done with features instead of graphs
	 *
	@Test
	public void testBuildPhase1Retriever() throws AccessDeniedException {
		GraphFactory gf = new MutableGraphFactory();
		BasicDBInterface db = SampleDBs.smallGraphsDBWithCorrectIndices();
		GraphRetriever r = RetrievalUtil.createPhase1Retriever(
				new BasicPath1IndexRetriever("test"), 
				new BasicGraphIndexCount());
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
	public void testBuild2PhasedRetriever() throws AccessDeniedException {
		GraphFactory gf = new MutableGraphFactory();
		GraphMatchingDistance h = Heuristics.basicEdgeCount();
		MatchingGenerator gen = MatchingUtil.createCompleteMatchingGenerator(
				CompatibilityUtil.greedy1To1Checker(CompatibilityUtil.lexicalChecker()), 
				h);
		GraphRetriever phase1 = RetrievalUtil.createPhase1Retriever(
				new BasicPath1IndexRetriever("test"), 
				new BasicGraphIndexCount());
		BasicDBInterface db = SampleDBs.smallGraphsDBWithIncorrectIndices();
		
		Graph query = SampleGraphs.getSmallGraph1();
		Iterator<Graph> i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 4, 4);
		assertTrue(i.hasNext());
		assertEquals(1, db.addGraph(i.next()));
		assertTrue(i.hasNext());
		assertEquals(2, db.addGraph(i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph1();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 2, 2);
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph1();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 2, 1);
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph2();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 4, 4);
		assertTrue(i.hasNext());
		assertEquals(2, db.addGraph(i.next()));
		assertTrue(i.hasNext());
		assertEquals(1, db.addGraph(i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph2();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 3, 3);
		assertTrue(i.hasNext());
		assertEquals(1, db.addGraph(i.next()));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
	
		query = SampleGraphs.getSmallGraph3();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 4, 4);
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph4();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 4, 4);
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(i.hasNext());
		assertTrue(Sets.newHashSet(1, 2, 3, 4).contains(db.addGraph(i.next())));
		assertTrue(!i.hasNext());
		
		query = SampleGraphs.getSmallGraph4();
		i = RetrievalUtil.twoPhasedRetrieval(phase1, db, gf, 
				MatchingUtil.createGraphMatchOrdering(query, gen, h),
				query, 2, 2);
		Set<Integer> results = Sets.newHashSet();
		for(Graph g : Sets.newHashSet(i)) results.add(db.addGraph(g));
		assertEquals(Sets.newHashSet(1,3), results);

	}
	*/
}
