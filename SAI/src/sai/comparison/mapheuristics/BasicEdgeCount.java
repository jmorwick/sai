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

import info.kendall_morwick.funcles.T3;
import info.kendall_morwick.funcles.Tuple;
import info.kendall_morwick.funcles.T2;

import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import sai.comparison.MapHeuristic;
import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.jgrapht.Edge;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;


/**
 * A simple map judging heuristic which counts the number of subsumed edges
 * induced by the mapping. 
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class BasicEdgeCount extends MapHeuristic {

    public static int countMappedEdges(Graph g1, Graph g2, GraphMapping ss) {
        return (int)(double)(new BasicEdgeCount()).apply(Tuple.makeTuple(g1, g2, ss));
    }

    @Override
	public Double apply(T3<Graph, Graph, GraphMapping> args) {
    		Graph g1 = args.a1();
    		Graph g2 = args.a2();
    		Map<Node, Node> m = args.a3();
            if(g1.edgeSet().size() == 0) return 0.0;
            int count = 0;
            Multiset<T2<Node,Node>> available = HashMultiset.create();
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
                    if(available.count(t) > 0) {
                        available.setCount(t, -1);
                        count++;
                    }
                }
            }
            double value = (double)count / (double)g1.edgeSet().size();
            return value;
    }
}