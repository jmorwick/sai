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

import sai.Edge;
import sai.Graph;
import sai.Node;
import sai.comparison.MapHeuristic;


/**
 * A simple map judging heuristic which counts the number of subsumed edges
 * induced by the mapping. 
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class BasicEdgeCount extends MapHeuristic {

    public static int countMappedEdges(Graph g1, Graph g2, Map<Node,Node> m) {
        return (int)(new BasicEdgeCount()).getValue(g1, g2, m);
    }

    @Override
    public double getValue(Graph g1, Graph g2, Map<Node, Node> m) {
            if(g1.edgeSet().size() == 0) return 0;
            int count = 0;
            Bag<T2<Node,Node>> available = new Bag<T2<Node,Node>>();
            for(Edge e : g2.edgeSet()) {
                available.add(Tuple.makeTuple(
                        g2.getEdgeSource(e),
                        g2.getEdgeTarget(e)));
            }
            for(Edge e : g1.edgeSet()) {
                Node n1 = g1.getEdgeSource(e);
                Node n2 = g1.getEdgeTarget(e);
                if(m.containsKey(n1) && m.containsKey(n2)) {
                    T2<Node,Node> t = Tuple.makeTuple(m.get(n1), m.get(n2));
                    if(available.get(t) > 0) {
                        available.incrementCount(t, -1);
                        count++;
                    }
                }
            }
            double value = (double)count / (double)g1.edgeSet().size();
            return value;
    }
}