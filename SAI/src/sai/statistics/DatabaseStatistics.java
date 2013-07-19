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

import sai.DBInterface;
import sai.Edge;
import sai.Feature;
import sai.Graph;
import sai.comparison.Util;
import info.km.funcles.T3;
import info.km.funcles.Tuple;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
/**
 * @since 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class DatabaseStatistics {

    private DBInterface db;

    public DatabaseStatistics(DBInterface db) {
        this.db = db;
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
    public Multiset<T3<Feature, Feature, Feature>> get3FeatureLinkOccurances(double progressIncrement) {
    	Multiset<T3<Feature, Feature, Feature>> features = HashMultiset.create();
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        if (progressIncrement < 1.0) {
            System.out.println("0% completed");
        }
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
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
        for (Edge e : g.edgeSet()) {
            for (Feature nf1 : g.getEdgeSource(e).getFeatures()) {
                for (Feature ef : e.getFeatures()) {
                    for (Feature nf2 : g.getEdgeTarget(e).getFeatures()) {
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
            Class<? extends Feature> edgeFeatureType, Graph g) {
        Multiset<Feature> b = HashMultiset.create();
        for (Edge e : g.edgeSet()) {
            if (!g.getEdgeTarget(e).getFeatures().contains(nodeFeature)) {
                continue;
            }
            for (Feature f : Util.retainOnly(e.getFeatures(), edgeFeatureType)) {
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
            Class<? extends Feature> edgeFeatureType, double progressIncrement) {
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        Multiset<Feature> b = HashMultiset.create();
        if (progressIncrement < 1.0) {
            //System.out.println("0% completed");
        }
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
            b = Multisets.union(b,incomingEdgeFeatures(nodeFeature, edgeFeatureType, g));
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
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
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
        for (Edge e : g.edgeSet()) {
            for (Set<Feature> nf1s : Util.getAllSubsets(g.getEdgeSource(e).getFeatures())) {
                for (Set<Feature> efs : Util.getAllSubsets(e.getFeatures())) {
                    for (Set<Feature> nf2s : Util.getAllSubsets(g.getEdgeTarget(e).getFeatures())) {
                        features.add(Tuple.makeTuple(nf1s, efs, nf2s));
                    }
                }
            }
        }
        return features;
    }
}