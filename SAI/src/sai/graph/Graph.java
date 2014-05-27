package sai.graph;

import java.util.Set;

import sai.db.DBInterface;


public interface Graph {
	public int getID();
	public Set<Edge> getEdges();
	public Set<Node> getNodes();
	public Node getNode(int id);
	public Edge getEdge(int id);
	public Set<Feature> getFeatures();
	public Node getEdgeSource(Edge e);
	public Node getEdgeTarget(Edge e);
	
	public boolean isPseudograph();
	public boolean isMultigraph();
	public boolean isDirectedgraph();
	public boolean isIndex();
}
