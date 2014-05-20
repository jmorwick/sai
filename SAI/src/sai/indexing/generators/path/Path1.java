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

package sai.indexing.generators.path;

import java.util.HashSet;
import java.util.Set;

import sai.comparison.Util;
import sai.db.DBInterface;
import sai.graph.Edge;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Index;
import sai.indexing.IndexGenerator;

/**
 * An index generator which generates sub-structure indices for each single 
 * edge path in a graph with the specified feature types.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Path1<G extends Graph> implements IndexGenerator<G> {

    private final String[] featureTypes;

    public Path1(String ... featureTypes) {
        this.featureTypes = featureTypes;
    }

    @Override
    public Set<Index> generateIndices(DBInterface db, GraphFactory<G> gf, Graph s) {
        Set<Index> indices = new HashSet<Index>();
        for(Edge e : s.getEdges()) {
            Set<Feature> fromNodeFeatures = new HashSet<Feature>();
            Set<Feature> toNodeFeatures = new HashSet<Feature>();
            Set<Feature> edgeFeatures = new HashSet<Feature>();
            edgeFeatures.addAll(Util.retainOnly(e.getFeatures(), featureTypes));
            if(edgeFeatures.size() == 0) edgeFeatures.add(null); //make links without edge features
            fromNodeFeatures.addAll(
                    Util.retainOnly(s.getEdgeSource(e).getFeatures(),
                    featureTypes));
            toNodeFeatures.addAll(
                    Util.retainOnly(s.getEdgeTarget(e).getFeatures(),
                    featureTypes));
            
            for(Feature n1f : fromNodeFeatures)
                for(Feature n2f : toNodeFeatures)
                    for(Feature ef : edgeFeatures) {
                Index i = new Index(getDB());
                Node fn = new Node(i, getDB());
                Node tn = new Node(i, getDB());
                Edge edge = new Edge(i, getDB());
                fn.addFeature(n1f);
                tn.addFeature(n2f);
                if(ef != null) edge.addFeature(ef);
                i.addEdge(fn, tn, edge);
                indices.add(i);
            }
        }
        return indices;
    }


}