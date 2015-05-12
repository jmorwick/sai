package sai.comparison.matching;

import static info.kendall_morwick.funcles.Funcles.apply;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.tuple.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import sai.comparison.compatibility.FeatureSetCompatibilityChecker;
import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.graph.Graph;
import sai.graph.Graphs;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class MatchingUtil {

	public static Comparator<Graph> createGraphMatchOrdering(
			final Graph query,
			final MatchingGenerator gen, 
			final GraphMatchingHeuristic h) {
		return (g1, g2) -> {
				GraphMatching gm1 = apply(gen, query, g1);
				GraphMatching gm2 = apply(gen, query, g2);
				
				//create an integer from the [-1,1] value below for comparisons
				double result = (1000*(h.apply(gm1) - h.apply(gm2)));
				if(result < 0) return (int)result - 1;
				if(result > 0) return (int)result + 1;
				return 0;
		};
	}

	public static GraphMatching createBasicNodeMatching(final Graph g1, final Graph g2,
			BiMap<Integer,Integer> nodeMatch) {
		final BiMap<Integer,Integer> copyNodeMatch = 
				ImmutableBiMap.copyOf(nodeMatch);

		// transform Map.Entry to Pair instances
		final ImmutableSet<Pair<Integer>> matchedNode = 
				ImmutableSet.copyOf(copyNodeMatch.entrySet().stream()
					.map((arg) -> Pair.makePair(arg.getKey(), arg.getValue()))
					.collect(Collectors.toSet()));
		return new GraphMatching() {

			@Override
			public Graph getGraph1() {
				return g1;
			}

			@Override
			public Graph getGraph2() {
				return g2;
			}

			@Override
			public int getMatchedNodeInGraph2(int g1NodeID) {
				if(copyNodeMatch.containsKey(g1NodeID))
					return copyNodeMatch.get(g1NodeID);
				return -1;
			}

			@Override
			public int getMatchedNodeInGraph1(int g2NodeID) {
				if(copyNodeMatch.inverse().containsKey(g2NodeID))
					return copyNodeMatch.inverse().get(g2NodeID);
				return -1;
			}

			@Override
			public Set<Pair<Integer>> getAllNodeMatches() {
				return matchedNode;
			}

			@Override
			public int getMatchedEdgeInGraph2(int g1NodeID) {
				return -1;
			}

			@Override
			public int getMatchedEdgeInGraph1(int g2NodeID) {
				return -1;
			}

			@Override
			public Set<Pair<Integer>> getAllEdgeMatches() {
				return Sets.newHashSet();
			}

		};
	}


	public static GraphMatching includeEdgeMatching(final GraphMatching nodeMatching, 
			BiMap<Integer,Integer> edgeMatch) {
		final BiMap<Integer,Integer> copyEdgeMatch = 
				ImmutableBiMap.copyOf(edgeMatch);

		// transform Map.Entry to Pair instances
		final ImmutableSet<Pair<Integer>> matchedEdges = 
				ImmutableSet.copyOf(edgeMatch.entrySet().stream()
					.map((arg) -> Pair.makePair(arg.getKey(), arg.getValue()))
					.collect(Collectors.toSet()));
		return new GraphMatching() {

			@Override
			public Graph getGraph1() {
				return nodeMatching.getGraph1();
			}

			@Override
			public Graph getGraph2() {
				return nodeMatching.getGraph2();
			}

			@Override
			public int getMatchedNodeInGraph2(int g1NodeID) {
				return nodeMatching.getMatchedNodeInGraph2(g1NodeID);
			}

			@Override
			public int getMatchedNodeInGraph1(int g2NodeID) {
				return nodeMatching.getMatchedNodeInGraph1(g2NodeID);
			}

			@Override
			public Set<Pair<Integer>> getAllNodeMatches() {
				return nodeMatching.getAllNodeMatches();
			}

			@Override
			public int getMatchedEdgeInGraph2(int g1NodeID) {
				if(copyEdgeMatch.containsKey(g1NodeID))
					return copyEdgeMatch.get(g1NodeID);
				return -1;
			}

			@Override
			public int getMatchedEdgeInGraph1(int g2NodeID) {
				if(copyEdgeMatch.inverse().containsKey(g2NodeID))
					return copyEdgeMatch.inverse().get(g2NodeID);
				return -1;
			}

			@Override
			public Set<Pair<Integer>> getAllEdgeMatches() {
				return matchedEdges;
			}

		};
	}

	/** given a matching of nodes, extends the matching to pair up all edges which
	 * have isomorphically matched incident nodes. In the case of a multigraph, 
	 * edges are matched arbitrarily.
	 * 
	 * @param nodeMatching
	 * @param fscc
	 * @return
	 */
	public static GraphMatching induceEdgeMatching(GraphMatching nodeMatching, 
			FeatureSetCompatibilityChecker fscc) {

		// if they're not directed, we need to treat edge compatibility differently:
		if(nodeMatching.getGraph1().getFeatures().contains(Graphs.DIRECTED) &&
				nodeMatching.getGraph2().getFeatures().contains(Graphs.DIRECTED))
			return induceEdgeMatchingUndirected(nodeMatching, fscc);


		final Graph g1 = nodeMatching.getGraph1();
		final Graph g2 = nodeMatching.getGraph2();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();

		Multimap<Pair<Integer>, Integer> g2Edges = HashMultimap.create();
		for(int g2e : g2.getEdgeIDs()) 
			g2Edges.put( 
					Pair.makePair(
							g2.getEdgeSourceNodeID(g2e), 
							g2.getEdgeTargetNodeID(g2e)), g2e);

		for(int eid : g1.getEdgeIDs()) {
			int g1n1 = g1.getEdgeSourceNodeID(eid);
			int g1n2 = g1.getEdgeTargetNodeID(eid);
			int g2n1 = nodeMatching.getMatchedNodeInGraph2(g1n1);
			int g2n2 = nodeMatching.getMatchedNodeInGraph2(g1n2);
			if(g2n1 == -1 || g2n2 == -1) 
				continue; //skip edges with unmapped nodes in graph 2		

			if(g2Edges.get(Pair.makePair(g2n1, g2n2)).size() == 0) 
				continue;  //skip if it can't be matched to a graph 2 edge

			int g2MatchedEdge = -1; // make sure the edges are compatible
			for(int g2e : g2Edges.get(Pair.makePair(g2n1, g2n2))) 
				if(apply(fscc, g1.getEdgeFeatures(eid), g2.getEdgeFeatures(g2e)))
					g2MatchedEdge = g2e;

			if(g2MatchedEdge != -1) //if we found a match, record it
				edgeMatch.put(eid, g2MatchedEdge);
		}
		return includeEdgeMatching(nodeMatching, edgeMatch);
	}


	/** given a matching of nodes, extends the matching to pair up all edges which
	 * have isomorphically matched incident nodes. In the case of a multigraph, 
	 * edges are matched arbitrarily.
	 * 
	 * @param nodeMatching
	 * @param fscc
	 * @return
	 */
	private static GraphMatching induceEdgeMatchingUndirected(
			GraphMatching nodeMatching, 
			FeatureSetCompatibilityChecker fscc) {
		final Graph g1 = nodeMatching.getGraph1();
		final Graph g2 = nodeMatching.getGraph2();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();

		Multimap<Set<Integer>, Integer> g2Edges = HashMultimap.create();
		for(int g2e : g2.getEdgeIDs()) 
			g2Edges.put( 
					Sets.newHashSet(
							g2.getEdgeSourceNodeID(g2e), 
							g2.getEdgeTargetNodeID(g2e)), g2e);

		for(int eid : g1.getEdgeIDs()) {
			int g1n1 = g1.getEdgeSourceNodeID(eid);
			int g1n2 = g1.getEdgeTargetNodeID(eid);
			int g2n1 = nodeMatching.getMatchedNodeInGraph2(g1n1);
			int g2n2 = nodeMatching.getMatchedNodeInGraph2(g1n2);
			if(g2n1 == -1 || g2n2 == -1) 
				continue; //skip edges with unmapped nodes in graph 2		

				if(g2Edges.get(Sets.newHashSet(g2n1, g2n2)).size() == 0) 
					continue;  //skip if it can't be matched to a graph 2 edge

				int g2MatchedEdge = -1; // make sure the edges are compatible
				for(int g2e : g2Edges.get(Sets.newHashSet(g2n1, g2n2))) 
					if(apply(fscc, g1.getEdgeFeatures(eid), g2.getEdgeFeatures(g2e)))
						g2MatchedEdge = g2e;

				if(g2MatchedEdge != -1) //if we found a match, record it
					edgeMatch.put(eid, g2MatchedEdge);
		}
		return includeEdgeMatching(nodeMatching, edgeMatch);
	}

	public static Multimap<Integer,Integer> getNodeMatchingPossibilities(
			final FeatureSetCompatibilityChecker fscc,
			Graph g1,
			Graph g2) {

		Multimap<Integer,Integer> possibilities = HashMultimap.create();

		for(int n1 : g1.getNodeIDs()) {
			for(int n2 : g2.getNodeIDs()) {
				if(Funcles.apply(fscc, g1.getNodeFeatures(n1), g2.getNodeFeatures(n2)))
					possibilities.put(n1, n2);
			}
		}

		return possibilities;
	}



	@SuppressWarnings("unchecked")
	public static MatchingGenerator createCompleteMatchingGenerator(
			final FeatureSetCompatibilityChecker fscc,
			final GraphMatchingHeuristic h
			) {
		return (g1, g2) -> {
				final Multimap<Integer,Integer> possibilities = 
						getNodeMatchingPossibilities(fscc, g1, g2);

				//put node ID's in an array to insure they remain in the same order
				List<Integer> nodeIDsTemp = Lists.newArrayList();
				for(int n1 : g1.getNodeIDs())
					if(possibilities.containsKey(n1))
						nodeIDsTemp.add(n1);
				final Integer[] nodeIDs = nodeIDsTemp.toArray(new Integer[nodeIDsTemp.size()]);

				//create an iterator for the possible complete mappings
				Iterator<GraphMatching> i = new Iterator<GraphMatching>() {
					private int[] currentMap = new int[nodeIDs.length];
					private GraphMatching nextMatching = nextMatching();

					@Override
					public boolean hasNext() {
						return nextMatching != null;
					}

					@Override
					public GraphMatching next() {
						if(nextMatching == null) 
							throw new IllegalStateException("no more matchings left");
						GraphMatching currentMatching = nextMatching;
						nextMatching = nextMatching();
						return currentMatching;
					}

					public GraphMatching nextMatching() {
						BiMap<Integer,Integer> nodeMap = null;
						while(nodeMap == null && currentMap != null) {
							nodeMap = HashBiMap.create();

							//create map object
							for(int i=0; i<nodeIDs.length; i++) {
								int skip = currentMap[i];
								// it is assumed the following iterator is deterministic
								Iterator<Integer> ii = possibilities.get(nodeIDs[i]).iterator();
								while(skip > 0) {
									skip--;
									ii.next();
								}
								int mapToNode = ii.next();
								if(nodeMap.containsValue(mapToNode)) {
									//don't map two g1 nodes to the same g2 node
									//nodeMap = null;
									//break;
									continue;
								}
								nodeMap.put(nodeIDs[i], mapToNode);
							}

							//increment to next map:
							boolean incremented = false;
							for(int i=0; i<currentMap.length; i++) {
								if(currentMap[i] < possibilities.get(nodeIDs[i]).size()-1) {
									currentMap[i]++;
									incremented = true;
									break;
								}
								currentMap[i] = 0;
							}
							if(!incremented) currentMap = null;
						}
						if(nodeMap == null) return null;
						return induceEdgeMatching(createBasicNodeMatching(g1, g2, nodeMap), fscc);
					}

				};
				Iterable<GraphMatching> iterable = () -> i;
				Stream<GraphMatching> s = StreamSupport.stream(iterable.spliterator(), false);
				// TODO: determine why there is a syntax error without the reference below
				// h extends Function<GraphMatching,Double>, but isn't recognized as such
				// this is also corrected with a cast, but this is shorter
				return Funcles.argmax(h::apply, s);
			};
	}
}
