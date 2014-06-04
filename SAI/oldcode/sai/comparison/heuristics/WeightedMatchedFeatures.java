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
package sai.comparison.heuristics;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.T2;
import info.kendall_morwick.funcles.T3;
import info.kendall_morwick.funcles.Tuple;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.comparison.mapgenerators.search.GraphMapping;
import sai.comparison.matching.GraphMatching;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;

/**
 * A map heuristic which sums weightings for the number of compatibility
 * relationships between a variety of types of features, induced by the mapping.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class WeightedMatchedFeatures implements GraphMatchingHeuristic {

	private final double mappedEdgeValue;
	private Map<String, Double> featureClassWeights = Maps.newHashMap();
	private Graph g1C = null;
	private Graph g2C = null;

	private boolean directed = true;
	private DBInterface db;

	public WeightedMatchedFeatures(double mappedEdgeValue,
			boolean directed,
			T2<String, Double> ... featureValues) {
		this.mappedEdgeValue = mappedEdgeValue;
		for (T2<String, Double> t : featureValues) {
			featureClassWeights.put(t.a1(), t.a2());
		}
		this.directed = directed;
	}


	public WeightedMatchedFeatures(
			DBInterface db,
			double mappedEdgeValue,
			boolean directed,
			Set<T2<String, Double>> featureValues) {
		this.mappedEdgeValue = mappedEdgeValue;
		for (T2<String, Double> t : featureValues) {
			featureClassWeights.put(t.a1(), t.a2());
		}
		this.directed = directed;
		this.db = db;
	}

	@Override
	public Double apply(GraphMatching m) {
		final Graph g1 = m.getGraph1(); 
		final Graph g2 = m.getGraph2(); 

		Map<Integer,Integer> rm = SAIUtil.reverseMap(m);

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
			if (featureClassWeights.containsKey(f.getName())) {
				maxCount += featureClassWeights.get(f.getName());
			}
		}
		for (Integer e : g1.getEdgeIDs()) {
			for (Feature f : g1.getEdgeFeatures(e)) {
				if (featureClassWeights.containsKey(f.getName())) {
					maxCount += featureClassWeights.get(f.getName());
				}
			}
		}
		for (Integer n : g1.getNodeIDs()) {
			for (Feature f : g1.getNodeFeatures(n)) {
				if (featureClassWeights.containsKey(f.getName())) {
					maxCount += featureClassWeights.get(f.getName());
				}
			}
		}

		if(maxCount == 0) return 0.0;

		//caclulate the score for this map

		//start by comparing the graph features
		double count = getMatchedValues(db, 
				g1.getFeatures(), g2.getFeatures());


				Multimap<T2<Integer, Integer>, Integer> available = HashMultimap.create();
				for (Integer e : g2.getEdgeIDs()) {
					available.put(Tuple.makeTuple(
							g2.getEdgeSourceNodeID(e),
							g2.getEdgeTargetNodeID(e)), e);
					if(!directed)  
						//add the opposite direction too if the graph is not directed
						available.put(Tuple.makeTuple(
								g2.getEdgeTargetNodeID(e),
								g2.getEdgeSourceNodeID(e)), e);
				}
				for (final Integer e : g1.getEdgeIDs()) {  //for each edge in the probe graph
					Integer n1 = g1.getEdgeSourceNodeID(e);
					Integer n2 = g1.getEdgeTargetNodeID(e);
					if (m.containsKey(n1) && m.containsKey(n2)) {  
						//if both nodes in the edge are mapped in the candidate map
						T2<Integer, Integer> t = Tuple.makeTuple(m.get(n1), m.get(n2));
						T2<Integer, Integer> tr = Tuple.makeTuple(m.get(n1), m.get(n2));
						Function<Integer,Double> f = new Function<Integer,Double>() {
							@Override
							public Double apply(Integer e2) {
								return getMatchedValues(db, 
										g1.getEdgeFeatures(e),
										g2.getEdgeFeatures(e2));
							}
						};
						if (available.get(t).size() > 0) {  
							//check to see if the edge is preserved
							Integer e2 = Funcles.argmaxCollection(f,available.get(t));
							available.remove(t, e2);
							count += f.apply(e2);
						}
					}
				}
				for (Integer n : m.keySet()) {
					count += (Double) getMatchedValues(db, g1.getNodeFeatures(n), 
							g2.getEdgeFeatures(m.get(n)));
				}

				count /= maxCount;
				return count;
	}

	public double getMatchedValues(DBInterface db, Set<Feature> s1, Set<Feature> s2) {
		double count = 0;
		for (Feature f1 : s1) {
			for (Feature f2 : Sets.newHashSet(s2)) {
				if (db.isCompatible(f1, f2) &&
						featureClassWeights.containsKey(f2.getName())) {
					count += featureClassWeights.get(f2.getName());
					break;
				}
			}
		}

		return count;
	}
}