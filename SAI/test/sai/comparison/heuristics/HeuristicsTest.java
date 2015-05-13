package sai.comparison.heuristics;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;

import org.junit.Test;

import sai.comparison.matching.GraphMatching;
import static sai.comparison.matching.MatchingGenerator.createBasicNodeMatching;
import static sai.comparison.matching.MatchingGenerator.includeEdgeMatching;
import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.Graph;
import sai.graph.SampleGraphs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class HeuristicsTest {

	@Test
	public void testBasicEdgeCount() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		
		GraphMatchingHeuristic h = GraphMatchingHeuristic::basicEdgeCount;
		Graph g1 = SampleGraphs.getSmallGraph3();
		Graph g2 = SampleGraphs.getSmallGraph1();
		BiMap<Integer,Integer> nodeMatch = HashBiMap.create();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();
		GraphMatching m;

		nodeMatch.put(1,1);
		nodeMatch.put(2,2);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		assertEquals(0.0, h.apply(m), 0.0000000001);
		m = createBasicNodeMatching(g2, g1, nodeMatch.inverse());
		assertEquals(0.0, h.apply(m), 0.0000000001);
		
		edgeMatch.put(1, 1);
		m = createBasicNodeMatching(g1, g2, nodeMatch);
		m = includeEdgeMatching(m, edgeMatch);
		assertEquals(1.0, h.apply(m), 0.0000000001);
		m = createBasicNodeMatching(g2, g1, nodeMatch.inverse());
		m = includeEdgeMatching(m, edgeMatch);
		assertEquals(0.25, h.apply(m), 0.0000000001);
	}

}
