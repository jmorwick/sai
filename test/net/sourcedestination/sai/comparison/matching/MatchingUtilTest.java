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

import net.sourcedestination.funcles.predicate.Predicate2;
import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.comparison.compatibility.EdgeCompatibilityChecker;
import net.sourcedestination.sai.comparison.compatibility.FeatureCompatibilityChecker;
import net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityCheckers;
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

}
