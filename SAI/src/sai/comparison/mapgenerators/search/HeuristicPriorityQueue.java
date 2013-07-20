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

package sai.comparison.mapgenerators.search;

import com.google.common.collect.MinMaxPriorityQueue;

import info.km.funcles.Funcles;
import static info.km.funcles.Tuple.makeTuple;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import sai.Feature;
import sai.Graph;
import sai.Node;
import sai.comparison.MapHeuristic;

/**
 * A search priority queue which caches map heuristic computations
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class HeuristicPriorityQueue extends SearchQueue {

    private final MinMaxPriorityQueue<GraphMapping> queue;
    private Map<Node, Node> best;
    private double bestScore = -1;
    private final MapHeuristic queueHueristic;
    private final MapHeuristic judgeHueristic;
    private int bestSize = 0;
    private int maxSize = -1;


    public HeuristicPriorityQueue(final MapHeuristic h,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = h;
        this.judgeHueristic = h;
        queue = MinMaxPriorityQueue.orderedBy(getDefaultQueueComparator()).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic h,
            Comparator<GraphMapping> c,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = h;
        this.judgeHueristic = h;
        queue = MinMaxPriorityQueue.orderedBy(c).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic queueHeuristic,
            final MapHeuristic judgeHeuristic,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = queueHeuristic;
        this.judgeHueristic = judgeHeuristic;
        queue = MinMaxPriorityQueue.orderedBy(getDefaultQueueComparator()).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic queueHeuristic,
            final MapHeuristic judgeHeuristic,
            Comparator<GraphMapping> c,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = queueHeuristic;
        this.judgeHueristic = judgeHeuristic;
        queue = MinMaxPriorityQueue.orderedBy(c).create();
    }
    
    
    public HeuristicPriorityQueue(final MapHeuristic h, int maxSize,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = h;
        this.judgeHueristic = h;
        this.maxSize = maxSize;
        queue = MinMaxPriorityQueue.orderedBy(getDefaultQueueComparator()).maximumSize(maxSize).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic h, 
            Comparator<GraphMapping> c,  int maxSize,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = h;
        this.judgeHueristic = h;
        this.maxSize = maxSize;
        queue = MinMaxPriorityQueue.orderedBy(c).maximumSize(maxSize).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic queueHeuristic, 
            final MapHeuristic judgeHeuristic,  int maxSize,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = queueHeuristic;
        this.judgeHueristic = judgeHeuristic;
        this.maxSize = maxSize;
        queue = MinMaxPriorityQueue.orderedBy(getDefaultQueueComparator()).maximumSize(maxSize).create();
    }

    public HeuristicPriorityQueue(final MapHeuristic queueHeuristic, 
            final MapHeuristic judgeHeuristic,  int maxSize,
            Comparator<GraphMapping> c,
            Class<? extends Feature>... featureTypes) {
        super(featureTypes);
        this.queueHueristic = queueHeuristic;
        this.judgeHueristic = judgeHeuristic;
        this.maxSize = maxSize;
        queue = MinMaxPriorityQueue.orderedBy(c).maximumSize(maxSize).create();
    }

    public MapHeuristic getQueueHeuristic() { return queueHueristic; }
    public MapHeuristic getJudgeHeuristic() { return judgeHueristic; }

    public void considerMap(Map<Node, Node> m) {
        double score = judgeHueristic.apply(makeTuple(getGraph1(), getGraph2(), m));
        if (score > bestScore || (score == bestScore && m.size() > bestSize)) {
            //System.out.println("new best: " + score);
            bestScore = score;
            bestSize = m.size();
            best = m;
        }
    }
    
    public void setMaxQueueSize(int maxSize) {
        this.maxSize = maxSize;
    }


            //***************************  DEBUG *************************
            public static void qpm(Map<Node,Node> m) {
                Set<Map.Entry<Node,Node>> me = new Set<Map.Entry<Node,Node>>(m.entrySet());
                me.sort(new Comparator<Map.Entry<Node,Node>>() {

            public int compare(Entry<Node, Node> o1, Entry<Node, Node> o2) {
                int x = o1.getKey().getID();
                int y = o2.getKey().getID();
                return x < y ? -1 : x > y ? 1 : 0;
            }
        });
                
                for(Map.Entry<Node,Node> e : m.entrySet()) {
                    System.out.print(e.getKey().getID()+"-");
                    if(e.getKey().getID() == e.getValue().getID()) {
                        System.out.print("*");
                    } else System.out.print("X");
                    System.out.print("->" + e.getValue().getID()+", ");

                }
            }
            //***************************  END DEBUG *************************/


    @Override
    public String toString() {
        return "size: " + queue.size() + "\n"
                + "best: " + best.size() + "-" + bestScore;
    }

    public void expand() {
        GraphMapping state = popState();
        state.doOperation();
        expand(state, limitPossibilities(state));
        
    }

    public void expand(GraphMapping state, MultiMap<Node, Node> possibilities) {
        Map<Node, Node> m = state.getMap();
        considerMap(m);

        if (m.keySet().size() == getGraph1().vertexSet().size()) {
            return;  //no child search states
        }


        for (final Node n : possibilities.keySet()) {  //queue all child search states
            for (final Node n2 : possibilities.get(n).difference(m.values())) { //skip already mapped nodes (enforce a 1-1 mapping)
                queue.add(new GraphMapping(m.putC(n, n2), possibilities));
            }
        }
    }

    @Override
    public void seed(Graph g1, Graph g2) {
        if(g1 == null) throw new IllegalArgumentException("seed graph 1 cannot be null");
        if(g2 == null) throw new IllegalArgumentException("seed graph 2 cannot be null");
        queue.clear();
        bestScore = -1;
        best = new Map<Node,Node>();
        super.seed(g1, g2);
        considerMap(getQueue().peek().getMap());
    }

    public boolean empty() {
        return queue.size() == 0 || bestScore == 1.0;
    }

    public Map<Node, Node> getBestMap() {
        return best;
    }

    public MinMaxPriorityQueue<GraphMapping> getQueue() {
        return queue;
    }

    @Override
    public void queueState(GraphMapping s) {
        queue.add(s);
    }

    public GraphMapping popState() {
        return queue.remove();
    }

    public Comparator<GraphMapping> getDefaultQueueComparator() {

        return new Comparator<GraphMapping>() {

            public int compare(GraphMapping s1, GraphMapping s2) {
                double v1 = queueHueristic.getValue(getGraph1(), getGraph2(), s1);
                double v2 = queueHueristic.getValue(getGraph1(), getGraph2(), s2);
                if (v1 == v2) {
                    return 0;
                }
                return v1 > v2 ? 1 : -1;
            }
        };
    }
}