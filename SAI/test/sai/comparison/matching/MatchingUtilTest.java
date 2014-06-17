package sai.comparison.matching;

import static org.junit.Assert.*;
import info.kendall_morwick.funcles.Pair;
import info.kendall_morwick.funcles.T2;

import java.nio.file.AccessDeniedException;
import java.util.List;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import sai.comparison.compatibility.CompatibilityUtil;
import sai.comparison.compatibility.FeatureSetCompatibilityChecker;
import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.comparison.heuristics.Heuristics;
import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.Graph;
import sai.graph.SampleGraphs;

public class MatchingUtilTest {
	
	@Test
	public void testBasicNodeMatching() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		db.connect();
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph3();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();

		GraphMatching m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(0, m.getAllNodeMatches().size());
		assertEquals(0, m.getAllEdgeMatches().size());
		assertEquals(-1, m.getMatchedNodeInGraph1(1));
		assertEquals(-1, m.getMatchedNodeInGraph2(1));
		assertEquals(-1, m.getMatchedNodeInGraph1(2));
		assertEquals(-1, m.getMatchedNodeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(1));
		assertEquals(-1, m.getMatchedEdgeInGraph2(1));
		
		nodeMatch.put(1,2);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(1, m.getAllNodeMatches().size());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 2)), m.getAllNodeMatches());
		assertEquals(0, m.getAllEdgeMatches().size());
		assertEquals(-1, m.getMatchedNodeInGraph1(1));
		assertEquals(2, m.getMatchedNodeInGraph2(1));
		assertEquals(1, m.getMatchedNodeInGraph1(2));
		assertEquals(-1, m.getMatchedNodeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(1));
		assertEquals(-1, m.getMatchedEdgeInGraph2(1));
		
		nodeMatch.put(2,1);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(2, m.getAllNodeMatches().size());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 2),
				Pair.makePair(2, 1)), m.getAllNodeMatches());
		assertEquals(0, m.getAllEdgeMatches().size());
		assertEquals(2, m.getMatchedNodeInGraph1(1));
		assertEquals(2, m.getMatchedNodeInGraph2(1));
		assertEquals(1, m.getMatchedNodeInGraph1(2));
		assertEquals(1, m.getMatchedNodeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(1));
		assertEquals(-1, m.getMatchedEdgeInGraph2(1));
		
		
	}

	@Test
	public void testIncludeEdgeMatching1() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		db.connect();
		
		Graph g1 = SampleGraphs.getSmallGraph3();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();
		GraphMatching m;

		nodeMatch.put(1,1);
		nodeMatch.put(2,2);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.includeEdgeMatching(m, edgeMatch);
		assertEquals(0, m.getAllEdgeMatches().size());
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(2, m.getAllNodeMatches().size());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),
				Pair.makePair(2, 2)), m.getAllNodeMatches());
		assertEquals(0, m.getAllEdgeMatches().size());
		assertEquals(1, m.getMatchedNodeInGraph1(1));
		assertEquals(1, m.getMatchedNodeInGraph2(1));
		assertEquals(2, m.getMatchedNodeInGraph1(2));
		assertEquals(2, m.getMatchedNodeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(1));
		assertEquals(-1, m.getMatchedEdgeInGraph2(1));
		
		
		edgeMatch.put(1, 1);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.includeEdgeMatching(m, edgeMatch);
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)), m.getAllEdgeMatches());
		
		
		edgeMatch.put(2, 2);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.includeEdgeMatching(m, edgeMatch);
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(2, m.getMatchedEdgeInGraph1(2));
		assertEquals(2, m.getMatchedEdgeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(3));
		assertEquals(-1, m.getMatchedEdgeInGraph2(3));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),Pair.makePair(2, 2)), m.getAllEdgeMatches());
	}

	@Test
	public void testInduceEdgeMatching() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		db.connect();
		
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		GraphMatching m;
		FeatureSetCompatibilityChecker fscc = 
				CompatibilityUtil.greedy1To1Checker(CompatibilityUtil.lexicalChecker());

		nodeMatch.put(1,1);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.induceEdgeMatching(m, fscc);
		assertEquals(0, m.getAllEdgeMatches().size());
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(1, m.getAllNodeMatches().size());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)), m.getAllNodeMatches());
		assertEquals(0, m.getAllEdgeMatches().size());
		assertEquals(1, m.getMatchedNodeInGraph1(1));
		assertEquals(1, m.getMatchedNodeInGraph2(1));
		assertEquals(-1, m.getMatchedNodeInGraph1(2));
		assertEquals(-1, m.getMatchedNodeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(1));
		assertEquals(-1, m.getMatchedEdgeInGraph2(1));
		
		

		nodeMatch.put(2,2);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.induceEdgeMatching(m, fscc);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),
				Pair.makePair(2, 2)), m.getAllNodeMatches());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)), m.getAllEdgeMatches());
		assertEquals(1, m.getMatchedNodeInGraph1(1));
		assertEquals(1, m.getMatchedNodeInGraph2(1));
		assertEquals(2, m.getMatchedNodeInGraph1(2));
		assertEquals(2, m.getMatchedNodeInGraph2(2));
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(-1, m.getMatchedEdgeInGraph1(2));
		assertEquals(-1, m.getMatchedEdgeInGraph2(2));
		

		nodeMatch.put(3,3);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.induceEdgeMatching(m, fscc);
		assertEquals(Sets.newHashSet(
				Pair.makePair(1, 1),
				Pair.makePair(2, 2)
				), m.getAllEdgeMatches());
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(2, m.getMatchedEdgeInGraph1(2));
		assertEquals(2, m.getMatchedEdgeInGraph2(2));
		assertEquals(-1, m.getMatchedEdgeInGraph1(3));
		assertEquals(-1, m.getMatchedEdgeInGraph2(3));
		

		nodeMatch.put(4,4);
		m = MatchingUtil.createBasicNodeMatching(g1, g2, nodeMatch);
		m = MatchingUtil.induceEdgeMatching(m, fscc);
		assertEquals(Sets.newHashSet(
				Pair.makePair(1, 1),
				Pair.makePair(3, 3),
				Pair.makePair(4, 4),
				Pair.makePair(2, 2)
				), m.getAllEdgeMatches());
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(2, m.getMatchedEdgeInGraph1(2));
		assertEquals(2, m.getMatchedEdgeInGraph2(2));
		assertEquals(3, m.getMatchedEdgeInGraph1(3));
		assertEquals(3, m.getMatchedEdgeInGraph2(3));
		assertEquals(4, m.getMatchedEdgeInGraph1(4));
		assertEquals(4, m.getMatchedEdgeInGraph2(4));
	}

	@Test
	public void testCreateGraphMatchOrdering() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		GraphMatchingHeuristic h = Heuristics.basicEdgeCount();
		db.connect();
		MatchingGenerator fakeGen = new MatchingGenerator(){

			@Override
			public GraphMatching apply(T2<Graph, Graph> args) {
				FeatureSetCompatibilityChecker fscc = 
						CompatibilityUtil.greedy1To1Checker(CompatibilityUtil.lexicalChecker());
				BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
				for(int nid : args.a1().getNodeIDs()) {
					if(args.a2().getNodeIDs().contains(nid))
						nodeMatch.put(nid, nid);  //this is complete BS...
					  //...but works for the examples since the ID's match
				}
				return MatchingUtil.induceEdgeMatching(
						MatchingUtil.createBasicNodeMatching(args.a1(), args.a2(), nodeMatch),
						fscc);
			}};
		Ordering<Graph> o = 
				MatchingUtil.createGraphMatchOrdering(SampleGraphs.getSmallGraph1(), fakeGen, h); 
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph2();
		Graph g3 = SampleGraphs.getSmallGraph4();
		List<Graph> ls = o.reverse() //descending instead of ascending order
				.sortedCopy(Sets.newHashSet(g3, g1, g2));
		assertTrue(ls.get(0) == g1);
		assertTrue(ls.get(1) == g2);
		assertTrue(ls.get(2) == g3);
				
	}


}
