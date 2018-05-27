package net.sourcedestination.sai.db.graph;

import java.util.stream.Stream;

public class GraphWrapper<G extends Graph> implements Graph {

    private G wrappedGraph;

    public GraphWrapper(G g) {
        this.wrappedGraph = g;
    }

    @Override
    public Stream<Integer> getEdgeIDs() { return wrappedGraph.getEdgeIDs(); }

    @Override
    public Stream<Integer> getNodeIDs() { return wrappedGraph.getNodeIDs(); }

    @Override
    public Stream<Feature> getFeatures() { return wrappedGraph.getFeatures(); }

    @Override
    public Stream<Feature> getNodeFeatures(int n) { return wrappedGraph.getNodeFeatures(n); }

    @Override
    public Stream<Feature> getEdgeFeatures(int e) { return getEdgeFeatures(e); }

    @Override
    public int getEdgeSourceNodeID(int edgeID) { return getEdgeSourceNodeID(edgeID); }

    @Override
    public int getEdgeTargetNodeID(int edgeID) { return getEdgeTargetNodeID(edgeID); }

    @Override
    public String toString() { return wrappedGraph.toString(); }

    @Override
    public int hashCode() { return wrappedGraph.hashCode(); }

    public G getWrappedGraph() { return wrappedGraph; }
}
