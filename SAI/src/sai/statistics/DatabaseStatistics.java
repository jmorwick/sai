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
package org.dataandsearch.sai.statistics;

import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Edge;
import org.dataandsearch.sai.Feature;
import org.dataandsearch.sai.Graph;

import java.util.Iterator;

import info.kendallmorwick.util.Bag;
import info.kendallmorwick.util.Set;
import info.kendallmorwick.util.tuple.T3;
import org.dataandsearch.sai.comparison.Util;
import static info.kendallmorwick.util.tuple.Tuple.makeTuple;

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
    public Bag<T3<Feature, Feature, Feature>> getFeatureLinkFrequencies() {
        return get3FeatureLinkOccurances(1.1);
    }

    /** returns a bag of every possible combination of 3 features with the 
     * following restrictions: 2 are node features which are present two nodes
     * connected by an edge in a graph, and the other is an edge feature on the
     * connecting edge
     * @param progressIncrement the portion of the total progress after which an update should be printed (1.0 is the total)
     */
    public Bag<T3<Feature, Feature, Feature>> get3FeatureLinkOccurances(double progressIncrement) {
        Bag<T3<Feature, Feature, Feature>> features = new Bag<T3<Feature, Feature, Feature>>();
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        if (progressIncrement < 1.0) {
            System.out.println("0% completed");
        }
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
            features.combine(get3FeatureLinkOccurances(g));
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
    public Bag<T3<Feature, Feature, Feature>> get3FeatureLinkOccurances(Graph g) {
        Bag<T3<Feature, Feature, Feature>> features = new Bag<T3<Feature, Feature, Feature>>();
        System.out.println(g);
        for (Edge e : g.edgeSet()) {
            for (Feature nf1 : g.getEdgeSource(e).getFeatures()) {
                for (Feature ef : e.getFeatures()) {
                    for (Feature nf2 : g.getEdgeTarget(e).getFeatures()) {
                        features.add(makeTuple(nf1, ef, nf2));
                    }
                }
            }
        }
        return features;
    }

    /** returns a bag of all features of edges (matching the specified edge 
     * class) incident to a node with the specified node feature in the Graph g.
     */
    public Bag<Feature> incomingEdgeFeatures(Feature nodeFeature,
            Class<? extends Feature> edgeFeatureType, Graph g) {
        Bag<Feature> b = new Bag<Feature>();
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
    public Bag<Feature> incomingEdgeFeatures(Feature nodeFeature,
            Class<? extends Feature> edgeFeatureType, double progressIncrement) {
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        Bag<Feature> b = new Bag<Feature>();
        if (progressIncrement < 1.0) {
            //System.out.println("0% completed");
        }
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
            b.combine(incomingEdgeFeatures(nodeFeature, edgeFeatureType, g));
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
    public Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>> getAllFeatureLinkOccurances(double progressIncrement) {
        Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>> features = new Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>>();
        int total = db.getDatabaseSize();
        int read = 0;
        double lastMessage = 0.0;
        if (progressIncrement < 1.0) {
            System.out.println("0% completed");
        }
        Iterator<Graph> i = db.getStructureIterator();
        for (Graph g = i.next(); i.hasNext(); g = i.next()) {
            features.combine(getAllFeatureLinkOccurances(g));

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
    public Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>> getAllFeatureLinkOccurances(Graph g) {
        Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>> features = new Bag<T3<Set<Feature>, Set<Feature>, Set<Feature>>>();
        int total = db.getDatabaseSize();
        System.out.println(g);
        for (Edge e : g.edgeSet()) {
            for (Set<Feature> nf1s : g.getEdgeSource(e).getFeatures().getAllSubsets()) {
                for (Set<Feature> efs : e.getFeatures().getAllSubsets()) {
                    for (Set<Feature> nf2s : g.getEdgeTarget(e).getFeatures().getAllSubsets()) {
                        features.add(makeTuple(nf1s, efs, nf2s));
                    }
                }
            }
        }
        return features;
    }
}