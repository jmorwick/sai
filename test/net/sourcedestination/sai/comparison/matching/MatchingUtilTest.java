package net.sourcedestination.sai.comparison.matching;

import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.createBasicNodeMatching;
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.includeEdgeMatching;
import static net.sourcedestination.sai.comparison.matching.MatchingGenerator.induceEdgeMatching;
import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;
import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.comparison.compatibility.EdgeCompatibilityChecker;
import net.sourcedestination.sai.comparison.compatibility.FeatureCompatibilityChecker;
import net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityCheckers;
import net.sourcedestination.sai.comparison.compatibility.NodeCompatabilityChecker;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.SampleGraphs;

import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

public class MatchingUtilTest {

    private final Predicate2<Set<Feature>,Set<Feature>> GREEDY1TO1 = (fs1, fs2) ->
            FeatureSetCompatibilityCheckers.checkFeaturesGreedy1To1(
                    FeatureCompatibilityChecker::areLexicallyCompatible, fs1, fs2);



    @Test
    public void testMatchingPossibilities() throws AccessDeniedException {
        Graph graph1 = SampleGraphs.getSmallGraph1();
        Graph graph2 = SampleGraphs.getSmallGraph2();
        Graph graph5 = SampleGraphs.getSmallGraph5();
        Graph graph6 = SampleGraphs.getSmallGraph6();
        NodeCompatabilityChecker<Graph> ncc = NodeCompatabilityChecker
                .useGenericFeatureChecker(FeatureSetCompatibilityCheckers::checkFeaturesGreedyManyTo1);
       Multimap<Integer,Integer> possibilities = null;

        possibilities = MatchingGenerator.getNodeMatchingPossibilities(ncc, graph1, graph1);
        assertEquals(Sets.newHashSet(1), possibilities.get(1));
        assertEquals(Sets.newHashSet(2), possibilities.get(2));
        assertEquals(Sets.newHashSet(3), possibilities.get(3));
        assertEquals(Sets.newHashSet(4), possibilities.get(4));

        possibilities = MatchingGenerator.getNodeMatchingPossibilities(ncc, graph5, graph5);
        assertTrue(ncc.apply(graph5, graph5, 1, 1));
        assertTrue(ncc.apply(graph5, graph5, 2, 2));
        assertTrue(ncc.apply(graph5, graph5, 2, 3));
        assertTrue(ncc.apply(graph5, graph5, 3, 2));
        assertTrue(ncc.apply(graph5, graph5, 3, 3));
        assertTrue(ncc.apply(graph5, graph5, 4, 4));
        assertEquals(Sets.newHashSet(1), possibilities.get(1));
        assertEquals(Sets.newHashSet(2, 3), possibilities.get(2));
        assertEquals(Sets.newHashSet(2, 3), possibilities.get(3));
        assertEquals(Sets.newHashSet(4), possibilities.get(4));
    }

