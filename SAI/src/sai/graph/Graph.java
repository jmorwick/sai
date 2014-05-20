package sai.graph;

import java.util.Set;

import sai.db.DBInterface;


public interface Graph {
	public int getID();
	public DBInterface getDB();
	public GraphFactory<?> getFactory();
	public boolean isMutable();
	public boolean isPseudograph();
	public boolean isMultigraph();
	public boolean isDirectedgraph();
	public Set<Edge> getEdges();
	public Set<Node> getNodes();
	public Set<Feature> getFeatures();
	public Node getEdgeSource(Edge e);
	public Node getEdgeTarget(Edge e);
}
