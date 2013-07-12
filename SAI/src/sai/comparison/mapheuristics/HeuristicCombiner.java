/* Copyright 2011 Joseph Kendall-Morwick

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

package org.dataandsearch.sai.comparison.mapheuristics;

import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.tuple.T2;
import org.dataandsearch.sai.Graph;
import org.dataandsearch.sai.Node;
import org.dataandsearch.sai.comparison.MapHeuristic;
import org.dataandsearch.sai.comparison.mapgenerators.search.SearchState;

/**
 * A wrapper for map heuristics which provides a weighted sum of multiple 
 * heuristics.
 *
 * @author jmorwick
 * @version 0.2.0
 */
public class HeuristicCombiner extends MapHeuristic {
    private Map<MapHeuristic, Double> heuristicWeights = new Map<MapHeuristic,Double>();

    public HeuristicCombiner(T2<MapHeuristic,Double> ... heuristics) {
        double totalWeight = 0;
        for(T2<MapHeuristic,Double> t : heuristics) {
            totalWeight += t.a2();
        }
        for(T2<MapHeuristic,Double> t : heuristics) {
            this.heuristicWeights.put(t.a1(), totalWeight == 0.0 ? 1.0 : t.a2()/totalWeight);
        }
    }

    public HeuristicCombiner(MapHeuristic ... heuristics) {
        this(.1, heuristics);
    }

    public HeuristicCombiner(double reductionFactor, MapHeuristic ... heuristics) {
        double weight = 1.0;
        double totalWeight = 0.0;
        for(MapHeuristic h : heuristics) {
            totalWeight += weight;

            weight *= reductionFactor;
        }
        weight = 1.0;
        for(MapHeuristic h : heuristics) {
            this.heuristicWeights.put(h, weight/totalWeight);
            weight *= reductionFactor;
        }
    }

    @Override
    public double getValue(Graph g1, Graph g2, Map<Node, Node> m) {
        double total = 0;
        for(MapHeuristic h : heuristicWeights.keySet()) {
            total += h.getValue(g1, g2, m)*heuristicWeights.get(h);
        }
        return total;
    }

    @Override
    public double getValue(Graph g1, Graph g2, SearchState s) {
        double total = 0;
        for(MapHeuristic h : heuristicWeights.keySet()) {
            total += h.getValue(g1, g2, s)*heuristicWeights.get(h);
        }
        return total;
    }

}