package sai.graph;

import info.kendall_morwick.funcles.tuple.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MutableGraph implements Graph {
	
	private Set<Feature> features = Sets.newHashSet();
	private Set<Integer> nodes = Sets.newHashSet();
	private Set<Integer> edges = Sets.newHashSet();
	private Map<Integer,Pair<Integer>> edgeContents = Maps.newHashMap();
	private Multimap<Integer,Feature> nodeFeatures = HashMultimap.create();
	private Multimap<Integer,Feature> edgeFeatures = HashMultimap.create();
	
	public MutableGraph() {
		
	}
	
	/** creates a mutable graph from the given graph for editing purposes.
	 * 
	 * @param g the graph to copy
	 */
	public MutableGraph(Graph g) {
		
		for(Feature f : g.getFeatures()) 
			addFeature(f);
		for(Integer n : g.getNodeIDs()) {
			addNode(n);
			for(Feature f : g.getNodeFeatures(n)) 
				addNodeFeature(n, f);
			
		}
		for(int e : g.getEdgeIDs()) {
			int fn = g.getEdgeSourceNodeID(e);
			int tn = g.getEdgeTargetNodeID(e);
			addEdge(e, fn, tn);
			for(Feature f : g.getEdgeFeatures(e)) 
				addEdgeFeature(e, f);
			
		}
	}
	
	@Override
	public Set<Integer> getEdgeIDs() {
		return Sets.newHashSet(edges);
	}
	@Override
	public Set<Integer> getNodeIDs() {
		return Sets.newHashSet(nodes);
	}
	@Override
	public Set<Feature> getFeatures() {
		return Sets.newHashSet(features);
	}
	@Override
	public int getEdgeSourceNodeID(int e) {
		return edgeContents.get(e).a1();
	}
	@Override
	public int getEdgeTargetNodeID(int e) {
		return edgeContents.get(e).a2();
	}

	public void addNode(final int nid) {
		if(nodes.contains(nid))
			throw new IllegalArgumentException(nid + " is already a node id");
		nodes.add(nid);
	}
	
	
	public void addEdge(final int eid, int n1, int n2) {
		if(edges.contains(eid))
			throw new IllegalArgumentException(eid + " is already an edge id");
		edges.add(eid);
		Pair<Integer> p = Pair.makePair(n1, n2);
		edgeContents.put(eid, p);
	}
	
	public void removeNode(int n) {
		nodes.remove(n);
		for(int e : getEdgeIDs()) {
			if(getEdgeSourceNodeID(e) == n || 
			   getEdgeTargetNodeID(e) == n) {
				removeEdge(e);
			}
		}
	}
	public void removeEdge(int e) {
		edges.remove(e);
		edgeContents.remove(e);
	}
	
	public void addFeature(Feature f) {
		features.add(f);
	}

	public void addNodeFeature(int nodeID, Feature f) {
		nodeFeatures.put(nodeID, f);
	}
	public void addEdgeFeature(int edgeID, Feature f) {
		edgeFeatures.put(edgeID, f);
	}

	public void removeNodeFeature(int nodeID, Feature f) {
		nodeFeatures.remove(nodeID, f);
	}
	public void removeEdgeFeature(int edgeID, Feature f) {
		edgeFeatures.remove(edgeID, f);
	}

	@Override
	public Set<Feature> getNodeFeatures(int n) {
		return Sets.newHashSet(nodeFeatures.get(n));
	}

	@Override
	public Set<Feature> getEdgeFeatures(int n) {
		return Sets.newHashSet(edgeFeatures.get(n));
	}
	
	public void removeFeature(Feature f) {
		features.remove(f);
	}
	
	public void removeFeature(int e, Feature f) {
		edgeFeatures.remove(e, f);
	}
	
	@Override
	public String toString() {
		StringWriter sout = new StringWriter();
		PrintWriter out = new PrintWriter(sout);
		out.print(getNodeIDs().size()+",");
		out.print(getEdgeIDs().size());
		getFeatures().stream().sorted().forEach( f->out.print("," + f));
		out.print("\n");
		//print a line for each node
		for(int n : getNodeIDs()) {
			out.print(n);
			getNodeFeatures(n).stream().sorted().forEach( f->out.print("," + f));
			out.print("\n");
		}
		//print a line for each edge
		for(int e : getEdgeIDs()) {
			out.print(e+","+getEdgeSourceNodeID(e)+","+getEdgeTargetNodeID(e));
			getEdgeFeatures(e).stream().sorted().forEach(f->out.print("," + f));
			out.print("\n");
		}
		return sout.toString();
	}

	@Override 
	public int hashCode() { 
		return toString().hashCode(); 
	}

	@Override 
	public boolean equals(Object o) { 
		return (o instanceof MutableGraph) ?
			 o.toString().equals(toString()) : false;
	}
	
}
