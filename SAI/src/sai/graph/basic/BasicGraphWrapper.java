package sai.graph.basic;

import java.util.Set;

import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.Node;

public class BasicGraphWrapper implements Graph {
	
	private Graph graph;

	public BasicGraphWrapper(Graph graph) {
		this.graph = graph;
	}

	@Override
	public int getID() {
		return graph.getID();
	}

	@Override
	public Set<Edge> getEdges() {
		return graph.getEdges();
	}

	@Override
	public Set<Node> getNodes() {
		return graph.getNodes();
	}

	@Override
	public Node getNode(int id) {
		return graph.getNode(id);
	}

	@Override
	public Edge getEdge(int id) {
		return graph.getEdge(id);
	}

	@Override
	public Set<Feature> getFeatures() {
		return graph.getFeatures();
	}

	@Override
	public Node getEdgeSource(Edge e) {
		return graph.getEdgeSource(e);
	}

	@Override
	public Node getEdgeTarget(Edge e) {
		return graph.getEdgeTarget(e);
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
	
}
