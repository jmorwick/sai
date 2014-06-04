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

package sai.comparison.mapgenerators.search;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import sai.SAIUtil;
import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;

/**
 * An abstract class for implementing BFS expansion and ranking algorithms
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public abstract class SearchQueue {
    private Multimap<Node, Node> possibilities = null;
    private Graph g1;
    private Graph g2;
    private Class<? extends Feature>[] types;

    private Map<Node,Node> inleaves1 = Maps.newHashMap();
    private Map<Node,Node> outleaves1 = Maps.newHashMap();
    private Map<Node,Node> inleaves2 = Maps.newHashMap();
    private Map<Node,Node> outleaves2 = Maps.newHashMap();


    public SearchQueue(Class<? extends Feature> ... types) {
        this.types = types;
    }


    public Graph getGraph1() {
        return g1;
    }

    public Graph getGraph2() {
        return g2;
    }

    public void seed(Graph g1, Graph g2) {
        if(g1 == null) throw new IllegalArgumentException("seed graph 1 cannot be null");
        if(g2 == null) throw new IllegalArgumentException("seed graph 2 cannot be null");
        this.g1 = g1;
        this.g2 = g2;
        
        //find leaves
        for(Node n : g1.vertexSet()) {
            if(g1.inDegreeOf(n) == 0 && g1.outDegreeOf(n) == 1) {
                outleaves1.put(n,g1.getLinkedToNodes(n).iterator().next());
            } else if(g1.inDegreeOf(n) == 1 && g1.outDegreeOf(n) == 0) {
                inleaves1.put(n,g1.getLinkedFromNodes(n).iterator().next());
            }
        }
        for(Node n : g2.vertexSet()) {
            if(g2.inDegreeOf(n) == 0 && g2.outDegreeOf(n) == 1) {
                outleaves2.put(n,g2.getLinkedToNodes(n).iterator().next());
            } else if(g2.inDegreeOf(n) == 1 && g2.outDegreeOf(n) == 0) {
                inleaves2.put(n,g2.getLinkedFromNodes(n).iterator().next());
            }
        }

        Map<Node, Node> m = Maps.newHashMap();
        possibilities = SAIUtil.nodeCompatibility(g1, g2, types);
        GraphMapping s = new GraphMapping(m, possibilities);
        limitPossibilities(s);
        queueState(s);
    }

    public Multimap<Node,Node> limitPossibilities(GraphMapping s) {
        Multimap<Node,Node> possibilities = HashMultimap.create(possibilities);
        int newsize = -1;
        int oldsize = s.getMap().size();
        while(newsize != oldsize) {
            oldsize = newsize;
            mapSingularOpportunities(s.getMap(), possibilities);
            for(Node n1 : possibilities.keySet()) {  //remove mapped nodes from possibilities
                if(s.getMap().containsKey(n1)) possibilities.remove(n1);
                
                for(Node n2 : possibilities.get(n1)) {
                    if(s.getMap().values().contains(n2))
                        possibilities.remove(n1,n2);
                }
            }
            newsize = s.getMap().size();
        }
        return possibilities;
    }

    public abstract void queueState(GraphMapping s);

    public abstract void expand();

    public abstract boolean empty();

    public abstract Map<Node, Node> getBestMap();




    public static void printPossibilities(MultiMap<Node,Node> possibilities) {

        System.out.println("--------------------------------------------"+(possibilities.size()));
        for (Node n : possibilities.keySet()) {
            Set<Integer> s = new Set<Integer>();
            for (Node n2 : possibilities.get(n)) {
                s.add(n2.getID());
            }
            System.out.println(n.getID() + ": " + s);
        }
    }


    public static void printMap(Map<Node,Node> m) {

        System.out.println("--------------------------------------------"+(m.size()));
        for (Node n : m.keySet()) {
            System.out.println(n.getID() + ": " + m.get(n).getID());
        }
    }


    public void mapSingularOpportunities(Map<Node, Node> m,
            MultiMap<Node, Node> possibilities) {
        MultiMap possibilitiesr = possibilities.reverseMultimap();
        for (Node n : possibilities.keySet()) {  //map any nodes with only 1 alternative
            if (possibilities.get(n).size() == 1 && //n is mapped to only one node
                    possibilitiesr.get(possibilities.get(n) //that node is only mapped to n
                    .getFirstElement()).size() == 1) {
                Node n2 = possibilities.get(n).getFirstElement();
                m.put(n, n2);
                possibilities.remove(n);
                possibilities.removeValues(n2);
            }
        }
    }

    public void mapLeaves(Map<Node, Node> m, MultiMap<Node,Node> possibilities) {
        for(Node n : inleaves1.keySet().difference(m.keySet())) {
            if(m.containsKey(inleaves1.get(n))) {
                for(Node nn : possibilities.get(n).intersection(inleaves2.keySet())) {
                    if(inleaves2.get(nn) == m.get(inleaves1.get(n))) {
                        System.out.println("Mapping " +n.getID() + " to " +nn.getID() + " as arbitrary alternative");
                        m.put(n, nn);
                        possibilities.remove(n);
                        possibilities.removeValues(nn);
                        break;
                    }
                }
            }
        }
        for(Node n : outleaves1.keySet().difference(m.keySet())) {
            if(m.containsKey(outleaves1.get(n))) {
                for(Node nn : possibilities.get(n).intersection(outleaves2.keySet())) {
                    if(outleaves2.get(nn) == m.get(outleaves1.get(n))) {
                        System.out.println("Mapping " +n.getID() + " to " +nn.getID() + " as arbitrary alternative");
                        m.put(n, nn);
                        possibilities.remove(n);
                        possibilities.removeValues(nn);
                        break;
                    }
                }
            }
        }
    }

}