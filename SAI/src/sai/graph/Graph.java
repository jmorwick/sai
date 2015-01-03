package sai.graph;

import java.util.Set;

public interface Graph {
	public Set<Integer> getEdgeIDs();
	public Set<Integer> getNodeIDs();
	public Set<Feature> getFeatures();
	public Set<Feature> getNodeFeatures(int n);
	public Set<Feature> getEdgeFeatures(int e);
	public int getEdgeSourceNodeID(int edgeID);
	public int getEdgeTargetNodeID(int edgeID);
}
