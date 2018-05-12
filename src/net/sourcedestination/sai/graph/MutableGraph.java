package net.sourcedestination.sai.graph;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourcedestination.funcles.consumer.Consumer2;
import net.sourcedestination.funcles.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MutableGraph implements Graph {

    private final Set<Feature> features = Sets.newHashSet();
    private final Set<Integer> nodes = Sets.newHashSet();
    private final Set<Integer> edges = Sets.newHashSet();
    private final Map<Integer, Pair<Integer>> edgeContents = Maps.newHashMap();
    private final Multimap<Integer, Feature> nodeFeatures = HashMultimap.create();
    private final Multimap<Integer, Feature> edgeFeatures = HashMultimap.create();

    public MutableGraph() {

    }

    /**
     * creates a mutable graph from the given graph for editing purposes.
     *
     * @param g the graph to copy
     */
    public MutableGraph(Graph g) {
        g.getFeatures().forEach(this::addFeature);
        g.getNodeIDs().forEach(n -> {
            addNode(n);
            g.getNodeFeatures(n).forEach(f -> addNodeFeature(n, f));
        });
        g.getEdgeIDs().forEach(e -> {
            var fn = g.getEdgeSourceNodeID(e);
            var tn = g.getEdgeTargetNodeID(e);
            addEdge(e, fn, tn);
            g.getEdgeFeatures(e).forEach(f -> addEdgeFeature(e, f));
        });
    }

    @Override
    public Stream<Integer> getEdgeIDs() {
        return Sets.newHashSet(edges).stream();
    }

    @Override
    public Stream<Integer> getNodeIDs() {
        return Sets.newHashSet(nodes).stream();
    }

    @Override
    public Stream<Feature> getFeatures() {
        return Sets.newHashSet(features).stream();
    }

    @Override
    public int getEdgeSourceNodeID(int e) {
        return edgeContents.get(e)._1;
    }

    @Override
    public int getEdgeTargetNodeID(int e) {
        return edgeContents.get(e)._2;
    }

    public void addNode(final int nid) {
        if (nodes.contains(nid))
            throw new IllegalArgumentException(nid + " is already a node id");
        nodes.add(nid);
    }


    public void addEdge(final int eid, int n1, int n2) {
        if (edges.contains(eid))
            throw new IllegalArgumentException(eid + " is already an edge id");
        edges.add(eid);
        Pair<Integer> p = Pair.makePair(n1, n2);
        edgeContents.put(eid, p);
    }

    public void removeNode(int n) {
        nodes.remove(n);
        getEdgeIDs().forEach(e -> {
            if (getEdgeSourceNodeID(e) == n ||
                    getEdgeTargetNodeID(e) == n) {
                removeEdge(e);
            }
        });
    }

    public void removeEdge(int e) {
        edges.remove(e);
        edgeContents.remove(e);
    }

    public void addFeature(Feature f) {
        features.add(f);
    }

    public void addNodeFeature(int nodeID, Feature f) {
        nodeFeatures.put(nodeID, f);
    }

    public void addEdgeFeature(int edgeID, Feature f) {
        edgeFeatures.put(edgeID, f);
    }

    public void removeNodeFeature(int nodeID, Feature f) {
        nodeFeatures.remove(nodeID, f);
    }

    public void removeEdgeFeature(int edgeID, Feature f) {
        edgeFeatures.remove(edgeID, f);
    }

    @Override
    public Stream<Feature> getNodeFeatures(int n) {
        return Sets.newHashSet(nodeFeatures.get(n)).stream();
    }

    @Override
    public Stream<Feature> getEdgeFeatures(int n) {
        return Sets.newHashSet(edgeFeatures.get(n)).stream();
    }

    public void removeFeature(Feature f) {
        features.remove(f);
    }

    public void removeFeature(int e, Feature f) {
        edgeFeatures.remove(e, f);
    }

    @Override
    public String toString() {
        StringWriter sout = new StringWriter();
        PrintWriter out = new PrintWriter(sout);
        out.print(getNodeIDs().count() + ",");
        out.print(getEdgeIDs().count());
        getFeatures().sorted().forEach(f -> out.print("," + f));
        out.print("\n");
        //print a line for each node
        getNodeIDs().forEach(n -> {
            out.print(n);
            getNodeFeatures(n).sorted().forEach(f -> out.print("," + f));
            out.print("\n");
        });
        //print a line for each edge
        getEdgeIDs().forEach(e -> {
            out.print(e + "," + getEdgeSourceNodeID(e) + "," + getEdgeTargetNodeID(e));
            getEdgeFeatures(e).sorted().forEach(f -> out.print("," + f));
            out.print("\n");
        });
        return sout.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MutableGraph) && o.toString().equals(toString());
    }

}
