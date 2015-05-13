package sai.graph;

import info.kendall_morwick.funcles.tuple.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ImmutableGraph implements Graph {
	
	private final Set<Feature> features;
	private final Set<Integer> nodes;
	private final Set<Integer> edges;
	private final Map<Integer,Pair<Integer>> edgeContents;
	private final Multimap<Integer,Feature> nodeFeatures;
	private final Multimap<Integer,Feature> edgeFeatures;
	
	
	/** creates an immutable graph from the given graph for editing purposes.
	 * 
	 * @param g the graph to copy
	 */
	public ImmutableGraph(Graph g) {
		
		features = ImmutableSet.copyOf(g.getFeatures().stream()
				.collect(Collectors.toSet()));
		nodes = ImmutableSet.copyOf(g.getNodeIDs().stream()
				.collect(Collectors.toSet()));
		edges = ImmutableSet.copyOf(g.getEdgeIDs().stream()
				.collect(Collectors.toSet()));
		edgeContents = ImmutableMap.copyOf(
		    g.getEdgeIDs().stream().collect(Collectors.toMap(
				edgeID -> edgeID,
				edgeID -> // TODO: determine if the casts to Integer are absolutely necessary
				Pair.makePair(g.getEdgeSourceNodeID((Integer)edgeID), 
						g.getEdgeTargetNodeID((Integer)edgeID)))));
		Multimap<Integer,Feature> tempMap = HashMultimap.create();
		g.getNodeIDs().stream().forEach(nodeID -> {
			g.getNodeFeatures(nodeID).stream()
			  .forEach( f -> tempMap.put(nodeID, f));
		});
		nodeFeatures = ImmutableMultimap.copyOf(tempMap);
		tempMap.clear();
		g.getEdgeIDs().stream().forEach(edgeID -> {
			g.getEdgeFeatures(edgeID).stream()
			  .forEach( f -> tempMap.put(edgeID, f));
		});
		edgeFeatures = ImmutableMultimap.copyOf(tempMap);
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
	public Set<Feature> getNodeFeatures(int n) {
		return Sets.newHashSet(nodeFeatures.get(n));
	}

	@Override
	public Set<Feature> getEdgeFeatures(int n) {
		return Sets.newHashSet(edgeFeatures.get(n));
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
		return (o instanceof ImmutableGraph) ?
			 o.toString().equals(toString()) : false;
	}
	
}
