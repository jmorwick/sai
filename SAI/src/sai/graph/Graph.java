package sai.graph;

import java.util.Set;

import sai.db.DBInterface;


public interface Graph {
	public int getID();
	public DBInterface getDB();
	public GraphFactory<?> getFactory();
	public Set<Edge> getEdges();
	public Set<Node> getNodes();
	public Node getNode(int id);
	public Edge getEdge(int id);
	public Set<Feature> getFeatures();
	public Node getEdgeSource(Edge e);
	public Node getEdgeTarget(Edge e);

	public boolean isMutable();
	public boolean isPseudograph();
	public boolean isMultigraph();
	public boolean isDirectedgraph();
	public boolean isIndex();
	
	public Node addNode();
	public Edge addEdge(Node n1, Node n2);
	public void removeNode(Node n);
	public void removeEdge(Edge e);
	public void addFeature(Feature f);
	public void removeFeature(Feature f);
	public void addFeature(Node n, Feature f);
	public void removeFeature(Node n, Feature f);
	public void addFeature(Edge e, Feature f);
	public void removeFeature(Edge e, Feature f);
}
