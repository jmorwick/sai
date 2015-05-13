package sai.graph;

import java.util.stream.Stream;

public interface Graph {
	// TODO: consider making all Sets into Streams
	public Stream<Integer> getEdgeIDs();
	public Stream<Integer> getNodeIDs();
	public Stream<Feature> getFeatures();
	public Stream<Feature> getNodeFeatures(int n);
	public Stream<Feature> getEdgeFeatures(int e);
	public int getEdgeSourceNodeID(int edgeID);
	public int getEdgeTargetNodeID(int edgeID);
}
