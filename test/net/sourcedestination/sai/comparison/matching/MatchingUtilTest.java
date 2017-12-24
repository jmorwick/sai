package net.sourcedestination.sai.comparison.matching;
/*
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.createBasicNodeMatching;
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.createGraphMatchOrdering;
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.includeEdgeMatching;
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.induceEdgeMatching;
import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.comparison.distance.GraphMatchingDistance;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.SampleGraphs;

import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
*/
public class MatchingUtilTest {
	/*
	@Test
	public void testBasicNodeMatching() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph3();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();

		GraphMatching m = createBasicNodeMatching(g1, g2, nodeMatch);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
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
		
		Graph g1 = SampleGraphs.getSmallGraph3();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();
		GraphMatching m;

		nodeMatch.put(1,1);
		nodeMatch.put(2,2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
		assertEquals(1, m.getMatchedEdgeInGraph1(1));
		assertEquals(1, m.getMatchedEdgeInGraph2(1));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)), m.getAllEdgeMatches());
		
		
		edgeMatch.put(2, 2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
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
		
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		GraphMatching m;
		FeatureSetCompatibilityCheckers fscc =
				greedy1To1Checker(FeatureCompatibilityChecker::areLexicallyCompatible);

		nodeMatch.put(1,1);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, fscc);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, fscc);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, fscc);
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
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, fscc);
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
		GraphMatchingDistance h = GraphMatchingDistance::basicEdgeCount;
		MatchingGenerator fakeGen = 
			(g1, g2) -> {
				FeatureSetCompatibilityCheckers fscc =
						greedy1To1Checker(
								FeatureCompatibilityChecker::areLexicallyCompatible);
				BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
				g1.getNodeIDs().forEach(nid-> {
					if(g2.getNodeIDs().anyMatch(n-> Objects.equals(n, nid)))
						nodeMatch.put(nid, nid);  //this is complete BS...
					  //...but works for the examples since the ID's match
				});
				return induceEdgeMatching(
						createBasicNodeMatching(g1, g2, nodeMatch),
						fscc);
		};
		Comparator<Graph> o = 
				createGraphMatchOrdering(SampleGraphs.getSmallGraph1(), fakeGen, h); 
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph2();
		Graph g3 = SampleGraphs.getSmallGraph4();
		List<Graph> ls = Sets.newHashSet(g3, g1, g2).stream()
				.sorted((x,y) -> -o.compare(x,y)) //reverse order of comparator
				.collect(Collectors.toList()); //collect in to an ordered list
		assertTrue(ls.get(0) == g1);
		assertTrue(ls.get(1) == g2);
		assertTrue(ls.get(2) == g3);
				
	}
*/

}
