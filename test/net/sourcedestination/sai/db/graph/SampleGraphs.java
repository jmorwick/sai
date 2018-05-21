package net.sourcedestination.sai.db.graph;

import static net.sourcedestination.sai.db.graph.Graph.*;
import static org.junit.Assert.assertEquals;

import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

public class SampleGraphs {

    public static Graph getSmallGraph1() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "c"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, new Feature("test", "a"));
        g.addEdge(3, 3, 4);
        g.addEdgeFeature(3, new Feature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addEdgeFeature(4, new Feature("test", "a"));
        return g;
    }


    public static Graph getSmallGraph2() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "c"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, new Feature("test", "a"));
        g.addEdge(3, 1, 4);
        g.addEdgeFeature(3, new Feature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addEdgeFeature(4, new Feature("test", "a"));
        return g;
    }


    static Graph getMultigraph1() {

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "c"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "d"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, new Feature("test", "b"));
        g.addEdge(3, 1, 2);
        g.addEdgeFeature(3, new Feature("test", "a"));
        g.addEdge(4, 2, 4);
        g.addEdgeFeature(4, new Feature("test", "a"));
        return g;
    }


    public static void assertGraphsAreIdentical(Graph pg1, Graph pg2) {
        MutableGraph g1 = new MutableGraph(pg1);
        MutableGraph g2 = new MutableGraph(pg2);
        if (getFeature(g1.getFeatures(), "sai-id") != null)
            g1.removeFeature(getFeature(g1.getFeatures(), "sai-id"));
        if (getFeature(g1.getFeatures(), "indexed-by") != null)
            g1.removeFeature(getFeature(g1.getFeatures(), "indexed-by"));
        if (getFeature(g1.getFeatures(), "indexes") != null)
            g1.removeFeature(getFeature(g1.getFeatures(), "indexes"));
        if (getFeature(g1.getFeatures(), "index") != null)
            g1.removeFeature(getFeature(g1.getFeatures(), "index"));

        if (getFeature(g2.getFeatures(), "sai-id") != null)
            g2.removeFeature(getFeature(g2.getFeatures(), "sai-id"));
        if (getFeature(g2.getFeatures(), "indexed-by") != null)
            g2.removeFeature(getFeature(g2.getFeatures(), "indexed-by"));
        if (getFeature(g2.getFeatures(), "indexes") != null)
            g2.removeFeature(getFeature(g2.getFeatures(), "indexes"));
        if (getFeature(g2.getFeatures(), "index") != null)
            g2.removeFeature(getFeature(g2.getFeatures(), "index"));

        assertEquals(
                g1.getNodeIDs().collect(toSet()),
                g2.getNodeIDs().collect(toSet()));
        assertEquals(
                g1.getEdgeIDs().collect(toSet()),
                g2.getEdgeIDs().collect(toSet()));
        Predicate<Feature> p =
                f ->
                        !f.getName().equals(SAI_ID_NAME) &&
                                !f.getName().equals(INDEXES_FEATURE_NAME);
        assertEquals(
                g1.getFeatures().filter(p).collect(toSet()),
                g2.getFeatures().filter(p).collect(toSet()));

        g1.getNodeIDs().forEach(nodeID ->
                assertEquals(g1.getNodeFeatures(nodeID).collect(toSet()),
                        g2.getNodeFeatures(nodeID).collect(toSet())));

        g1.getEdgeIDs().forEach(edgeID -> {
            assertEquals(g1.getNodeFeatures(edgeID).collect(toSet()),
                    g2.getNodeFeatures(edgeID).collect(toSet()));
            assertEquals(g1.getEdgeSourceNodeID(edgeID),
                    g2.getEdgeSourceNodeID(edgeID));
            assertEquals(g1.getEdgeTargetNodeID(edgeID),
                    g2.getEdgeTargetNodeID(edgeID));
        });
    }


    public static String toString(Graph g1) {
        StringBuilder ret = new StringBuilder();
        ret.append("nodes: ").append(g1.getNodeIDs()).append("\n")
                .append("edges: ").append(g1.getEdgeIDs()).append("\n")
                .append("features: ").append(g1.getFeatures()).append("\n");
        g1.getNodeIDs().forEach(nodeID ->
                ret.append("node #").append(nodeID).append(": ")
                        .append(g1.getNodeFeatures(nodeID)));

        g1.getNodeIDs().forEach(edgeID ->
                ret.append("edge #").append(edgeID).append("(")
                        .append(g1.getEdgeSourceNodeID(edgeID))
                        .append(",").append(g1.getEdgeTargetNodeID(edgeID))
                        .append("): ").append(g1.getNodeFeatures(edgeID)));
        return ret.toString();
    }

    public static Graph getSmallGraph3() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        return g;
    }


    public static Graph getMediumUnlabeledTreeOneSelfIso() {
        MutableGraph g = new MutableGraph();
        g.addFeature(TREE);
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addEdge(1, 1, 2);
        g.addEdge(2, 1, 3);
        g.addNode(4);
        g.addNode(5);
        g.addEdge(3, 3, 4);
        g.addEdge(4, 3, 5);
        g.addNode(6);
        g.addEdge(5, 5, 6);
        return g;
    }


    public static Graph getSmallSymmetricTree() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "b"));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 1, 3);
        g.addEdgeFeature(2, new Feature("test", "a"));
        return g;
    }


    public static Graph getSmallGraph4() {
        MutableGraph g = new MutableGraph();
        g.addNode(30);
        g.addNodeFeature(30, new Feature("test", "a"));
        g.addNode(40);
        g.addNodeFeature(40, new Feature("test", "b"));
        g.addNodeFeature(40, new Feature("test", "d"));
        g.addEdge(10, 30, 40);
        g.addEdgeFeature(10, new Feature("test", "a"));
        return g;
    }

    public static Graph getOneEdgeIndex(String n1Val, String n2Val, String edgeVal) {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", n1Val));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", n2Val));
        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", edgeVal));

        g.addFeature(INDEX);
        return g;
    }


    public static Graph getSmallGraph5() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "b"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "c"));

        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, new Feature("test", "a"));
        g.addEdge(3, 3, 2);
        g.addEdgeFeature(3, new Feature("test", "a"));
        g.addEdge(4, 4, 3);
        g.addEdgeFeature(4, new Feature("test", "a"));
        return g;
    }


    public static Graph getSmallGraph6() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "c"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "b"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "b"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdgeFeature(1, new Feature("test", "a"));
        g.addEdge(2, 2, 3);
        g.addEdgeFeature(2, new Feature("test", "a"));
        g.addEdge(3, 3, 2);
        g.addEdgeFeature(3, new Feature("test", "a"));
        g.addEdge(4, 4, 3);
        g.addEdgeFeature(4, new Feature("test", "a"));
        return g;
    }


    public static Graph getSmallGraph7() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "a"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "a"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdge(2, 2, 3);
        g.addEdge(3, 3, 4);
        g.addEdge(4, 4, 1);


        g.addEdge(5, 1, 4);
        g.addEdge(6, 4, 3);
        g.addEdge(7, 3, 2);
        g.addEdge(8, 2, 1);

        g.addEdge(9, 1, 3);
        g.addEdge(10, 3, 1);
        return g;
    }

    public static Graph getSmallGraph8() {
        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "a"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "a"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdge(2, 2, 3);
        g.addEdge(3, 3, 4);
        g.addEdge(4, 4, 1);


        g.addEdge(5, 1, 4);
        g.addEdge(6, 4, 3);
        g.addEdge(7, 3, 2);
        g.addEdge(8, 2, 1);

        g.addEdge(9, 2, 4);
        g.addEdge(10, 4, 2);
        return g;
    }

    public static Graph getSmallGraph9() {
        // sample graph from: http://qasimpasta.info/data/uploads/sina-2015/calculating-clustering-coefficient.pdf

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "a"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "a"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdge(2, 2, 1);
        g.addEdge(3, 2, 3);
        g.addEdge(4, 3, 2);
        g.addEdge(5, 3, 1);
        g.addEdge(6, 1, 3);
        g.addEdge(7, 3, 4);
        g.addEdge(8, 4, 3);
        return g;
    }

    public static Graph getSmallGraph10() {
        // sample graph from: http://qasimpasta.info/data/uploads/sina-2015/calculating-clustering-coefficient.pdf

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "a"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "a"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));
        g.addNode(5);
        g.addNodeFeature(5, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdge(2, 2, 1);
        g.addEdge(3, 2, 3);
        g.addEdge(4, 3, 2);
        g.addEdge(5, 3, 1);
        g.addEdge(6, 1, 3);
        g.addEdge(7, 3, 4);
        g.addEdge(8, 4, 3);
        g.addEdge(9, 2, 5);
        g.addEdge(10, 5, 2);
        g.addEdge(11, 3, 5);
        g.addEdge(12, 5, 3);
        return g;
    }

    public static Graph getSmallGraph11() {
        // sample graph from: http://qasimpasta.info/data/uploads/sina-2015/calculating-clustering-coefficient.pdf

        MutableGraph g = new MutableGraph();
        g.addNode(1);
        g.addNodeFeature(1, new Feature("test", "a"));
        g.addNode(2);
        g.addNodeFeature(2, new Feature("test", "a"));
        g.addNode(3);
        g.addNodeFeature(3, new Feature("test", "a"));
        g.addNode(4);
        g.addNodeFeature(4, new Feature("test", "a"));
        g.addNode(5);
        g.addNodeFeature(5, new Feature("test", "a"));
        g.addNode(6);
        g.addNodeFeature(6, new Feature("test", "a"));

        g.addEdge(1, 1, 2);
        g.addEdge(2, 2, 1);
        g.addEdge(3, 2, 3);
        g.addEdge(4, 3, 2);
        g.addEdge(5, 3, 1);
        g.addEdge(6, 1, 3);
        g.addEdge(7, 3, 4);
        g.addEdge(8, 4, 3);
        g.addEdge(9, 2, 5);
        g.addEdge(10, 5, 2);
        g.addEdge(11, 3, 5);
        g.addEdge(12, 5, 3);
        g.addEdge(13, 4, 6);
        g.addEdge(14, 6, 4);
        return g;
    }

}
