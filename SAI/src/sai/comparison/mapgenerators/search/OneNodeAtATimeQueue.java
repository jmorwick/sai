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


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static info.kendall_morwick.funcles.Tuple.makeTuple;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.comparison.MapHeuristic;
import sai.comparison.mapgenerators.search.HeuristicPriorityQueue;
import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;

/**
 * A search queue with a 2-phase expansion: 1 for selecting which node to
 * expand, and the second to select which other node the node should be
 * mapped to.  
 *
 * @author jmorwick
 * @version 2.0.0
 */
public class OneNodeAtATimeQueue extends HeuristicPriorityQueue {

    private long bfsTime = 0;
    private long startTime = 0;

    public OneNodeAtATimeQueue(final MapHeuristic queueHeuristic,
            final MapHeuristic judgeHeuristic,
            boolean directed,
            Class<? extends Feature>... featureTypes) {
        super(queueHeuristic, judgeHeuristic, featureTypes);

    }
    
    public OneNodeAtATimeQueue(final MapHeuristic h,
            Class<? extends Feature>... featureTypes) {
        super(h, featureTypes);

        
    }

    @Override
    public GraphMapping popState() {


            /***************************  DEBUG *************************
        System.out.println("queue: ");
        for(SearchState s : getQueue()) {
            System.out.print(s.hashCode() + "  ");
            qpm(s.getMap());
            if(s instanceof NodeChoiceSearchState) {
                System.out.print(" ["+((NodeChoiceSearchState)s).getNodeToMap().getID()+"]");
            }
            System.out.print(" - " + getQueueHeuristic().getValue(getGraph1(), getGraph2(), s));
            System.out.println(" - " + getJudgeHeuristic().getValue(getGraph1(), getGraph2(), s));
        }
        System.out.println("\n\n");
            //***************************  END DEBUG *************************/

        if(System.currentTimeMillis() - startTime < bfsTime) {
            System.out.println("Breadth");
            List<GraphMapping> ls = Lists.newArrayList(this.getQueue());
            GraphMapping s = ls.get(0);
            for(GraphMapping ss : ls) {
                if(ss.getMap().size() < s.getMap().size() ||
                   (
                     ss.getMap().size() == s.getMap().size()) &&
                       getQueueHeuristic().apply(makeTuple(getGraph1(), getGraph2(), ss)) <
                       getQueueHeuristic().apply(makeTuple(getGraph1(), getGraph2(), s)))
                    s = ss;
            }
            this.getQueue().remove(s);
            return s;
        }

        return super.popState();
    }

    @Override
    public void seed(Graph g1, Graph g2) {
        super.seed(g1, g2);
        this.startTime = System.currentTimeMillis();
    }

    public void setBreadthFirstTime(long time) {
        this.bfsTime = time;
    }

            //***************************  DEBUG *************************
    public String shorten(Set<Node> s) {
        String str = "[";
        for(Node n : s) str += str.equals("[") ? n.getID() : ", " + n.getID();
        return str+"]";
    }
            //***************************  END DEBUG *************************/

    @Override
    public void expand(GraphMapping state, Multimap<Node, Node> possibilities) {
            //***************************  DEBUG *************************
            System.out.println("expanding state " + state.hashCode());
            //***************************  END DEBUG *************************/
        Map<Node, Node> m = state.getMap();
        Set<Integer> mapped = Sets.newHashSet();
        for(Node n : m.keySet()) mapped.add(n.getID());
        
        if (m.keySet().size() == getGraph1().vertexSet().size()) {
            considerMap(m);
            return;  //no child search states
        }

            //***************************  DEBUG *************************
            HeuristicPriorityQueue.qpm(m);
            //***************************  END DEBUG *************************/


        if (state instanceof NodeChoiceSearchState) {  //expand only on this node
            Node n = ((NodeChoiceSearchState) state).getNodeToMap();

            /***************************  DEBUG *************************
            System.out.println("expanding node " + n.getID());
            //***************************  END DEBUG *************************/

            for (Node n2 : possibilities.keySet()) {
                if (n != n2) {
                    possibilities.remove(n2);
                }
            }
            super.expand(state, possibilities);
        } else {  // queue states for each mappable node

            Set<Node> mappableFringe = getGraph1().getFringe(m.keySet());
            Set<Node> isolated = new Set<Node>();
            for(Node n : mappableFringe.copy()) {
                if(isIsolated(getGraph1(), m, n)) {
                    isolated.add(n);
                    mappableFringe.remove(n);
                }
            }
            /***************************  DEBUG *************************
            System.out.print ("exp: " + shorten(mappableFringe) + shorten(isolated));
            //***************************  END DEBUG *************************/
            if(mappableFringe.size() > 0) {
            /***************************  DEBUG *************************
                System.out.println(" mappable");
            /***************************  END DEBUG *************************/
                Set<NodeChoiceSearchState> nstates = new Set<NodeChoiceSearchState>();
                for (final Node n : mappableFringe) {  //queue all child search states connected to mapped nodes
                    getQueue().add(new NodeChoiceSearchState(m, possibilities, n));
                }
                /*
                    nstates.add(new NodeChoiceSearchState(m, possibilities, n));
                }

                final MapHeuristic h = getQueueHeuristic();
                final Graph g1 = getGraph1();
                final Graph g2 = getGraph2();
                getQueue().offer((new Function<Double,NodeChoiceSearchState>(){

                    @Override
                    public Double implementation(NodeChoiceSearchState s) {
                        return h.getValue(g1, g2, s);
                    }
                }).argmaxC(nstates));
                */
            } else if(isolated.size() > 0) {  //select a random isolated node
            /***************************  DEBUG *************************
                System.out.println(" isolated");
            /***************************  END DEBUG *************************/
                getQueue().add(new NodeChoiceSearchState(m, possibilities, isolated.getRandomElement()));
            } else {
            /***************************  DEBUG *************************
                System.out.println(" brand new");
            //***************************  END DEBUG *************************/
                for (final Node n : possibilities.keySet()) {  //queue all child search states
                    getQueue().add(new NodeChoiceSearchState(m, possibilities, n));
                }
            }
        }
    }

    /** determines whether or not the node is not connected to any unmapped nodes.  It is
        considered 'isolated' if it is not connected to an unmapped node.  */
    public static boolean isIsolated(Graph g, Map<Node,Node> m, Node n) {
        return Sets.difference(
        		 Sets.union(g.getLinkedFromNodes(n), g.getLinkedToNodes(n)), 
        		 m.keySet()).size() == 0;
    }



    @Override
    public Comparator<GraphMapping> getDefaultQueueComparator() {
        final Comparator<GraphMapping> supercomp = super.getDefaultQueueComparator();

        return new Comparator<GraphMapping>() {

            public int compare(GraphMapping s1, GraphMapping s2) {

                int orig =  supercomp.compare(s1, s2);
                if(orig != 0) return orig;
                else {
                    if(s1 instanceof NodeChoiceSearchState && !(s2 instanceof NodeChoiceSearchState))
                        return -1;
                    else if(s2 instanceof NodeChoiceSearchState && !(s1 instanceof NodeChoiceSearchState))
                        return 1;
                    else return 0;
                }
            }
        };
    }
}