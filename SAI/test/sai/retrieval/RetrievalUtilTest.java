package sai.retrieval;

import static org.junit.Assert.*;

import org.junit.Test;

import static info.kendall_morwick.funcles.Funcles.apply;
import sai.comparison.compatibility.CompatibilityUtil;
import sai.comparison.heuristics.Heuristics;
import sai.comparison.matching.GraphMatching;
import sai.comparison.matching.MatchingGenerator;
import sai.comparison.matching.MatchingUtil;
import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.BasicGraphFactory;
import sai.graph.Graph;
import sai.graph.SampleGraphs;

public class RetrievalUtilTest {

	@Test
	public void testBasicCountRetriever() {
		//TODO: create a sample DB with some indexing in it
		//TODO: create a few queries and check w/ sampling DB and basic count retriever for accuracy
		fail("Not yet implemented");
	}

	private void selfTest(Graph g, MatchingGenerator gen) {
		GraphMatching m = apply(gen, g, g);

		System.out.println(m.getAllNodeMatches());
		System.out.println(m.getAllEdgeMatches());
		
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
		fail("Not yet implemented");
	}

	@Test
	public void testCompleteMatchingGeneratorAgainstSimilar() {
		//TODO: try to match an assortment of graphs that don't match exactly and check for proper maximal matching
		fail("Not yet implemented");
	}
	
	@Test
	public void testBuild2PhasedRetriever() {
		//TODO: use test DB from first test w/ mistakes in indexing and see that complete checker corrects them
		fail("Not yet implemented");
	}
	
	//TODO: create an index retriever interface an integrate in to the 2-phase retriever framework
	//TODO: test this with a basic implementation that looks up 

}