    @Test
    public void testBasicNodeMatching() throws AccessDeniedException {
        DBInterface db = SampleDBs.getEmptyDB();
        Graph g1 = SampleGraphs.getSmallGraph1();
        Graph g2 = SampleGraphs.getSmallGraph3();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();

		GraphMatching m = createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(0, m.getAllNodeMatches().count());
		assertEquals(0, m.getAllEdgeMatches().count());
		assertEquals(null, m.getMatchedNodeInGraph1(1));
		assertEquals(null, m.getMatchedNodeInGraph2(1));
		assertEquals(null, m.getMatchedNodeInGraph1(2));
		assertEquals(null, m.getMatchedNodeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(1));
		assertEquals(null, m.getMatchedEdgeInGraph2(1));
		
		nodeMatch.put(1,2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(1, m.getAllNodeMatches().count());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 2)),
                m.getAllNodeMatches().collect(Collectors.toSet()));
		assertEquals(0, m.getAllEdgeMatches().count());
		assertEquals(null, m.getMatchedNodeInGraph1(1));
		assertEquals((Integer)2, m.getMatchedNodeInGraph2(1));
		assertEquals((Integer)1, m.getMatchedNodeInGraph1(2));
		assertEquals(null, m.getMatchedNodeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(1));
		assertEquals(null, m.getMatchedEdgeInGraph2(1));
		
		nodeMatch.put(2,1);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(2, m.getAllNodeMatches().count());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 2),
				Pair.makePair(2, 1)),
                m.getAllNodeMatches().collect(Collectors.toSet()));
		assertEquals(0, m.getAllEdgeMatches().count());
		assertEquals((Integer)2, m.getMatchedNodeInGraph1(1));
		assertEquals((Integer)2, m.getMatchedNodeInGraph2(1));
		assertEquals((Integer)1, m.getMatchedNodeInGraph1(2));
		assertEquals((Integer)1, m.getMatchedNodeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(1));
		assertEquals(null, m.getMatchedEdgeInGraph2(1));
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
		assertEquals(0, m.getAllEdgeMatches().count());
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(2, m.getAllNodeMatches().count());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),
				Pair.makePair(2, 2)),
                m.getAllNodeMatches().collect(Collectors.toSet()));
		assertEquals(0, m.getAllEdgeMatches().count());
		assertEquals((Integer)1, m.getMatchedNodeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedNodeInGraph2(1));
		assertEquals((Integer)2, m.getMatchedNodeInGraph1(2));
		assertEquals((Integer)2, m.getMatchedNodeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(1));
		assertEquals(null, m.getMatchedEdgeInGraph2(1));
		
		
		edgeMatch.put(1, 1);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
		assertEquals((Integer)1, m.getMatchedEdgeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph2(1));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)),
                m.getAllEdgeMatches().collect(Collectors.toSet()));
		
		
		edgeMatch.put(2, 2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
		assertEquals((Integer)1, m.getMatchedEdgeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph2(1));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph1(2));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(3));
		assertEquals(null, m.getMatchedEdgeInGraph2(3));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),
                Pair.makePair(2, 2)),
                m.getAllEdgeMatches().collect(Collectors.toSet()));
	}

	@Test
	public void testInduceEdgeMatching() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		
		Graph g1 = SampleGraphs.getSmallGraph1();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		GraphMatching m;
		nodeMatch.put(1,1);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, EdgeCompatibilityChecker.useGenericFeatureChecker(GREEDY1TO1));
		assertEquals(0, m.getAllEdgeMatches().count());
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(1, m.getAllNodeMatches().count());
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)),
                m.getAllNodeMatches().collect(Collectors.toSet()));
		assertEquals(0, m.getAllEdgeMatches().count());
		assertEquals((Integer)1, m.getMatchedNodeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedNodeInGraph2(1));
		assertEquals(null, m.getMatchedNodeInGraph1(2));
		assertEquals(null, m.getMatchedNodeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(1));
		assertEquals(null, m.getMatchedEdgeInGraph2(1));

		nodeMatch.put(2,2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, EdgeCompatibilityChecker.useGenericFeatureChecker(GREEDY1TO1));
		assertTrue(m.getGraph1() == g1);
		assertTrue(m.getGraph2() == g2);
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1),
				Pair.makePair(2, 2)),
                m.getAllNodeMatches().collect(Collectors.toSet()));
		assertEquals(Sets.newHashSet(Pair.makePair(1, 1)),
                m.getAllEdgeMatches().collect(Collectors.toSet()));
		assertEquals((Integer)1, m.getMatchedNodeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedNodeInGraph2(1));
		assertEquals((Integer)2, m.getMatchedNodeInGraph1(2));
		assertEquals((Integer)2, m.getMatchedNodeInGraph2(2));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph2(1));
		assertEquals(null, m.getMatchedEdgeInGraph1(2));
		assertEquals(null, m.getMatchedEdgeInGraph2(2));
		

		nodeMatch.put(3,3);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, EdgeCompatibilityChecker.useGenericFeatureChecker(GREEDY1TO1));
		assertEquals(Sets.newHashSet(
				Pair.makePair(1, 1),
				Pair.makePair(2, 2)
				), m.getAllEdgeMatches().collect(Collectors.toSet()));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph2(1));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph1(2));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph2(2));
		assertEquals(null, m.getMatchedEdgeInGraph1(3));
		assertEquals(null, m.getMatchedEdgeInGraph2(3));
		

		nodeMatch.put(4,4);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = induceEdgeMatching(m, EdgeCompatibilityChecker.useGenericFeatureChecker(GREEDY1TO1));
		assertEquals(Sets.newHashSet(
				Pair.makePair(1, 1),
				Pair.makePair(3, 3),
				Pair.makePair(4, 4),
				Pair.makePair(2, 2)
				), m.getAllEdgeMatches().collect(Collectors.toSet()));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph1(1));
		assertEquals((Integer)1, m.getMatchedEdgeInGraph2(1));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph1(2));
		assertEquals((Integer)2, m.getMatchedEdgeInGraph2(2));
		assertEquals((Integer)3, m.getMatchedEdgeInGraph1(3));
		assertEquals((Integer)3, m.getMatchedEdgeInGraph2(3));
		assertEquals((Integer)4, m.getMatchedEdgeInGraph1(4));
		assertEquals((Integer)4, m.getMatchedEdgeInGraph2(4));
	}

	@Test
	public void testGetMapQuality() throws AccessDeniedException {
        Graph graph1 = SampleGraphs.getSmallGraph1();
        Graph graph2 = SampleGraphs.getSmallGraph2();
        Graph graph5 = SampleGraphs.getSmallGraph5();
        Graph graph6 = SampleGraphs.getSmallGraph6();
        Graph graph7 = SampleGraphs.getSmallGraph7();
        Graph graph8 = SampleGraphs.getSmallGraph8();

		NodeCompatabilityChecker<Graph> ncc = NodeCompatabilityChecker
				.useGenericFeatureChecker(FeatureSetCompatibilityCheckers::checkFeaturesGreedyManyTo1);

		EdgeCompatibilityChecker<Graph> ecc = EdgeCompatibilityChecker
				.useGenericFeatureChecker(FeatureSetCompatibilityCheckers::checkFeaturesGreedyManyTo1);

		MatchingGenerator<Graph> gen = (g1, g2) -> MatchingGenerator.generateAllMatchings(g1, g2, ncc, ecc);

		MatchingEvaluator<Graph> eval = new EdgeCountMatchingEvaluator();

		double matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph1, graph1), eval);
		assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.999999 & matchQuality < 1.00000001);

		matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph2, graph2), eval);
		assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.999999 & matchQuality < 1.00000001);

		matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph1, graph2), eval);
		assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.74999999 & matchQuality < 0.7500000001);


        matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph5, graph5), eval);
        assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.999999 & matchQuality < 1.00000001);

        Set<GraphMatching<Graph>> mp = gen.apply(graph5, graph6).collect(Collectors.toSet());
        matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph5, graph6), eval);
        assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.999999 & matchQuality < 1.00000001);

        mp = gen.apply(graph7, graph8).collect(Collectors.toSet());
        matchQuality = MatchingEvaluator.getMapQuality(gen.apply(graph5, graph6), eval);
        assertTrue("matchQuality unexpected: " + matchQuality,
                matchQuality > 0.999999 & matchQuality < 1.00000001);
	}


	@Test
	public void testGetClosestGraphs() throws AccessDeniedException {

	}

}
