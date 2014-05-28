package sai.graph;

import info.kendall_morwick.funcles.Pair;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MutableGraph implements Graph {
	
	private int id;
	private Set<Feature> features = Sets.newHashSet();
	private Set<Integer> nodes = Sets.newHashSet();
	private Set<Integer> edges = Sets.newHashSet();
	private Map<Integer,Pair<Integer>> edgeContents = Maps.newHashMap();
	private Multimap<Integer,Feature> nodeFeatures = HashMultimap.create();
	private Multimap<Integer,Feature> edgeFeatures = HashMultimap.create();

	private boolean isPseudoGraph;
	private boolean isMultiGraph;
	private boolean isDirectedGraph;
	private boolean isIndex;

	
	public MutableGraph(boolean isPseudoGraph, boolean isMultiGraph,
			            boolean isDirectedGraph, boolean isIndex) {
		this.isPseudoGraph = isPseudoGraph;
		this.isMultiGraph = isMultiGraph;
		this.isDirectedGraph = isDirectedGraph;
		this.isIndex = isIndex;
	}
	
	/** creates a mutable graph from the given graph for editing purposes.
	 * 
	 * @param g the graph to copy
	 */
	public MutableGraph(Graph g) {
		isPseudoGraph = g.isPseudograph();
		isMultiGraph = g.isMultigraph();
		isDirectedGraph = g.isDirectedgraph();
		isIndex = g.isIndex();
		
		for(Feature f : g.getFeatures()) 
			addFeature(f);
		for(Integer n : g.getNodeIDs()) {
			addNode(n);
			for(Feature f : g.getNodeFeatures(n)) 
				addNodeFeature(n, f);
			
		}
		for(int e : g.getEdgeIDs()) {
			int fn = g.getEdgeSourceNodeID(e);
			int tn = g.getEdgeSourceNodeID(e);
			addEdge(e, fn, tn);
			for(Feature f : g.getEdgeFeatures(e)) 
				addEdgeFeature(e, f);
			
		}
	}
	
	public void setID(int id) { this.id = id; }
	public void setIsPseudoGraph(boolean isPseudoGraph) { 
		this.isPseudoGraph = isPseudoGraph; 
	}
	
	public void setIsMultiGraph(boolean isMultiGraph) { 
		this.isMultiGraph = isMultiGraph; 
	}
	
	public void setIsDirectedGraph(boolean isDirectedGraph) { 
		this.isDirectedGraph = isDirectedGraph; 
	}
	
	public void setIsIndex(boolean isIndex) { 
		this.isIndex = isIndex; 
	}
	
	@Override
	public int getSaiID() {
		return id;
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
	@Override
	public boolean isPseudograph() {
		return isPseudoGraph;
	}
	@Override
	public boolean isMultigraph() {
		return isMultiGraph;
	}
	@Override
	public boolean isDirectedgraph() {
		return isDirectedGraph;
	}
	@Override
	public boolean isIndex() {
		return isIndex;
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
		edgeContents.put(eid, Pair.makeImmutablePair(n1, n2));
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
	
	public static Feature createFeature(final String name, final String value) {
		return new Feature() {

			@Override
			public int getID() {
				return -1;
			}

			@Override
			public String getValue() {
				return value;
			}

			@Override
			public String getName() {
				return name;
			}
			
		};
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
	public void addFeature(int n, Feature f) {
		nodeFeatures.put(n, f);
	}
	public void removeFeature(int e, Feature f) {
		edgeFeatures.remove(e, f);
	}
}
