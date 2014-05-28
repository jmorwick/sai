package sai.graph;

import java.util.Set;

public class BasicGraphWrapper implements Graph {
	
	private Graph graph;

	public BasicGraphWrapper(Graph graph) {
		this.graph = graph;
	}

	@Override
	public int getSaiID() {
		return graph.getSaiID();
	}

	@Override
	public Set<Integer> getEdgeIDs() {
		return graph.getEdgeIDs();
	}

	@Override
	public Set<Integer> getNodeIDs() {
		return graph.getNodeIDs();
	}

	@Override
	public Set<Feature> getFeatures() {
		return graph.getFeatures();
	}

	@Override
	public int getEdgeSourceNodeID(int e) {
		return graph.getEdgeSourceNodeID(e);
	}

	@Override
	public int getEdgeTargetNodeID(int e) {
		return graph.getEdgeTargetNodeID(e);
	}

	@Override
	public boolean isPseudograph() {
		return graph.isPseudograph();
	}

	@Override
	public boolean isMultigraph() {
		return graph.isMultigraph();
	}

	@Override
	public boolean isDirectedgraph() {
		return graph.isDirectedgraph();
	}

	@Override
	public boolean isIndex() {
		return graph.isIndex();
	}

	@Override
	public Set<Feature> getNodeFeatures(int nodeID) {
		return graph.getEdgeFeatures(nodeID);
	}

	@Override
	public Set<Feature> getEdgeFeatures(int edgeID) {
		return graph.getEdgeFeatures(edgeID);
	}
	
}
