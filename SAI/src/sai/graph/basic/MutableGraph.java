package sai.graph.basic;

import info.kendall_morwick.funcles.Pair;

import java.util.Map;
import java.util.Set;

import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.Node;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MutableGraph implements Graph {
	
	private int id;
	private Set<Feature> features = Sets.newHashSet();
	private Map<Integer,Node> nodes = Maps.newHashMap();
	private Map<Integer,Edge> edges = Maps.newHashMap();
	private Map<Edge,Pair<Node>> edgeContents = Maps.newHashMap();
	private Multimap<Node,Feature> nodeFeatures = HashMultimap.create();
	private Multimap<Edge,Feature> edgeFeatures = HashMultimap.create();

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
		for(Node n : g.getNodes()) {
			Node cn = addNode(n.getID());
			for(Feature f : n.getFeatures()) 
				addFeature(cn, f);
			
		}
		for(Edge e : g.getEdges()) {
			Node fn = getNode(g.getEdgeSource(e).getID());
			Node tn = getNode(g.getEdgeSource(e).getID());
			Edge ce = addEdge(e.getID(), fn, tn);
			for(Feature f : e.getFeatures()) 
				addFeature(ce, f);
			
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
	public int getID() {
		return id;
	}
	@Override
	public Set<Edge> getEdges() {
		return Sets.newHashSet(edges.values());
	}
	@Override
	public Set<Node> getNodes() {
		return Sets.newHashSet(nodes.values());
	}
	@Override
	public Node getNode(int id) {
		return nodes.get(id);
	}
	@Override
	public Edge getEdge(int id) {
		return edges.get(id);
	}
	@Override
	public Set<Feature> getFeatures() {
		return Sets.newHashSet(features);
	}
	@Override
	public Node getEdgeSource(Edge e) {
		return edgeContents.get(e).a1();
	}
	@Override
	public Node getEdgeTarget(Edge e) {
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

	public Node addNode(final int nid) {
		final MutableGraph self = this;
		Node n = new Node(){

			@Override
			public int getID() {
				return nid;
			}

			@Override
			public Set<Feature> getFeatures() {
				return Sets.newHashSet(self.nodeFeatures.get(this));
			}};
			nodes.put(nid, n);
			return n;
	}
	
	
	public Edge addEdge(final int eid, Node n1, Node n2) {
		final MutableGraph self = this;
		Edge e = new Edge() {

			@Override
			public int getID() {
				return eid;
			}

			@Override
			public Set<Feature> getFeatures() {
				return Sets.newHashSet(self.edgeFeatures.get(this));
			}
			
		};
		edges.put(eid, e);
		edgeContents.put(e, Pair.makeImmutablePair(n1, n2));
		return e;
	}
	
	public void removeNode(Node n) {
		nodes.remove(n.getID());
		for(Edge e : getEdges()) {
			if(getEdgeSource(e).getID() == n.getID() || 
			   getEdgeTarget(e).getID() == n.getID()) {
				removeEdge(e);
			}
		}
	}
	public void removeEdge(Edge e) {
		edges.remove(e.getID());
		edgeContents.remove(e);
	}
	
	public void addFeature(Feature f) {
		features.add(f);
	}
	public void removeFeature(Feature f) {
		features.remove(f);
	}
	public void addFeature(Node n, Feature f) {
		nodeFeatures.put(n, f);
	}
	public void removeFeature(Node n, Feature f) {
		nodeFeatures.remove(n, f);
	}
	public void addFeature(Edge e, Feature f) {
		edgeFeatures.put(e, f);
	}
	public void removeFeature(Edge e, Feature f) {
		edgeFeatures.remove(e, f);
	}
}
