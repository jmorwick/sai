package sai.graph.jgrapht;

import info.kendall_morwick.funcles.T2;
import info.kendall_morwick.funcles.T3;

import java.util.Map;

import sai.comparison.heuristics.GraphMatchingHeuristic;
import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.Graph;
import sai.test.graph.Node;

import com.google.common.collect.Sets;

public class JGraphTMapHeuristics {

    public static final GraphMatchingHeuristic AVOID_LOWER_DEGREE = new GraphMatchingHeuristic() {

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

    public static final GraphMatchingHeuristic AVOID_LOWER_OUT_DEGREE = new GraphMatchingHeuristic() {

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


    public static final GraphMatchingHeuristic AVOID_LOWER_IN_DEGREE = new GraphMatchingHeuristic() {

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

    public static final GraphMatchingHeuristic AVOID_HIGHER_DEGREE = new GraphMatchingHeuristic() {

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


    public static final GraphMatchingHeuristic AVOID_HIGHER_OUT_DEGREE = new GraphMatchingHeuristic() {

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


    public static final GraphMatchingHeuristic AVOID_HIGHER_IN_DEGREE = new GraphMatchingHeuristic() {

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



    public static final GraphMatchingHeuristic MINIMIZE_DISTANCE = new GraphMatchingHeuristic() {

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
