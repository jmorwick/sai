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
package sai.comparison;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;

import sai.Edge;
import sai.Node;
import sai.Graph;
import sai.Feature;

/**
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Util {

    /** returns a set containing only features of the specified type */
    public static Set<? extends Feature> retainOnly(Set<? extends Feature> features,
            Set<? extends Class<? extends Feature>> types) {
        Set<Feature> ret = new HashSet<Feature>();
        for (Feature f : features) {
            for (Class<? extends Feature> type : types) {
                if (type.isInstance(f)) {
                    ret.add(f);
                    break;
                }
            }
        }
        return ret;
    }

    /** returns a set containing only features of the specified type */
    public static Set<? extends Feature> retainOnly(Set<? extends Feature> features,
            Class<? extends Feature>... types) {
        Set<Class<? extends Feature>> ntypes =
                new HashSet<Class<? extends Feature>>();
        for (Class<? extends Feature> t : types) {
            ntypes.add(t);
        }
        return retainOnly(features, ntypes);
    }

    /** Determines which nodes in s1 can be mapped to which nodes in s2.
     * This is determined by checking to see if both nodes have the same features
     * for the feature classes indicated in featureTypes.
     * @param <N> indexable node type
     * @param <E> indexable edge type
     * @param <S> indexable structure type
     * @param s1 structure from which to map nodes
     * @param s2 structure to which to map nodes
     * @param featureTypes feature types to be considered when comparing nodes
     * @return a multi-map indicating which nodes in s1 can be mapped to which in s2
     */
    public static Multimap<Node, Node> nodeCompatibility(Graph s1, Graph s2,
            Class<? extends Feature>... featureTypes) {
        return nodeCompatibility(s1.vertexSet(), s2.vertexSet(), featureTypes);
    }

    public static Multimap<Node, Node> nodeCompatibility(
            Set<Node> s1,
            Set<Node> s2,
            Class<? extends Feature>... featureTypes) {
        Multimap<Node, Node> possibilities = new Multimap<Node, Node>();
        for (Node n1 : s1) {
            for (Node n2 : s2) {
                if (n1.compatible(n2, featureTypes)) {
                    possibilities.put(n1, n2);
                }
            }
        }
        return possibilities;
    }
    public static final Function<MultiMap<Edge, Edge>,Integer> completeEdgeMatchCounter =
            new Function<MultiMap<Edge, Edge>,Integer>() {

                @Override
                public Integer implementation(Multimap<Edge, Edge> p1) {
                    return p1.findRepresentativesComplete().size();
                }
            };

    /** returns the number of subsumed edges for the mapping */
    public static int matchedEdges(Graph s1,
            Graph s2,
            Node n1,
            Map<Node, Node> m,
            Function<Integer, Multimap<Edge, Edge>> countMappableEdges,
            Class<? extends Feature>... featureTypes) {
        Multimap<Edge, Edge> possibleMappings = new Multimap<Edge, Edge>();
        Node n2 = m.get(n1);
        if (n2 == null) {
            return 0;
        }

        for (Edge e1 : s1.edgeSet()) {
            for (Edge e2 : s2.edgeSet()) {
                if (!e1.subsumes(e2, featureTypes)) {
                    continue;
                }
                if (n1 == s1.getEdgeSource(e1)
                        && n2 == s2.getEdgeSource(e2)
                        && m.get(s1.getEdgeTarget(e1)) == s2.getEdgeTarget(e2)) {
                    possibleMappings.put(e1, e2);
                } else if (n1 == s1.getEdgeTarget(e1)
                        && n2 == s2.getEdgeTarget(e2)
                        && m.get(s1.getEdgeSource(e1)) == s2.getEdgeSource(e2)
                        && e1.subsumes(e2, featureTypes)) {
                    possibleMappings.put(e1, e2);
                } else if (s1.getDB().directedGraphs()
                        && n1 == s1.getEdgeSource(e1)
                        && n2 == s2.getEdgeTarget(e2)
                        && m.get(s1.getEdgeTarget(e1)) == s2.getEdgeSource(e2)
                        && e1.subsumes(e2, featureTypes)) {
                    possibleMappings.put(e1, e2);
                } else if (s1.getDB().directedGraphs()
                        && n1 == s1.getEdgeTarget(e1)
                        && n2 == s2.getEdgeSource(e2)
                        && m.get(s1.getEdgeSource(e1)) == s2.getEdgeTarget(e2)
                        && e1.subsumes(e2, featureTypes)) {
                    possibleMappings.put(e1, e2);
                }
            }
        }
        return possibleMappings.findRepresentativesComplete().size();
    }

    public static int matchedEdges(Graph s1,
            Graph s2,
            Map<Node, Node> m,
            Function<Integer, Multimap<Edge, Edge>> countMappableEdges,
            Class<? extends Feature>... featureTypes) {
        int matches = 0;
        for (Map.Entry<Node, Node> e : m.entrySet()) {
            matches += matchedEdges(s1,
                    s2,
                    e.getKey(),
                    m,
                    countMappableEdges,
                    featureTypes);
        }

        return matches / 2;   //each edge will be counted twice (once on the starting node and once on the ending node)
    }

    public static BigInteger getNumberOfCompleteMappings(Graph g1, Graph g2,
            Class<? extends Feature>... featureTypes) {
        Multimap<Node, Node> compatibility = nodeCompatibility(g1, g2, featureTypes);
        return compatibility.getNumberOfCompleteMappings();
    }

    public static BigInteger getNumberOfPartialMappings(Graph g1, Graph g2,
            Class<? extends Feature>... featureTypes) {
        Multimap<Node, Node> compatibility = nodeCompatibility(g1, g2, featureTypes);
        return compatibility.getNumberOfPartialMappings();
    }
}