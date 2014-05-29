package sai.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.Sets;

import sai.db.DBInterface;
import sai.test.SampleDBs;
import sai.test.SampleGraphs;

public class MutableGraphTest {
	
	private static DBInterface db = SampleDBs.getEmptyDB(null);

	@Test
	public void testGeneralFeatureFunctionality() {
		MutableGraph g = new MutableGraph();
		assertEquals(Sets.newHashSet(), g.getFeatures());
		g.addFeature(db.getFeature("a", "1"));
		assertEquals(Sets.newHashSet(
				db.getFeature("a", "1")
				), g.getFeatures());
		g.addFeature(db.getFeature("b", "2"));
		assertEquals(Sets.newHashSet(
				db.getFeature("a", "1"),
				db.getFeature("b", "2")
				), g.getFeatures());
		g.addFeature(db.getFeature("b", "3"));
		assertEquals(Sets.newHashSet(
				db.getFeature("a", "1"),
				db.getFeature("b", "2"),
				db.getFeature("b", "3")
				), g.getFeatures());
		g.addFeature(db.getFeature("b", "2"));
		assertEquals(Sets.newHashSet(
				db.getFeature("a", "1"),
				db.getFeature("b", "2"),
				db.getFeature("b", "3")
				), g.getFeatures());
	}

	@Test
	public void testNodeFunctionality() {
		MutableGraph g = new MutableGraph(SampleGraphs.getSmallGraph1(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getNodeIDs());
		g = new MutableGraph(SampleGraphs.getSmallGraph2(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getNodeIDs());
		g = new MutableGraph(SampleGraphs.getMultigraph1(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getNodeIDs());
	}

	@Test
	public void testEdgeFunctionality() {
		MutableGraph g = new MutableGraph(SampleGraphs.getSmallGraph1(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getEdgeIDs());
		assertEquals(1, g.getEdgeSourceNodeID(1));
		assertEquals(2, g.getEdgeTargetNodeID(1));
		assertEquals(2, g.getEdgeSourceNodeID(2));
		assertEquals(3, g.getEdgeTargetNodeID(2));
		assertEquals(3, g.getEdgeSourceNodeID(3));
		assertEquals(4, g.getEdgeTargetNodeID(3));
		assertEquals(2, g.getEdgeSourceNodeID(4));
		assertEquals(4, g.getEdgeTargetNodeID(4));
		g = new MutableGraph(SampleGraphs.getSmallGraph2(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getEdgeIDs());
		assertEquals(1, g.getEdgeSourceNodeID(1));
		assertEquals(2, g.getEdgeTargetNodeID(1));
		assertEquals(2, g.getEdgeSourceNodeID(2));
		assertEquals(3, g.getEdgeTargetNodeID(2));
		assertEquals(1, g.getEdgeSourceNodeID(3));
		assertEquals(4, g.getEdgeTargetNodeID(3));
		assertEquals(2, g.getEdgeSourceNodeID(4));
		assertEquals(4, g.getEdgeTargetNodeID(4));
		g = new MutableGraph(SampleGraphs.getMultigraph1(db));
		assertEquals(Sets.newHashSet(1, 2, 3, 4), g.getEdgeIDs());
		assertEquals(1, g.getEdgeSourceNodeID(1));
		assertEquals(2, g.getEdgeTargetNodeID(1));
		assertEquals(2, g.getEdgeSourceNodeID(2));
		assertEquals(3, g.getEdgeTargetNodeID(2));
		assertEquals(1, g.getEdgeSourceNodeID(3));
		assertEquals(2, g.getEdgeTargetNodeID(3));
		assertEquals(2, g.getEdgeSourceNodeID(4));
		assertEquals(4, g.getEdgeTargetNodeID(4));
	}

	@Test
	public void testMutators() {
		MutableGraph g = new MutableGraph();
		assertEquals(Sets.newHashSet(), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.addNode(1);
		assertEquals(Sets.newHashSet(1), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.addNode(2);
		assertEquals(Sets.newHashSet(1, 2), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.removeNode(1);
		assertEquals(Sets.newHashSet(2), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.addNode(3);
		assertEquals(Sets.newHashSet(2, 3), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.addEdge(1, 2, 3);
		assertEquals(2, g.getEdgeSourceNodeID(1));
		assertEquals(3, g.getEdgeTargetNodeID(1));
		assertEquals(Sets.newHashSet(2, 3), g.getNodeIDs());
		assertEquals(Sets.newHashSet(1), g.getEdgeIDs());
		g.removeNode(2);
		assertEquals(Sets.newHashSet(3), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
		g.addNode(4);
		g.addEdge(2, 3, 4);
		assertEquals(3, g.getEdgeSourceNodeID(2));
		assertEquals(4, g.getEdgeTargetNodeID(2));
		assertEquals(Sets.newHashSet(3, 4), g.getNodeIDs());
		assertEquals(Sets.newHashSet(2), g.getEdgeIDs());
		g.removeEdge(2);
		assertEquals(Sets.newHashSet(3, 4), g.getNodeIDs());
		assertEquals(Sets.newHashSet(), g.getEdgeIDs());
	}

}
