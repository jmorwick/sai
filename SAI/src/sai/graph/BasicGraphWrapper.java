package sai.graph;

import java.io.PrintWriter;
import java.io.StringWriter;
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
	public Set<Feature> getNodeFeatures(int nodeID) {
		return graph.getNodeFeatures(nodeID);
	}

	@Override
	public Set<Feature> getEdgeFeatures(int edgeID) {
		return graph.getEdgeFeatures(edgeID);
	}
	
	@Override
	public String toString() {
		StringWriter sout = new StringWriter();
		PrintWriter out = new PrintWriter(sout);
		out.print(getSaiID()+",");
		out.print(getNodeIDs().size()+",");
		out.print(getEdgeIDs().size());
		for(Feature f : getFeatures()) 
			out.print("," + f);
		out.print("\n");
		//print a line for each node
		for(int n : getNodeIDs()) {
			out.print(n);
			for(Feature f : getNodeFeatures(n)) 
				out.print("," + f);
			out.print("\n");
		}
		//print a line for each edge
		for(int e : getEdgeIDs()) {
			out.print(e+","+getEdgeSourceNodeID(e)+","+getEdgeTargetNodeID(e));
			for(Feature f : getEdgeFeatures(e)) 
				out.print("," + f);
			out.print("\n");
		}
		return sout.toString();
	}

	@Override 
	public int hashCode() { return toString().hashCode(); }

	@Override 
	public boolean equals(Object o) { 
		return (o instanceof BasicGraphWrapper) ?
			 o.toString().equals(toString()) : false;
	}
	
}
