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

package sai.comparison.subgraphcomparators;

import static info.kendall_morwick.funcles.Funcles.apply;
import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.Graphs;

/**
 * This class may not be included in 2.0; I'm still considering its inclusion
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */

//TODO: completely rewrite
public class CompleteSubgraphComparator implements BinaryRelation<Graph> {

	
	
	
	

    /** Determines which nodes in s1 can be mapped to which nodes in s2.
     * @param <N> indexable node type
     * @param <E> indexable edge type
     * @param <S> indexable structure type
     * @param s1 graph from which to map nodes
     * @param s2 graph to which to map nodes
     * @param featureSetComparator determines if sets of features are compatible
     * @return a multi-map indicating which nodes in s1 can be mapped to which in s2
     */
    public static Multimap<Integer, Integer> nodeCompatibility(
    		BinaryRelation<Set<Feature>> featureSetComparator,
    		Graph s1, Graph s2) {
        Multimap<Integer, Integer> possibilities = HashMultimap.create();
        for (Integer n1 : s1.getNodeIDs()) {
            for (Integer n2 : s2.getNodeIDs()) {
                if (apply(featureSetComparator, s1.getNodeFeatures(n1), 
                		s2.getNodeFeatures(n2))) {
                    possibilities.put(n1, n2);
                }
            }
        }
        return possibilities;
    }


    
    private static <K,V> BigInteger getNumberOfCompleteMappings(Multimap<K,V> m) {
        BigInteger i = BigInteger.ONE;
        for(K k : m.keySet()) {
            if(m.get(k).size() > 0)
                i = i.multiply(BigInteger.valueOf(m.get(k).size()));
        }
        return i;
    }

    public static <K extends Comparable, V extends Comparable> Iterator<Map<K,V>> 
    	getMappingIterator(Multimap<K,V> m) {
  	  return getMappingIterator(m, Ordering.<K>natural(), Ordering.<V>natural());
    }

    public static <K,V> Iterator<Map<K,V>> getMappingIterator(final Multimap<K,V> m, 
  		  final Comparator<K> keyComparator,
  		  final Comparator<V> valueComparator) {
  	  final BigInteger limit = getNumberOfCompleteMappings(m);
  	  
  	  return new Iterator<Map<K,V>>() {
  		private BigInteger i = BigInteger.ZERO;
  		
			@Override
			public boolean hasNext() {
				return i.compareTo(limit) < 0;
			}

			@Override
			public Map<K, V> next() {
		        Map<K,V> ret = Maps.newHashMap();
		        List<K> keylist = Lists.newArrayList(m.keySet());
		        Collections.sort(keylist, keyComparator);
		        for(K k : keylist) {
		            BigInteger j = BigInteger.valueOf(m.get(k).size());
                  if(j.equals(BigInteger.ZERO)) continue;
	                List<V> values = Lists.newArrayList(m.get(k));
	                Collections.sort(values, valueComparator);
	                ret.put(k, values.get(i.mod(j).intValue()));
	                i = i.divide(j);
	            }
		        i = i.add(BigInteger.ONE);
		        return ret;
			}

			@Override
			public void remove() {
				//ignored
			}
  		  
  	  };
    }


    public static <K,V> BigInteger getNumberOfPartialMappings(Multimap<K,V> m) {
        BigInteger ret = BigInteger.ONE;
        for(K k : m.keySet()) {
            ret = ret.multiply(BigInteger.valueOf(m.get(k).size()+1));
        }
        return ret;
    }
    
    public static <K,V> boolean isOneToOne(Map<K,V> m) {
        Set<V> values = Sets.newHashSet();
        for(K k : m.keySet()) {
            if(values.contains(m.get(k)))
                return false;
            values.add(m.get(k));
        }
        return true;
    }

    public static BigInteger getNumberOfCompleteMappings(
    		final BinaryRelation<Set<Feature>> featureSetComparator,
    		Graph g1, 
    		Graph g2) {
        Multimap<Integer, Integer> compatibility = nodeCompatibility(featureSetComparator, g1, g2);
        return getNumberOfCompleteMappings(compatibility);
    }

    public static BigInteger getNumberOfPartialMappings(
    		final BinaryRelation<Set<Feature>> featureSetComparator,
    		Graph g1, 
    		Graph g2) {
        Multimap<Integer, Integer> compatibility = nodeCompatibility(featureSetComparator, g1, g2);
        return getNumberOfPartialMappings(compatibility);
    }

    /** performs a complete, exponential-time search to select as many unique values for each key as possible.*/
    public static <K,V> Map<K,V> 
    	findRepresentativesComplete(BinaryRelation<V> comparator, Multimap<K,V> m) {
        BigInteger max = getNumberOfPartialMappings(m);
        Map<K,V> maxmap = Maps.newHashMap();
        Iterator<Map<K,V>> partialMappings = null; //TODO: ********IMPLEMENT THIS ITERATOR!!
        for(BigInteger i = BigInteger.ZERO; i.compareTo(max) < 0; i = i.add(BigInteger.ONE)) {
            Map<K,V> m2 = partialMappings.next();
            if(m2.size() > maxmap.size() && isOneToOne(m2))
                maxmap = m2;
        }
        return maxmap;
    	
    }
    
    

