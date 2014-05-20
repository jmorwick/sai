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

import java.math.BigInteger;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import sai.graph.jgrapht.Edge;
import sai.graph.jgrapht.Feature;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;

/**
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Util {

    /** returns a set containing only features of the specified type */
    public static Set<? extends Feature> retainOnly(Set<? extends Feature> features,
            Set<? extends Class<? extends Feature>> types) {
        Set<Feature> ret = new HashSet<Feature>();
        for (Feature f : features) {
            for (Class<? extends Feature> type : types) {
                if (type.isInstance(f)) {
                    ret.add(f);
                    break;
                }
            }
        }
        return ret;
    }

    /** returns a set containing only features of the specified type */
    public static Set<? extends Feature> retainOnly(Set<? extends Feature> features,
            Class<? extends Feature>... types) {
        Set<Class<? extends Feature>> ntypes =
                new HashSet<Class<? extends Feature>>();
        for (Class<? extends Feature> t : types) {
            ntypes.add(t);
        }
        return retainOnly(features, ntypes);
    }

    /** Determines which nodes in s1 can be mapped to which nodes in s2.
     * This is determined by checking to see if both nodes have the same features
     * for the feature classes indicated in featureTypes.
     * @param <N> indexable node type
     * @param <E> indexable edge type
     * @param <S> indexable structure type
     * @param s1 structure from which to map nodes
     * @param s2 structure to which to map nodes
     * @param featureTypes feature types to be considered when comparing nodes
     * @return a multi-map indicating which nodes in s1 can be mapped to which in s2
     */
    public static Multimap<Node, Node> nodeCompatibility(Graph s1, Graph s2) {
        return nodeCompatibility(s1.vertexSet(), s2.vertexSet());
    }

    public static Multimap<Node, Node> nodeCompatibility(
            Set<Node> s1,
            Set<Node> s2) {
        Multimap<Node, Node> possibilities = HashMultimap.<Node, Node>create();
        for (Node n1 : s1) {
            for (Node n2 : s2) {
                if (n1.compatible(n2)) {
                    possibilities.put(n1, n2);
                }
            }
        }
        return possibilities;
    }
    public static final Function<Multimap<Edge, Edge>,Integer> completeEdgeMatchCounter =
            new Function<Multimap<Edge, Edge>,Integer>() {

                @Override
                public Integer apply(Multimap<Edge, Edge> p1) {
                    return findRepresentativesComplete(p1).size();
                }
            };

    /** returns the number of subsumed edges for the mapping */
    public static int matchedEdges(Graph s1,
            Graph s2,
            Node n1,
            Map<Node, Node> m,
            Function<Multimap<Edge, Edge>,Integer> countMappableEdges) {
        Multimap<Edge, Edge> possibleMappings = HashMultimap.<Edge, Edge>create();
        Node n2 = m.get(n1);
        if (n2 == null) {
            return 0;
        }

        for (Edge e1 : s1.edgeSet()) {
            for (Edge e2 : s2.edgeSet()) {
                if (!e1.subsumes(e2)) {
                    continue;
                }
                if (n1 == s1.getEdgeSource(e1)
                        && n2 == s2.getEdgeSource(e2)
                        && m.get(s1.getEdgeTarget(e1)) == s2.getEdgeTarget(e2)) {
                    possibleMappings.put(e1, e2);
                } else if (n1 == s1.getEdgeTarget(e1)
                        && n2 == s2.getEdgeTarget(e2)
                        && m.get(s1.getEdgeSource(e1)) == s2.getEdgeSource(e2)
                        && e1.subsumes(e2)) {
                    possibleMappings.put(e1, e2);
                } else if (s1.getDB().directedGraphs()
                        && n1 == s1.getEdgeSource(e1)
                        && n2 == s2.getEdgeTarget(e2)
                        && m.get(s1.getEdgeTarget(e1)) == s2.getEdgeSource(e2)
                        && e1.subsumes(e2)) {
                    possibleMappings.put(e1, e2);
                } else if (s1.getDB().directedGraphs()
                        && n1 == s1.getEdgeTarget(e1)
                        && n2 == s2.getEdgeSource(e2)
                        && m.get(s1.getEdgeSource(e1)) == s2.getEdgeTarget(e2)
                        && e1.subsumes(e2)) {
                    possibleMappings.put(e1, e2);
                }
            }
        }
        return findRepresentativesComplete(possibleMappings).size();
    }

    public static int matchedEdges(Graph s1,
            Graph s2,
            Map<Node, Node> m,
            Function<Multimap<Edge, Edge>,Integer> countMappableEdges) {
        int matches = 0;
        for (Map.Entry<Node, Node> e : m.entrySet()) {
            matches += matchedEdges(s1,
                    s2,
                    e.getKey(),
                    m,
                    countMappableEdges);
        }

        return matches / 2;   //each edge will be counted twice (once on the starting node and once on the ending node)
    }

    public static BigInteger getNumberOfCompleteMappings(Graph g1, Graph g2) {
        Multimap<Node, Node> compatibility = nodeCompatibility(g1, g2);
        return getNumberOfCompleteMappings(compatibility);
    }

    public static BigInteger getNumberOfPartialMappings(Graph g1, Graph g2) {
        Multimap<Node, Node> compatibility = nodeCompatibility(g1, g2);
        return getNumberOfPartialMappings(compatibility);
    }
    
    


    public static <K,V> Multimap<V, K> reverseMultimap(Multimap<K,V> m) {
        Multimap<V,K> ret = HashMultimap.<V,K>create();
        for(K k : Sets.newHashSet(m.keySet())) {
            for(V v : m.get(k)) {
                ret.put(v, k);
            }
        }
        return ret;
    }



  /** performs a complete, EXP-TIME search to select as many unique values for each key as possible.*/
      public static <K extends Comparable,V extends Comparable> Map<K,V> 
      		findRepresentativesComplete(Multimap<K,V> m) {
          BigInteger max = getNumberOfPartialMappings(m);
          Map<K,V> maxmap = Maps.newHashMap();
          for(BigInteger i = BigInteger.ZERO; i.compareTo(max) < 0; i = i.add(BigInteger.ONE)) {
              Map<K,V> m2 = getIthPartialMapping(m, i);
              if(m2.size() > maxmap.size() && isOneToOne(m2))
                  maxmap = m2;
          }
          return maxmap;
      }
      
      public static <K,V> BigInteger getNumberOfCompleteMappings(Multimap<K,V> m) {
          BigInteger i = BigInteger.ONE;
          for(K k : m.keySet()) {
              if(m.get(k).size() > 0)
                  i = i.multiply(BigInteger.valueOf(m.get(k).size()));
          }
          return i;
      }

      /** this will be replaced with an iterator-based method */
      @Deprecated public static <K extends Comparable,V extends Comparable> 
      		Map<K,V> getIthCompleteMapping(Multimap<K,V> m, BigInteger i) {
          Map<K,V> ret = Maps.newHashMap();
          List<K> keylist = Lists.newArrayList(m.keySet());
          Collections.sort(keylist);
          for(K k : keylist) {
              BigInteger j = BigInteger.valueOf(m.get(k).size());
              if(j.equals(BigInteger.ZERO)) continue;
              List<V> values = Lists.newArrayList(m.get(k));
              Collections.sort(values);
              ret.put(k, values.get(i.mod(j).intValue()));
              i = i.divide(j);
          }
          return ret;
      }


      public static <K,V> BigInteger getNumberOfPartialMappings(Multimap<K,V> m) {
          BigInteger ret = BigInteger.ONE;
          for(K k : m.keySet()) {
              ret = ret.multiply(BigInteger.valueOf(m.get(k).size()+1));
          }
          return ret;
      }

      /** this will be replaced with an iterator-based method */
      @Deprecated public static <K extends Comparable,V extends Comparable> 
      		Map<K,V> getIthPartialMapping(Multimap<K,V> m, BigInteger i) {
          Map<K,V> ret = Maps.newHashMap();
          List<K> keylist = Lists.newArrayList(m.keySet());
          Collections.sort(keylist);
          for(K k : keylist) {
              int j = m.get(k).size()+1;
              if(j < 2 || i.mod(BigInteger.valueOf(j)).intValue() == j-1) continue; // in this case, do not map this node
              List<V> values = Lists.newArrayList(m.get(k));
              Collections.sort(values);
              ret.put(k, values.get(i.mod(BigInteger.valueOf(j)).intValue()));
              i = i.divide(BigInteger.valueOf(j));
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
      


      private static <T> Set<T> getSubset(Set<T> s, List<Boolean> id) {
        if(id == null) return null;
        if(id.size() != s.size()) return null;
        Set<T> subset = Sets.newHashSet();

        int i=0;
        for(T x : s) {
          if(id.get(i)) subset.add(x);
          i++;
        }
        return subset;
      }


      private static List<Boolean> getFirstSubsetID(Set s) {
    	  ArrayList<Boolean> ls = Lists.newArrayList();
    	  for(int i=0; i<s.size(); i++)
    		  ls.add(false);
    	  return ls;
      }

      private static List<Boolean> getNextSubsetID(List<Boolean> id) {
        if(id == null) return null;
        id = Lists.newArrayList(id);

        int i=0;
        for(i=0; i<id.size(); i++) {
          if(id.get(i)) {
            id.set(i, false);
          } else {
            id.set(i, true);
            break;
          }
        }
        if(i == id.size()) return null;  //overflow
        return id;
      }

      private static <T> Set<Set<T>> 
      		getPartition(Set<T> s, List<List<Boolean>> id) {
    	s = Sets.newHashSet(s);
        Set<Set<T>> partition = Sets.newHashSet();
        for(List<Boolean> row : id) {
          Set<T> rowContents = Sets.newHashSet();
          rowContents.add(s.iterator().next());
          s.remove(s.iterator().next());

          int i=0;
          for(T x : Sets.newHashSet(s)) {
            if(row.get(i)) {
              rowContents.add(x);
              s.remove(x);
            }
            i++;
          }

          partition.add(rowContents);
        }
        return partition;
      }

      //checks whether or not an entry in a partition ID has its maximum value
      private static boolean rowIsMaxed(List<Boolean> row) {
        boolean maxed = true;
        for(Boolean b : row) if(!b) maxed = false;
        return maxed;
      }

      private static List<Boolean> incrementRow(List<Boolean> row) {
        row = Lists.newArrayList(row);
        for(int i=0; i<row.size(); i++) {
          if(row.get(i))
            row.set(i, false);
          else {
            row.set(i, true);
            break;
          }
        }
        return row;
      }

      
      /** return an iterator that iterates though each possible subset of this set.
       *  subsets are generated on demand so that initial operations will not
       * comprimise memory or computation time.  
       * @return an iterator for all subsets of this set
       */
      public static <T> Iterator<Set<T>> getAllSubsetsIterator(final Set<T> s) {
        return new Iterator<Set<T>>() {
          List<Boolean> i = getFirstSubsetID(s);
          public boolean hasNext() {
            return i != null;
          }

          public Set<T> next() {
            if(i == null) return null;
            Set<T> ret = getSubset(s,i);
            i = getNextSubsetID(i);
            return ret;
          }

          public void remove() {
            throw new UnsupportedOperationException("remove not supported");
          }

        };
      }

      public static <T> Collection<Set<T>> getAllSubsets(Set<T> s) {
          return iteratorToCollection(getAllSubsetsIterator(s));
      }

      

      /** creates a collection from an iterator to allow foreach over iterators */
      public static <A> Collection<A> iteratorToCollection(final Iterator<A> i) {
          return new Collection<A>() {

              public int size() {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean isEmpty() {
                  return i.hasNext();
              }

              public boolean contains(Object o) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public Iterator<A> iterator() {
                  return i;
              }

              public Object[] toArray() {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public <T> T[] toArray(T[] a) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean add(A e) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean remove(Object o) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean containsAll(Collection<?> c) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean addAll(Collection<? extends A> c) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean removeAll(Collection<?> c) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public boolean retainAll(Collection<?> c) {
                  throw new UnsupportedOperationException("Not supported.");
              }

              public void clear() {
                  throw new UnsupportedOperationException("Not supported.");
              }

          };
      }

      

      public static <T> Iterator<Set<Set<T>>> getAllSecondOrderSubsets(Set<Set<T>> p) {
        int ts = 0;
        for(Set<T> s : p) ts += s.size();
        final int totalSize = ts;
        final Set<Set<T>> original = p;
        return new Iterator<Set<Set<T>>>() {

      	  private List<Boolean> i;
          {
        	  i = Lists.newArrayList();
              for(int j=0; j<totalSize; j++)
            	  i.add(false);
          }
          
          public boolean hasNext() {
            return i != null;
          }

          public Set<Set<T>> next() {
            if(i == null) return null;
            Set<Set<T>> newP = Sets.newHashSet();
            int j=0;
            for(Set<T> orow : original) {
              Set<T> newrow = Sets.newHashSet();
              for(T x : orow) {
                if(i.get(j)) newrow.add(x);
                j++;
              }
              if(newrow.size() > 0) newP.add(newrow);
            }

            i = getNextSubsetID(i);
            return newP;
          }

          public void remove() {
            throw new UnsupportedOperationException("remove not supported");
          }

        };
      }

    /** return the i-ith mapping from f to t.  This function is deterministic
     * and each i represents a unique mapping for every i from 0 up to the number
     * of possible mappings.
     * @param <FROM>
     * @param <TO>
     * @param f set of keys to be mapped
     * @param t set of values to be mapped to
     * @param i id of the unique mapping to be generated
     * @return a mapping from f to t
     */
      public static <FROM, TO> Map<FROM, TO> getMapping(Set<FROM> f, Set<TO> t, int i) {
        Map<FROM, TO> m = Maps.newHashMap();
        if(i < 0 || i >= possibleMappings(f.size(), t.size()).intValue()) return m;
        
        if(f.size() > t.size()) return reverseMap(getMapping(t, f, i));
        
        f = Sets.newHashSet();
        t = Sets.newHashSet();
        
        for(FROM from : f) {
          int j = i % t.size();
          i /= t.size();
          for(TO to : t) {
            if(j == 0) m.put(from, to);
            j--;
          }
          t.remove(m.get(from));
        }
        return m;
      }
      
      

      private class MappingCollection<FROM, TO> extends AbstractCollection<Map<FROM, TO>> {
        
        private final int max;
        private final Set<FROM> from;
        private final Set<TO> to;
        
        public MappingCollection(Set<FROM> from, Set<TO> to) {
          this.from = from;
          this.to = to;
          max = possibleMappings(from.size(), to.size()).intValue();
        }
        
            @Override
        public boolean isEmpty() { return max == 0; }
        public int size() { return max; }
        public Iterator<Map<FROM, TO>> iterator() {
          return new Iterator<Map<FROM, TO>>() {
            private int i=0;
            public boolean hasNext() { return i < max; }
            public Map<FROM, TO> next() {
              return getMapping(from, to, i++);
            }
            public void remove() {}
          };
        }
      }

    // utility functions for mapping

      private static BigInteger bigFactorial(int x) {
        return bigFactorial(new BigInteger(""+x));
      }
      private static BigInteger bigFactorial(BigInteger x) {
        BigInteger b = BigInteger.ONE;
        for(;x.compareTo(BigInteger.ONE) > 0; x = x.subtract(BigInteger.ONE)) {
          b = b.multiply(x);
        }
        return b;
      }

      private static BigInteger possibleMappings(int n, int k) {
        return possibleMappings(new BigInteger(""+n), new BigInteger(""+k));
      }
      private static BigInteger possibleMappings(BigInteger n, BigInteger k) {
        if(k.compareTo(n) > 0) return possibleMappings(k, n);
        return bigFactorial(n).divide(bigFactorial(n.subtract(k)));
      }
      
      /** returns the reverse of this map.  If the map is not one-to-one,
       * the returned mapping will include an arbitrary selection.
       * @return
       */
      public static <V,K> Map<V, K> reverseMap(Map<K,V> m) {
        Map<V,K> rev = Maps.newHashMap();
        for(Map.Entry<K,V> e : m.entrySet())
          rev.put(e.getValue(), e.getKey());
        return rev;
      }
      
}