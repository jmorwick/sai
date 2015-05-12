package sai.graph;

import org.junit.Test;

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
		assertGraphsAreIdentical(g, new MutableGraph(g));
	}

}
