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


import java.util.Map;

import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.Graph;
import sai.graph.Node;
import info.kendall_morwick.funcles.T2;
import info.kendall_morwick.funcles.T3;
import info.kendall_morwick.funcles.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * This class houses a method for judging the utility of a mapping between
 * two graphs.  These are mainly used for ranking retrieval candidates.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public interface MapHeuristic extends Function<T3<Graph,Graph,GraphMapping>,Double>{

	
    public static final MapHeuristic AVOID_LOWER_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.inDegreeOf(n) + g1.outDegreeOf(n);
                int degree2 = g2.inDegreeOf(m.get(n)) +
                              g2.outDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 >= degree) tot += 1;
                else tot += (double) degree2 / (double) degree;
            }
            return tot/(double)m.size();
        }

    };

    public static final MapHeuristic AVOID_LOWER_OUT_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.outDegreeOf(n);
                int degree2 = g2.outDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 >= degree) tot += 1;
                else tot += (double) degree2 / (double) degree;
            }
            return tot/(double)m.size();
        }

    };


    public static final MapHeuristic AVOID_LOWER_IN_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.inDegreeOf(n);
                int degree2 = g2.inDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 >= degree) tot += 1;
                else tot += (double) degree2 / (double) degree;
            }
            return tot/(double)m.size();
        }

    };

    public static final MapHeuristic AVOID_HIGHER_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.inDegreeOf(n) + g1.outDegreeOf(n);
                int degree2 = g2.inDegreeOf(m.get(n)) +
                              g2.outDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 <= degree) tot += 1;
                else tot += (double) degree / (double) degree2;
            }
            return tot/(double)m.size();
        }

    };


    public static final MapHeuristic AVOID_HIGHER_OUT_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.outDegreeOf(n);
                int degree2 = g2.outDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 <= degree) tot += 1;
                else tot += (double) degree / (double) degree2;
            }
            return tot/(double)m.size();
        }

    };


    public static final MapHeuristic AVOID_HIGHER_IN_DEGREE = new MapHeuristic() {

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            double tot = 0.0;
            if(m.size() == 0) return 0.0;

            for(Node n : m.keySet()) {
                int degree = g1.inDegreeOf(n);
                int degree2 = g2.inDegreeOf(m.get(n));
                if(degree2 == 0 && degree == 0) tot += 1;
                else if(degree2 <= degree) tot += 1;
                else tot += (double) degree / (double) degree2;
            }
            return tot/(double)m.size();
        }

    };



    public static final MapHeuristic MINIMIZE_DISTANCE = new MapHeuristic() {

        private Graph g1 = null;
        private Graph g2 = null;
        private Map<T2<Node,Node>,Double> g1Dist;
        private Map<T2<Node,Node>,Double> g2Dist;

        @Override
        public Double apply(T3<Graph,Graph,GraphMapping> args) {
        	Graph g1 = args.a1();
        	Graph g2 = args.a2();
        	Map<Node,Node> m = args.a3();
        	
            if(this.g1 != g1 || this.g2 != g2) {
                this.g1 = g1;
                this.g2 = g2;
                g1Dist = g1.allPairsShortestPaths();
                g2Dist = g2.allPairsShortestPaths();
            }
            double worstPossible = g2.vertexSet().size()-1;
            double worstTotal = 0;
            double mistakeTotal = 0;

            for(Node n1 : Sets.newHashSet(m.keySet())) {
                for (Node n2 : Sets.newHashSet(m.keySet())) {
                    T2<Node,Node> t = T2.makeTuple(n1, n2);
                    if(n1 == n2 || !g1Dist.containsKey(t)) continue;
                    double minDist = g1Dist.get(t);
                    worstTotal += worstPossible - minDist;
                    n1 = m.get(n1);
                    n2 = m.get(n2);
                    t = T2.makeTuple(m.get(n1), m.get(n2));
                    double actDist = g2Dist.get(t) == null ? worstPossible : g2Dist.get(t);
                    if(actDist > minDist) {
                        mistakeTotal += actDist - minDist;
                    }
                }
            }

            if(worstTotal == 0) return 1.0;
            return 1 - mistakeTotal/worstTotal;
    }};


}