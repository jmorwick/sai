package sai.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.Sets;
import static sai.graph.SampleGraphs.assertGraphsAreIdentical;

public class BasicGraphFactoryTest {

	@Test
	public void testCopyGraph() {
		MutableGraph g = new MutableGraph();
		g.addNode(2);
		g.addNode(3);
		g.addEdge(1, 2, 3);
		g.addNode(4);
		g.addEdge(2, 3, 4);
		g.addEdgeFeature(1, new Feature("test", "a"));
		g.addEdgeFeature(2, new Feature("test", "a"));
		g.addNodeFeature(2, new Feature("test", "a"));
		g.addNodeFeature(3, new Feature("test", "b"));
		g.addNodeFeature(4, new Feature("test", "c"));
		BasicGraphFactory gf = new BasicGraphFactory();
		System.out.println(SampleGraphs.toString(g));
		System.out.println(SampleGraphs.toString(gf.copy(g)));
		assertGraphsAreIdentical(g, gf.copy(g));
	}

	@Test
	public void testCopyGraphNewID() {
		MutableGraph g = new MutableGraph();
		g.setID(31);
		g.addNode(1);
		g.addNode(2);
		g.addEdge(1, 1, 2);
		g.addEdgeFeature(1, new Feature("test", "a"));
		g.addNodeFeature(1, new Feature("test", "a"));
		g.addNodeFeature(2, new Feature("test", "b"));
		BasicGraphFactory gf = new BasicGraphFactory();
		System.out.println(SampleGraphs.toString(g));
		System.out.println(SampleGraphs.toString(gf.copy(g, 322)));
		assertGraphsAreIdentical(g, gf.copy(g));
		assertEquals(31, g.getSaiID());
		assertEquals(322, gf.copy(g, 322).getSaiID());
	}

}
