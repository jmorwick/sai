/* Copyright 2011 Joseph Kendall-Morwick

This file is part of SAI: The Structure Access Interface.

jmorwick-javalib is free software: you can redistribute it and/or modify
it under the terms of the Lesser GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

jmorwick-javalib is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
Lesser GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License
along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */
package sai.comparison.mapheuristics;

import info.km.funcles.Funcles;
import info.km.funcles.T2;
import info.km.funcles.T3;
import info.km.funcles.Tuple;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.Edge;
import sai.Feature;
import sai.Graph;
import sai.Node;
import sai.comparison.MapHeuristic;
import sai.comparison.Util;
import sai.comparison.mapgenerators.search.GraphMapping;

/**
 * A map heuristic which sums weightings for the number of compatibility
 * relationships between a variety of types of features, induced by the mapping.
 *
 * TODO: re-implement caching
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class WeightedMatchedFeatures extends MapHeuristic {

    private final double mappedEdgeValue;
    private Map<Class<? extends Feature>, Double> values =
            Maps.newHashMap();
    private Graph g1C = null;
    private Graph g2C = null;

    private boolean directed = true;

    public WeightedMatchedFeatures(double mappedEdgeValue,
            boolean directed,
            T2<? extends Class<? extends Feature>, Double>... featureValues) {
        this.mappedEdgeValue = mappedEdgeValue;
        for (T2<? extends Class<? extends Feature>, Double> t : featureValues) {
            values.put(t.a1(), t.a2());
        }
        //startCaching(matchFeatures);
        //startCaching(getMatchingClass);
        this.directed = directed;
    }


    public WeightedMatchedFeatures(double mappedEdgeValue,
            boolean directed,
            Set<T2<? extends Class<? extends Feature>, Double>> featureValues) {
        this.mappedEdgeValue = mappedEdgeValue;
        for (T2<? extends Class<? extends Feature>, Double> t : featureValues) {
            values.put(t.a1(), t.a2());
        }
        //matchFeatures.startCaching();
        //getMatchingClass.startCaching();
        this.directed = directed;
    }

    @Override
    public Double apply(T3<Graph,Graph,GraphMapping> args) {
    	Graph g1 = args.a1(); 
    	Graph g2 = args.a2(); 
    	Map<Node, Node> m = args.a3();

        Map<Node,Node> rm = Util.reverseMap(m);

        //clear the cache if we're looking at new graphs
        if (g1C == null || g2C == null
                || (g1C != g1 && !g1C.equals(g1))
                || g2C != g2 && !g2C.equals(g2)) {
            //matchFeatures.clearCache();
            //getMatchingClass.clearCache();
            g1C = g1;
            g2C = g2;
        }

        //calculate the maximum possible score
        double maxCount = 0;
        for (Feature f : g1.getFeatures()) {
            Class<? extends Feature> c = getMatchingClass.apply(f);
            if (c != null) {
                maxCount += values.get(c);
            }
        }
        for (Edge e : g1.edgeSet()) {
            for (Feature f : e.getFeatures()) {
                Class<? extends Feature> c = getMatchingClass.apply(f);
                if (c != null) {
                    maxCount += values.get(c);
                }
            }
        }
        for (Node n : g1.vertexSet()) {
            for (Feature f : n.getFeatures()) {
                Class<? extends Feature> c = getMatchingClass.apply(f);
                if (c != null) {
                    maxCount += values.get(c);
                }
            }
        }
        
        if(maxCount == 0) return 0.0;

        //caclulate the score for this map

        //start by comparing the graph features
        double count = getMatchedValues(
                g1.getFeatures(), g2.getFeatures());

        
        Multimap<T2<Node, Node>, Edge> available = HashMultimap.create();
        for (Edge e : g2.edgeSet()) {
            available.put(Tuple.makeTuple(
                    g2.getEdgeSource(e),
                    g2.getEdgeTarget(e)), e);
            if(!directed)  //add the opposite direction too if the graph is not directed
                available.put(Tuple.makeTuple(
                    g2.getEdgeTarget(e),
                    g2.getEdgeSource(e)), e);
        }
        for (final Edge e : g1.edgeSet()) {  //for each edge in the probe graph
            Node n1 = g1.getEdgeSource(e);
            Node n2 = g1.getEdgeTarget(e);
            if (m.containsKey(n1) && m.containsKey(n2)) {  //if both nodes in the edge are mapped in the candidate map
                T2<Node, Node> t = Tuple.makeTuple(m.get(n1), m.get(n2));
                T2<Node, Node> tr = Tuple.makeTuple(m.get(n1), m.get(n2));
                Function<Edge,Double> f = new Function<Edge,Double>() {
                    @Override
                    public Double apply(Edge e2) {
                        return getMatchedValues(
                                e.getFeatures(),
                                e2.getFeatures());
                    }
                };
                if (available.get(t).size() > 0) {  //check to see if the edge is preserved
                    Edge e2 = Funcles.argmaxCollection(f,available.get(t));
                    available.remove(t, e2);
                    count += f.apply(e2);
                }
            }
        }
        for (Node n : m.keySet()) {
            count += (Double) getMatchedValues(n.getFeatures(), m.get(n).getFeatures());
        }

        count /= maxCount;
        return count;
    }

    public double getMatchedValues(Set<Feature> s1, Set<Feature> s2) {
        return matchFeatures.apply(Tuple.makeTuple(s1, s2));
    }

    private Function<T2<? extends Set<? extends Feature>, ? extends Set<? extends Feature>>,Double> matchFeatures =
            new Function<T2<? extends Set<? extends Feature>, ? extends Set<? extends Feature>>,Double>() {

                @Override
                public Double apply(T2<? extends Set<? extends Feature>, ? extends Set<? extends Feature>> args) {
                    Set<? extends Feature> fs1 = args.a1();
                    Set<? extends Feature> fs2 = args.a2();
                    double count = 0;
                    for (Feature f1 : fs1) {
                        for (Feature f2 : Sets.newHashSet(fs2)) {
                            Class<? extends Feature> c = getMatchingClass.apply(f2);
                            if (f1.compatible(f2) && c != null) {
                                count += values.get(c);
                                break;
                            }
                        }
                    }

                    return count;
                }
            };
    private Function<Feature, Class<? extends Feature>> getMatchingClass =
            new Function<Feature, Class<? extends Feature>>() {

                @Override
                public Class<? extends Feature> apply(Feature f) {
                    for (Class<? extends Feature> c : values.keySet()) {
                        if (c.isInstance(f)) {
                            return c;
                        }
                    }
                    return null;
                }
            };
}