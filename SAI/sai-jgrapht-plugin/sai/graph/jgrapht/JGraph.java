package sai.graph.jgrapht;

import java.util.Set;

import sai.db.DBInterface;
import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Node;

public class JGraph implements Graph {

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DBInterface getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphFactory<?> getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Feature> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

}
