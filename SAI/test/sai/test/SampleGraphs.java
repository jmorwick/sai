package sai.test;

import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.MutableGraph;


public class SampleGraphs {

    public static Graph getSmallGraph1(DBInterface db) {
      MutableGraph g = new MutableGraph();
      g.addNode(1);
      g.addFeature(1, db.getFeature("test", "a"));
      g.addNode(2);
      g.addFeature(2, db.getFeature("test", "b"));
      g.addNode(3);
      g.addFeature(3, db.getFeature("test", "c"));
      g.addNode(4);
      g.addFeature(4, db.getFeature("test", "d"));
      g.addEdge(1, 1, 2);
      g.addFeature(1, db.getFeature("test", "a"));
      g.addEdge(2, 2, 3);
      g.addFeature(2, db.getFeature("test", "a"));
      g.addEdge(3, 3, 4);
      g.addFeature(3, db.getFeature("test", "a"));
      g.addEdge(4, 2, 4);
      g.addFeature(4, db.getFeature("test", "a"));
      return g;
    }

    public static Graph getSmallGraph2(DBInterface db) {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addFeature(1, db.getFeature("test", "a"));
        g.addNode(2);
        g.addFeature(2, db.getFeature("test", "b"));
        g.addNode(3);
        g.addFeature(3, db.getFeature("test", "c"));
        g.addNode(4);
        g.addFeature(4, db.getFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addFeature(1, db.getFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addFeature(2, db.getFeature("test", "a"));
        g.addEdge(3, 1, 4);
        g.addFeature(3, db.getFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addFeature(4, db.getFeature("test", "a"));
        return g;
    }


    public static Graph getMultigraph1(DBInterface db) {

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addFeature(1, db.getFeature("test", "a"));
        g.addNode(2);
        g.addFeature(2, db.getFeature("test", "b"));
        g.addNode(3);
        g.addFeature(3, db.getFeature("test", "c"));
        g.addNode(4);
        g.addFeature(4, db.getFeature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addFeature(1, db.getFeature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addFeature(2, db.getFeature("test", "b"));
        g.addEdge(3, 1, 2);
        g.addFeature(3, db.getFeature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addFeature(4, db.getFeature("test", "a"));
        return g;
    }


}
