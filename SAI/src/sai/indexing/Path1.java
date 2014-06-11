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

package sai.indexing;

import java.util.Set;

import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Graphs;
import sai.graph.MutableGraph;

/**
 * An index generator which generates sub-structure indices for each single 
 * edge path in a graph with the specified feature types.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class Path1<G extends Graph> implements IndexGenerator<G> {

    private final String[] featureNames;

    public Path1(String ... featureTypes) {
        this.featureNames = featureTypes;
    }

    @Override
    public Set<G> generateIndices(DBInterface db, GraphFactory<G> gf, Graph s) {
        Set<G> indices = Sets.newHashSet();
        for(int e : s.getEdgeIDs()) {
            Set<Feature> fromNodeFeatures = Sets.newHashSet();
            Set<Feature> toNodeFeatures = Sets.newHashSet();
            Set<Feature> edgeFeatures = Sets.newHashSet();
            edgeFeatures.addAll(SAIUtil.retainOnly(s.getEdgeFeatures(e), featureNames));
            if(edgeFeatures.size() == 0) edgeFeatures.add(null); //make links without edge features
            fromNodeFeatures.addAll(
                    SAIUtil.retainOnly(s.getNodeFeatures(s.getEdgeSourceNodeID(e)),
                    featureNames));
            toNodeFeatures.addAll(
                    SAIUtil.retainOnly(s.getNodeFeatures(s.getEdgeTargetNodeID(e)),
                    featureNames));
            
            for(Feature n1f : fromNodeFeatures)
                for(Feature n2f : toNodeFeatures)
                    for(Feature ef : edgeFeatures) {
                    	MutableGraph i = new MutableGraph();
                    	i.addNode(1);
                    	i.addNode(2);
                    	i.addEdge(1, 1, 2);
                    	i.addNodeFeature(1, n1f);
                    	i.addNodeFeature(2, n2f);
                    	i.addEdgeFeature(1, ef);
                    	i.addFeature(Graphs.INDEX);
                    	indices.add(gf.copy(i));
            }
        }
        return indices;
    }


}