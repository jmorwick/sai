package sai.graph.jgrapht;

import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import sai.db.DBInterface;
import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Node;

public class JDirectedMultiGraph<N extends Node, E extends Edge> 
	extends DirectedMultigraph implements Graph {

	private boolean isIndex;

	public JDirectedMultiGraph(EdgeFactory<N,E> ef, boolean isIndex) {
		super(ef);
		this.isIndex = isIndex;
	}

	public JDirectedMultiGraph(Class<E> edgeClass, boolean isIndex) {
		super(edgeClass);
		this.isIndex = isIndex;
	}


	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public boolean isPseudograph() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMultigraph() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectedgraph() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Edge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Node> getNodes() {
		return this.edgeSet()
	}

	@Override
	public Set<Feature> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Node getNode(int id) {
		
	}

	@Override
	public Edge getEdge(int id) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node getEdgeSource(Edge e) {
		return (N)this.getEdgeSource((E)e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public N getEdgeTarget(Edge e) {
		return (N)this.getEdgeTarget((E)e);
	}

	@Override
	public boolean isIndex() {
		// TODO Auto-generated method stub
		return isIndex;
	}

}