    /** returns the number of subsumed edges for the mapping */
    public static int matchedEdges(
    		final BinaryRelation<Set<Feature>> featureSetComparator,
    		final Graph s1,
            final Graph s2,
            int n1,
            Map<Integer, Integer> m) {
        Multimap<Integer, Integer> possibleMappings = HashMultimap.create();
        Integer n2 = m.get(n1);
        if (n2 == null) {
            return 0;
        }
        
        boolean s1directed = Graphs.isDirected(s1);

        for (Integer e1 : s1.getEdgeIDs()) {
            for (Integer e2 : s2.getEdgeIDs()) {
                if (!apply(featureSetComparator, 
                		s1.getEdgeFeatures(e1), s2.getEdgeFeatures(e2))) {
                    continue;
                }
                if (n1 == s1.getEdgeSourceNodeID(e1)
                        && n2 == s2.getEdgeSourceNodeID(e2)
                        && m.get(s1.getEdgeTargetNodeID(e1)) == s2.getEdgeTargetNodeID(e2)) {
                    possibleMappings.put(e1, e2);
                } else if (n1 == s1.getEdgeTargetNodeID(e1)
                        && n2 == s2.getEdgeTargetNodeID(e2)
                        && m.get(s1.getEdgeSourceNodeID(e1)) == s2.getEdgeSourceNodeID(e2)
                        && apply(featureSetComparator, 
                        		s1.getEdgeFeatures(e1), s2.getEdgeFeatures(e2))) {
                    possibleMappings.put(e1, e2);
                } else if (s1directed
                        && n1 == s1.getEdgeSourceNodeID(e1)
                        && n2 == s2.getEdgeTargetNodeID(e2)
                        && m.get(s1.getEdgeTargetNodeID(e1)) == s2.getEdgeSourceNodeID(e2)
                        && apply(featureSetComparator,
                        		s1.getEdgeFeatures(e1), s2.getEdgeFeatures(e2))) {
                    possibleMappings.put(e1, e2);
                } else if (s1directed
                        && n1 == s1.getEdgeTargetNodeID(e1)
                        && n2 == s2.getEdgeSourceNodeID(e2)
                        && m.get(s1.getEdgeSourceNodeID(e1)) == s2.getEdgeTargetNodeID(e2)
                        && apply(featureSetComparator,
                        		s1.getEdgeFeatures(e1), s2.getEdgeFeatures(e2))) {
                    possibleMappings.put(e1, e2);
                }
            }
        }
        return findRepresentativesComplete(new BinaryRelation<Integer>() {

			@Override
			public boolean apply(Pair<Integer> args) {
				return Funcles.apply(featureSetComparator, 
						s1.getEdgeFeatures(args.a1()), 
						s2.getEdgeFeatures(args.a2()));
			}
        }, possibleMappings).size();
    }
    public static int matchedEdges(
    		final BinaryRelation<Set<Feature>> featureSetComparator,
    		Graph s1,
            Graph s2,
            Map<Integer, Integer> m) {
        int matches = 0;
        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            matches += matchedEdges(
            		featureSetComparator,
            		s1,
                    s2,
                    e.getKey(),
                    m);
        }

        return matches / 2;   //each edge will be counted twice (once on the starting node and once on the ending node)
    }

	public static boolean compare(DBInterface db, Graph g1, Graph g2, 
			BinaryRelation<Set<Feature>> featureSetComparator) {
		CompleteSubgraphComparator csc = 
				new CompleteSubgraphComparator(db, featureSetComparator);
		return Funcles.apply(csc, g1, g2);
	}

	private BinaryRelation<Set<Feature>> featureSetComparator;

	public CompleteSubgraphComparator(final DBInterface db, 
			BinaryRelation<Set<Feature>> featureSetComparator) {
		this.featureSetComparator = featureSetComparator;
	}


	@Override
	public boolean apply(Pair<Graph> args) {
		Graph sub = args.a1();
		Graph sup = args.a2();
		Multimap<Integer, Integer> possibilities = SAIUtil.nodeCompatibility(
				featureSetComparator, sub,
				sup);

		//make sure the features for the graphs themselves are compatible
		//TODO: determine why eclipse thinks the import IS NOT WORKING below... :(
		//should be: if(!apply(featureSetComparator,
		if(!info.kendall_morwick.funcles.Funcles.apply(featureSetComparator,
				sub.getFeatures(),
				sup.getFeatures())) {
			return false;
		}

		
		Iterator<Map<Integer,Integer>> i = SAIUtil.getMappingIterator(possibilities, 
				Ordering.<Integer>natural(), Ordering.<Integer>natural());
		for(Map<Integer,Integer> map : SAIUtil.iteratorToCollection(i)) {
			if(map.size() < possibilities.size()) {
				return false;
			} else if(SAIUtil.matchedEdges(
					featureSetComparator,
					sub,
					sup,
					map) ==
					sub.getEdgeIDs().size() &&
					map.size() == sup.getNodeIDs().size()) {
				return true;
			}
		}

		return false; // TODO: is this an error? I was throwing an exception here
	}

}
