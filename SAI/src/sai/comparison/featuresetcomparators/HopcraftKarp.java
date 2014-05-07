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

package sai.comparison.featuresetcomparators;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.T2;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.Feature;

/**
 * This featureset comparator assumes that feature-mappings must be unique.
 * This algorithm has a worst-case time complexity of O(m*n^(1/2)), where m
 * is the number of edges and n is the number of nodes.  
 *
 * @author jmorwick
 * @version 2.0.0
 */
public class HopcraftKarp implements BinaryRelation<Set<? extends Feature>> {


    public boolean apply(T2<Set<? extends Feature>,
    		                Set<? extends Feature>> args) {
        Set<? extends Feature> t1s = args.a1();
        Set<? extends Feature> t2s = args.a2();
        
        Set<T2> edges = Sets.newHashSet();
        for(Feature f : t1s) {
            for (Feature f2 : t2s) {
                if(f.compatible(f2))
                    edges.add(T2.makeTuple(f, f2));
            }
        }

        return HopcraftKarpMatch(t1s, t2s, edges) == t1s.size();
    }


    // Hopkraft-Karp algorithm implemented from pseudo-code at:
    // http://en.wikipedia.org/wiki/Hopcroft%E2%80%93Karp_algorithm
    public static <T extends T2> int HopcraftKarpMatch(
            Set g1, Set g2, Set<T> edges)
    {
        Map<Object,Double> dist = Maps.newHashMap();
        Map pair = Maps.newHashMap();
        Multimap adj = HashMultimap.create();
        for(T e : edges) //initialize adjacency map
            adj.put(e.a1(), e.a2());

        int matching = 0;
        while(HKBFS(dist,pair,adj,g1,g2)) {
            for(Object v : g1) {
                if(pair.get(v) == null) {
                    if(HKDFS(dist,pair,adj,g1,g2,v)) {
                        matching = matching + 1;
                    }
                }
            }
        }
        return matching;
    }

    private static <T extends T2> boolean HKBFS(
            Map<Object,Double> dist,
            Map pair,
            Multimap adj,
            Set g1, Set g2)
    {
        Queue q = new LinkedList();

        for(Object v : g1) {
            if(pair.get(v) == null) {
                dist.put(v, 0.0);
                q.offer(v);
            } else {
                dist.put(v, Double.POSITIVE_INFINITY);
            }
        }
        dist.put(null, Double.POSITIVE_INFINITY);
        while(!q.isEmpty()) {
            Object v = q.poll();
            for(Object u : adj.get(v)) {
                if(dist.get(pair.get(u)) == Double.POSITIVE_INFINITY) {
                    dist.put(pair.get(u),
                            dist.get(v)+1);
                }
            }
        }

        return dist.get(null) != Double.POSITIVE_INFINITY;
    }

    private static <T extends T2> boolean HKDFS(
            Map<Object,Double> dist,
            Map pair,
            Multimap adj,
            Set g1, Set g2,
            Object v)
    {
        if(v == null) return true;
        for(Object u : adj.get(v)) {
            if(dist.get(pair.get(u)) == dist.get(v) + 1) {
                if(HKDFS(dist,pair,adj,g1,g2,pair.get(u))) {
                    pair.put(u, v);
                    pair.put(v, u);
                    return true;
                }
            }
        }
        dist.put(v, Double.POSITIVE_INFINITY);
        return false;
        
    }

}