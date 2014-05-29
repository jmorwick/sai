package sai.test;

import sai.graph.Graph;
import sai.graph.MutableGraph;


public class SampleGraphs {

    public static Graph getSmallGraph1() {
      MutableGraph g = new MutableGraph();
      g.addNode(1);
      g.addFeature(1, MutableGraph.createFeature("test", "a"));
      g.addNode(2);
      g.addFeature(2, MutableGraph.createFeature("test", "b"));
      g.addNode(3);
      g.addFeature(3, MutableGraph.createFeature("test", "c"));
      g.addNode(4);
      g.addFeature(4, MutableGraph.createFeature("test", "d"));
      g.addEdge(1, 1, 2);
      g.addFeature(1, MutableGraph.createFeature("test", "a"));
      g.addEdge(2, 2, 3);
      g.addFeature(2, MutableGraph.createFeature("test", "a"));
      g.addEdge(3, 3, 4);
      g.addFeature(3, MutableGraph.createFeature("test", "a"));
      g.addEdge(4, 2, 4);
      g.addFeature(4, MutableGraph.createFeature("test", "a"));
      return g;
    }

    public static Graph getSmallGraph2() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addFeature(1, MutableGraph.createFeature("test", "a"));
        g.addNode(2);
        g.addFeature(2, MutableGraph.createFeature("test", "b"));
        g.addNode(3);
        g.addFeature(3, MutableGraph.createFeature("test", "c"));
        g.addNode(4);
        g.addFeature(4, MutableGraph.createFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addFeature(1, MutableGraph.createFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addFeature(2, MutableGraph.createFeature("test", "a"));
        g.addEdge(3, 1, 4);
        g.addFeature(3, MutableGraph.createFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addFeature(4, MutableGraph.createFeature("test", "a"));
        return g;
    }


    public static Graph getMultigraph1() {

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addFeature(1, MutableGraph.createFeature("test", "a"));
        g.addNode(2);
        g.addFeature(2, MutableGraph.createFeature("test", "b"));
        g.addNode(3);
        g.addFeature(3, MutableGraph.createFeature("test", "c"));
        g.addNode(4);
        g.addFeature(4, MutableGraph.createFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addFeature(1, MutableGraph.createFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addFeature(2, MutableGraph.createFeature("test", "b"));
        g.addEdge(3, 1, 2);
        g.addFeature(3, MutableGraph.createFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addFeature(4, MutableGraph.createFeature("test", "a"));
        return g;
    }


}
