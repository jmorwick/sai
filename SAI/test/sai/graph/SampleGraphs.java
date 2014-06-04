package sai.graph;

import static org.junit.Assert.assertEquals;
import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.MutableGraph;


public class SampleGraphs {

    public static Graph getSmallGraph1(DBInterface db) {
      MutableGraph g = new MutableGraph();
      g.addNode(1);
      g.addNodeFeature(1, db.getFeature("test", "a"));
      g.addNode(2);
      g.addNodeFeature(2, db.getFeature("test", "b"));
      g.addNode(3);
      g.addNodeFeature(3, db.getFeature("test", "c"));
      g.addNode(4);
      g.addNodeFeature(4, db.getFeature("test", "d"));
      g.addEdge(1, 1, 2);
      g.addEdgeFeature(1, db.getFeature("test", "a"));
      g.addEdge(2, 2, 3);
      g.addEdgeFeature(2, db.getFeature("test", "a"));
      g.addEdge(3, 3, 4);
      g.addEdgeFeature(3, db.getFeature("test", "a"));
      g.addEdge(4, 2, 4);
      g.addEdgeFeature(4, db.getFeature("test", "a"));
      return g;
    }

    public static Graph getSmallGraph2(DBInterface db) {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, db.getFeature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, db.getFeature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, db.getFeature("test", "c"));
        g.addNode(4);
        g.addNodeFeature(4, db.getFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, db.getFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, db.getFeature("test", "a"));
        g.addEdge(3, 1, 4);
        g.addEdgeFeature(3, db.getFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addEdgeFeature(4, db.getFeature("test", "a"));
        return g;
    }


    public static Graph getMultigraph1(DBInterface db) {

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, db.getFeature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, db.getFeature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, db.getFeature("test", "c"));
        g.addNode(4);
        g.addNodeFeature(4, db.getFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, db.getFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, db.getFeature("test", "b"));
        g.addEdge(3, 1, 2);
        g.addEdgeFeature(3, db.getFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addEdgeFeature(4, db.getFeature("test", "a"));
        return g;
    }


	public static void assertGraphsAreIdentical(Graph g1, Graph g2) {
		assertEquals(g1.getNodeIDs(), g2.getNodeIDs());
		assertEquals(g1.getEdgeIDs(), g2.getEdgeIDs());
		assertEquals(g1.getFeatures(), g2.getFeatures());
		for(int nodeID : g1.getNodeIDs()) {
			assertEquals(g1.getNodeFeatures(nodeID), g2.getNodeFeatures(nodeID));
		}
		for(int edgeID : g1.getEdgeIDs()) {
			assertEquals(g1.getNodeFeatures(edgeID), g2.getNodeFeatures(edgeID));
			assertEquals(g1.getEdgeSourceNodeID(edgeID), 
				     g2.getEdgeSourceNodeID(edgeID));
			assertEquals(g1.getEdgeTargetNodeID(edgeID), 
				     g2.getEdgeTargetNodeID(edgeID));
		}
	}
	

	public static String toString(Graph g1) {
		String ret = "nodes: " + g1.getNodeIDs() + "\n";
		ret += "edges: " + g1.getEdgeIDs() + "\n";
		ret += "features: " + g1.getFeatures() + "\n";
		for(int nodeID : g1.getNodeIDs()) {
			ret += "node #" + nodeID + ": " + g1.getNodeFeatures(nodeID);
		}
		for(int edgeID : g1.getEdgeIDs()) {
			ret += "edge #" + edgeID + "(" + g1.getEdgeSourceNodeID(edgeID) + 
					"," + g1.getEdgeTargetNodeID(edgeID) +"): " + 
					g1.getNodeFeatures(edgeID);
		}
		return ret;
	}

	public static Graph getSmallGraph3(DBInterface db) {
	      MutableGraph g = new MutableGraph();
	      g.addNode(1);
	      g.addNodeFeature(1, db.getFeature("test", "a"));
	      g.addNode(2);
	      g.addNodeFeature(2, db.getFeature("test", "b"));
	      g.addNodeFeature(4, db.getFeature("test", "d"));
	      g.addEdge(1, 1, 2);
	      g.addEdgeFeature(1, db.getFeature("test", "a"));
	      return g;
	}


	public static Graph getSmallGraph4(DBInterface db) {
	      MutableGraph g = new MutableGraph();
	      g.addNode(30);
	      g.addNodeFeature(30, db.getFeature("test", "a"));
	      g.addNode(40);
	      g.addNodeFeature(40, db.getFeature("test", "b"));
	      g.addNodeFeature(40, db.getFeature("test", "d"));
	      g.addEdge(10, 30, 40);
	      g.addEdgeFeature(10, db.getFeature("test", "a"));
	      return g;
	}

}
