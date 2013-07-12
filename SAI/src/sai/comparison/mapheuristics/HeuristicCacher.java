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
package sai.comparison.mapheuristics;

import sai.Graph;
import sai.Node;
import sai.comparison.MapHeuristic;

/**
 * A wrapper for map heuristics which cache the values they generate
 *
 * @author jmorwick
 * @version 0.2.0
 */
public class HeuristicCacher extends MapHeuristic {

    private final MapHeuristic h;
    private Cache<Map<Node, Node>, Double> cachedValues = null;
    private Graph g1C = null;
    private Graph g2C = null;

    public HeuristicCacher(MapHeuristic h, int maxCacheSize) {
        this.h = h;
        cachedValues = new Cache<Map<Node, Node>, Double>(maxCacheSize);
    }

    @Override
    public double getValue(Graph g1, Graph g2, Map<Node, Node> m) {
        //clear the cache if we're looking at new graphs
        if (g1C == null || g2C == null
                || (g1C != g1 && !g1C.equals(g1))
                || g2C != g2 && !g2C.equals(g2)) {
            g1C = g1;
            g2C = g2;
            cachedValues.clear();
        }


        if (!cachedValues.contains(m)) {
            double value = h.getValue(g1, g2, m);
            cachedValues.set(m, value);
            return value;
        }
        
        return cachedValues.get(m);

    }
}