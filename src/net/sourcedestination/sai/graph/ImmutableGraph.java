package net.sourcedestination.sai.graph;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourcedestination.funcles.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

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
		
		features = ImmutableSet.copyOf(g.getFeatures()
				.collect(Collectors.toSet()));
		nodes = ImmutableSet.copyOf(g.getNodeIDs()
				.collect(Collectors.toSet()));
		edges = ImmutableSet.copyOf(g.getEdgeIDs()
				.collect(Collectors.toSet()));
		edgeContents = ImmutableMap.copyOf(
		    g.getEdgeIDs().collect(Collectors.toMap(
				edgeID -> edgeID,
				edgeID ->
				Pair.makePair(g.getEdgeSourceNodeID(edgeID),
						g.getEdgeTargetNodeID(edgeID)))));
		Multimap<Integer,Feature> tempMap = HashMultimap.create();
		g.getNodeIDs().forEach(nodeID -> {
			g.getNodeFeatures(nodeID)
			  .forEach( f -> tempMap.put(nodeID, f));
		});
		nodeFeatures = ImmutableMultimap.copyOf(tempMap);
		tempMap.clear();
		g.getEdgeIDs().forEach(edgeID -> {
			g.getEdgeFeatures(edgeID)
			  .forEach( f -> tempMap.put(edgeID, f));
		});
		edgeFeatures = ImmutableMultimap.copyOf(tempMap);
	}
	
	@Override
	public Stream<Integer> getEdgeIDs() {
		return edges.stream();
	}
	@Override
	public Stream<Integer> getNodeIDs() {
		return nodes.stream();
	}
	@Override
	public Stream<Feature> getFeatures() {
		return features.stream();
	}
	@Override
	public int getEdgeSourceNodeID(int e) {
		return edgeContents.get(e)._1;
	}
	@Override
	public int getEdgeTargetNodeID(int e) {
		return edgeContents.get(e)._2;
	}

	@Override
	public Stream<Feature> getNodeFeatures(int n) {
		return nodeFeatures.get(n).stream();
	}

	@Override
	public Stream<Feature> getEdgeFeatures(int n) {
		return edgeFeatures.get(n).stream();
	}
	
	@Override
	public String toString() {
		StringWriter sout = new StringWriter();
		PrintWriter out = new PrintWriter(sout);
		out.print(getNodeIDs().count()+",");
		out.print(getEdgeIDs().count());
		getFeatures().sorted().forEach( f->out.print("," + f));
		out.print("\n");
		//print a line for each node
		getNodeIDs().forEach(n -> {
			out.print(n);
			getNodeFeatures(n).sorted().forEach( f->out.print("," + f));
			out.print("\n");
		});
		//print a line for each edge
		getEdgeIDs().forEach(e -> {
			out.print(e+","+getEdgeSourceNodeID(e)+","+getEdgeTargetNodeID(e));
			getEdgeFeatures(e).sorted().forEach(f->out.print("," + f));
			out.print("\n");
		});
		return sout.toString();
	}

	@Override 
	public int hashCode() { 
		return toString().hashCode(); 
	}

	@Override 
	public boolean equals(Object o) { 
		return (o instanceof ImmutableGraph) && o.toString().equals(toString());
	}
	
}
