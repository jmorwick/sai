/* Copyright 2011-2013 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    SAI is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SAI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */

package net.sourcedestination.sai.experiment.matching;

import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Stream;

import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.experiment.compatibility.EdgeCompatibilityChecker;
import net.sourcedestination.sai.experiment.compatibility.NodeCompatabilityChecker;
import net.sourcedestination.sai.db.graph.Graph;

import static net.sourcedestination.sai.util.FunctionUtil.argmax;
import static net.sourcedestination.sai.util.StreamUtil.toStream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * A shortcut type for a function generating matchings between two graphs
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */

// TODO: take another pass over this file looking for 1.8 updates to make

@FunctionalInterface
public interface MatchingGenerator<G extends Graph> extends
		Function2<G, G, Stream<GraphMatching<G>>> {

	/** gerenates a matching object including only node matches given a BiMap of the node matching */
	public static <G extends Graph> GraphMatching<G> createBasicNodeMatching(
	        final Graph g1, final Graph g2,
			BiMap<Integer,Integer> nodeMatch) {
		final BiMap<Integer,Integer> copyNodeMatch = 
				ImmutableBiMap.copyOf(nodeMatch);

		// transform Map.Entry to Pair instances
		final ImmutableSet<Pair<Integer>> matchedNode = 
				ImmutableSet.copyOf(copyNodeMatch.entrySet().stream()
					.map((arg) -> Pair.makePair(arg.getKey(), arg.getValue()))
					.collect(toSet()));
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
			public Integer getMatchedNodeInGraph2(int g1NodeID) {
				if(copyNodeMatch.containsKey(g1NodeID))
					return copyNodeMatch.get(g1NodeID);
				return null;
			}

			@Override
			public Integer getMatchedNodeInGraph1(int g2NodeID) {
				if(copyNodeMatch.inverse().containsKey(g2NodeID))
					return copyNodeMatch.inverse().get(g2NodeID);
				return null;
			}

			@Override
			public Stream<Pair<Integer>> getAllNodeMatches() {
				return matchedNode.stream();
			}

			@Override
			public Set<Pair<Integer>> getAllNodeMatchesAsSet() {
				return matchedNode;
			}

			@Override
			public Integer getMatchedEdgeInGraph2(int g1NodeID) {
				return null;
			}

			@Override
			public Integer getMatchedEdgeInGraph1(int g2NodeID) {
				return null;
			}

		};
	}

	public static <G extends Graph> GraphMatching<G> includeEdgeMatching(
	        final GraphMatching<G> nodeMatching,
			BiMap<Integer,Integer> edgeMatch) {
		final BiMap<Integer,Integer> copyEdgeMatch = 
				ImmutableBiMap.copyOf(edgeMatch);

		// transform Map.Entry to Pair instances
		final ImmutableSet<Pair<Integer>> matchedEdges = 
				ImmutableSet.copyOf(edgeMatch.entrySet().stream()
					.map((arg) -> Pair.makePair(arg.getKey(), arg.getValue()))
					.collect(toSet()));
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
			public Integer getMatchedNodeInGraph2(int g1NodeID) {
				return nodeMatching.getMatchedNodeInGraph2(g1NodeID);
			}

			@Override
			public Integer getMatchedNodeInGraph1(int g2NodeID) {
				return nodeMatching.getMatchedNodeInGraph1(g2NodeID);
			}

			@Override
			public Stream<Pair<Integer>> getAllNodeMatches() {
				return nodeMatching.getAllNodeMatches();
			}

			@Override
			public Set<Pair<Integer>> getAllNodeMatchesAsSet() {
				return nodeMatching.getAllNodeMatchesAsSet();
			}

			@Override
			public Integer getMatchedEdgeInGraph2(int g1NodeID) {
				if(copyEdgeMatch.containsKey(g1NodeID))
					return copyEdgeMatch.get(g1NodeID);
				return null;
			}

			@Override
			public Integer getMatchedEdgeInGraph1(int g2NodeID) {
				if(copyEdgeMatch.inverse().containsKey(g2NodeID))
					return copyEdgeMatch.inverse().get(g2NodeID);
				return null;
			}

			@Override
			public Stream<Pair<Integer>> getAllEdgeMatches() {
				return matchedEdges.stream();
			}

			@Override
			public Set<Pair<Integer>> getAllEdgeMatchesAsSet() {
				return matchedEdges;
			}

		};
	}

	/** given a matching of nodes, extends the matching to pair up all edges which
	 * have isomorphically matched incident nodes. In the case of a multigraph, 
	 * edges are matched arbitrarily.
	 * 
	 * @param nodeMatching
	 * @param ecc
	 * @return
	 */
	public static <G extends Graph> GraphMatching<G> induceEdgeMatching(
	        GraphMatching<G> nodeMatching,
			EdgeCompatibilityChecker<G> ecc) {

		final G g1 = nodeMatching.getGraph1();
		final G g2 = nodeMatching.getGraph2();
		BiMap<Integer,Integer> edgeMatch = HashBiMap.create();

		// cache the edges in graph 2
		Multimap<Pair<Integer>, Integer> g2Edges = HashMultimap.create();
		g2.getEdgeIDs().forEach(g2e -> 
			g2Edges.put( 
					Pair.makePair(
							g2.getEdgeSourceNodeID(g2e), 
							g2.getEdgeTargetNodeID(g2e)), g2e));

		// try to match up edges in graph 1 with an edge in graph 2
		g1.getEdgeIDs().forEach(eid1 -> {
			Integer g1n1 = g1.getEdgeSourceNodeID(eid1);
            Integer g1n2 = g1.getEdgeTargetNodeID(eid1);
            Integer g2n1 = nodeMatching.getMatchedNodeInGraph2(g1n1);
            Integer g2n2 = nodeMatching.getMatchedNodeInGraph2(g1n2);
			if(g2n1 == null || g2n2 == null)
				return; //skip edges with unmapped nodes in graph 2		

			if(g2Edges.get(Pair.makePair(g2n1, g2n2)).size() == 0) 
				return;  //skip if it can't be matched to a graph 2 edge

            // try to find a matched edge in g2
            Optional<Integer> g2MatchedEdge = g2Edges.get(Pair.makePair(g2n1, g2n2)).stream()
                    .filter(eid2 -> ecc.apply(g1, g2, eid1, eid2))
                    .findFirst();

			if(g2MatchedEdge.isPresent()) //if we found a match, record it
				edgeMatch.put(eid1, g2MatchedEdge.get());
		});
		return includeEdgeMatching(nodeMatching, edgeMatch);
	}



	public static <G extends Graph> Multimap<Integer,Integer> getNodeMatchingPossibilities(
			final NodeCompatabilityChecker<G> ncc,
			final G g1,
			final G g2) {

		Multimap<Integer,Integer> possibilities = HashMultimap.create();

		g1.getNodeIDs().forEach(nid1 -> {
			g2.getNodeIDs().forEach(nid2 -> {
				if(ncc.apply(g1, g2, nid1, nid2))
					possibilities.put(nid1, nid2);
			});
		});

		return possibilities;
	}

	public static <G extends Graph> Stream<GraphMatching<G>> generateAllMatchings(
	        G g1, G g2,
            final NodeCompatabilityChecker<G> ncc,
            final EdgeCompatibilityChecker<G> ecc) {
				final Multimap<Integer,Integer> possibilities =
						getNodeMatchingPossibilities(ncc, g1, g2);

				//put node ID's in an array
				List<Integer> nodeIDsTemp = Lists.newArrayList();
				g1.getNodeIDs().forEach(n1-> {
					if(possibilities.containsKey(n1))
						nodeIDsTemp.add(n1);
				});
				final Integer[] nodeIDs = nodeIDsTemp.toArray(new Integer[nodeIDsTemp.size()]);


				//create an iterator for the possible complete mappings
				Iterator<GraphMatching<G>> i = new Iterator<GraphMatching<G>>() {
					private int[] currentMap = new int[nodeIDs.length];
					private GraphMatching<G> nextMatching = nextMatching();


					@Override
					public boolean hasNext() {
						return nextMatching != null;
					}

					@Override
					public GraphMatching<G> next() {
						if(nextMatching == null) 
							throw new IllegalStateException("no more matchings left");
						GraphMatching<G> currentMatching = nextMatching;
						nextMatching = nextMatching();
						return currentMatching;
					}

					public GraphMatching<G> nextMatching() {

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
									//break
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
						return induceEdgeMatching(createBasicNodeMatching(g1, g2, nodeMap), ecc);
					}

				};

				return toStream(i);
	}

}