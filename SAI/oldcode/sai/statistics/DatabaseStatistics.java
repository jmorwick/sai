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
package sai.statistics;

import sai.SAIUtil;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.test.graph.Edge;
import info.kendall_morwick.funcles.T3;
import info.kendall_morwick.funcles.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;
/**
 * @since 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class DatabaseStatistics {

    private DBInterface db;
	private GraphFactory<? extends Graph> gf;

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
           

    /** return an iterator that iterates though each possible subset of this set.
     *  subsets are generated on demand so that initial operations will not
     * compromise memory or computation time.  
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
    

	
	
    public DatabaseStatistics(DBInterface db, GraphFactory<? extends Graph> gf) {
        this.db = db;
        this.gf = gf;
    }

    /** TODO: testme */
    public Multiset<T3<Feature, Feature, Feature>> getFeatureLinkFrequencies() {
        return get3FeatureLinkOccurances(1.1);
    }

    /** returns a bag of every possible combination of 3 features with the 
     * following restrictions: 2 are node features which are present two nodes
     * connected by an edge in a graph, and the other is an edge feature on the
     * connecting edge
     * @param progressIncrement the portion of the total progress after which an update should be printed (1.0 is the total)
     */
    public Multiset<T3<Feature, Feature, Feature>> 
    	get3FeatureLinkOccurances(double progressIncrement) {
    	Multiset<T3<Feature, Feature, Feature>> features = HashMultiset.create();
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        if (progressIncrement < 1.0) {
            System.out.println("0% completed");
        }
        Iterator<Integer> i = db.getGraphIDIterator();
        for (Graph g = db.retrieveGraph(i.next(), gf); i.hasNext(); 
        		g = db.retrieveGraph(i.next(), gf)) {
            features = Multisets.union(features, get3FeatureLinkOccurances(g));
            if ((double) read / (double) total > lastMessage + progressIncrement) {
                lastMessage = (double) read / (double) total;
                System.out.println((lastMessage * 100) + "% completed");
            }
        }
        if (progressIncrement < 1.0) {
            System.out.println("100% completed");
        }

        return features;
    }

    /** returns a bag of every possible combination of 3 features with the
     * following restrictions: 2 are node features which are present two nodes
     * connected by an edge in the specified graph, and the other is an edge
     * feature on the connecting edge
     * @param g the graph in which to look for linked features
     */
    public Multiset<T3<Feature, Feature, Feature>> get3FeatureLinkOccurances(Graph g) {
        Multiset<T3<Feature, Feature, Feature>> features = HashMultiset.create();
        System.out.println(g);
        for (Edge e : g.getEdgeIDs()) {
            for (Feature nf1 : g.getEdgeSourceNodeID(e).getFeatures()) {
                for (Feature ef : e.getFeatures()) {
                    for (Feature nf2 : g.getEdgeTargetNodeID(e).getFeatures()) {
                        features.add(Tuple.makeTuple(nf1, ef, nf2));
                    }
                }
            }
        }
        return features;
    }

    /** returns a bag of all features of edges (matching the specified edge 
     * class) incident to a node with the specified node feature in the Graph g.
     */
    public Multiset<Feature> incomingEdgeFeatures(Feature nodeFeature,
            String edgeFeatureName, Graph g) {
        Multiset<Feature> b = HashMultiset.create();
        for (Edge e : g.getEdgeIDs()) {
            if (!g.getEdgeTargetNodeID(e).getFeatures().contains(nodeFeature)) {
                continue;
            }
            for (Feature f : SAIUtil.retainOnly(e.getFeatures(), edgeFeatureName)) {
                b.add(f);
            }
        }
        return b;
    }

    /** returns a bag of all features of edges (matching the specified edge
     * class) incident to a node with the specified node feature.
     * @param progressIncrement the portion of the total progress after which an update should be printed (1.0 is the total)
     */
    public Multiset<Feature> incomingEdgeFeatures(Feature nodeFeature,
            String edgeFeatureName, double progressIncrement) {
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        Multiset<Feature> b = HashMultiset.create();
        if (progressIncrement < 1.0) {
            //System.out.println("0% completed");
        }
        Iterator<Integer> i = db.getGraphIDIterator();
        for (Graph g = db.retrieveGraph(i.next(), gf); i.hasNext(); 
        		g = db.retrieveGraph(i.next(), gf)) {
            b = Multisets.union(b,incomingEdgeFeatures(nodeFeature, edgeFeatureName, g));
            if ((double) read / (double) total > lastMessage + progressIncrement) {
                lastMessage = (double) read / (double) total;
                //System.out.println((lastMessage * 100) + "% completed");
            }
        }
        if (progressIncrement < 1.0) {
            //System.out.println("100% completed");
        }

        return b;

    }

    /** Retrieves all feature-set triples representing the feature-sets of each
     * pair of connected nodes and the feature-sets of the connecting edges.
     * @param progressIncrement the portion of the total progress after which an update should be printed (1.0 is the total)
     */
    public Multiset<T3<Set<Feature>, Set<Feature>, Set<Feature>>> getAllFeatureLinkOccurances(double progressIncrement) {
        Multiset<T3<Set<Feature>, Set<Feature>, Set<Feature>>> features = HashMultiset.create();
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        if (progressIncrement < 1.0) {
            System.out.println("0% completed");
        }
        Iterator<Integer> i = db.getGraphIDIterator();
        for (Graph g = db.retrieveGraph(i.next(), gf); i.hasNext(); 
        		g = db.retrieveGraph(i.next(), gf)) {
            features = Multisets.union(features, getAllFeatureLinkOccurances(g));

            if ((double) read / (double) total > lastMessage + progressIncrement) {
                lastMessage = (double) read / (double) total;
                System.out.println((lastMessage * 100) + "% completed");
            }
        }
        if (progressIncrement < 1.0) {
            System.out.println("100% completed");
        }

        return features;
    }

    /** Retrieves all feature-set triples representing the feature-sets of each
     * pair of connected nodes and the feature-sets of the connecting edges for
     * the specified graph.
     */
    public Multiset<T3<Set<Feature>, Set<Feature>, Set<Feature>>> getAllFeatureLinkOccurances(Graph g) {
        Multiset<T3<Set<Feature>, Set<Feature>, Set<Feature>>> features = HashMultiset.create();
        int total = db.getDatabaseSize();
        System.out.println(g);
        for (Edge e : g.getEdgeIDs()) {
            for (Set<Feature> nf1s : SAIUtil.getAllSubsets(g.getEdgeSourceNodeID(e).getFeatures())) {
                for (Set<Feature> efs : SAIUtil.getAllSubsets(e.getFeatures())) {
                    for (Set<Feature> nf2s : SAIUtil.getAllSubsets(g.getEdgeTargetNodeID(e).getFeatures())) {
                        features.add(Tuple.makeTuple(nf1s, efs, nf2s));
                    }
                }
            }
        }
        return features;
    }
}