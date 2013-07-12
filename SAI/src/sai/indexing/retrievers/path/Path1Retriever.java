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

package org.dataandsearch.sai.indexing.retrievers;

import info.kendallmorwick.util.List;
import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.Set;
import info.kendallmorwick.util.tuple.T2;
import info.kendallmorwick.util.tuple.Tuple;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Edge;
import org.dataandsearch.sai.Feature;
import org.dataandsearch.sai.Graph;
import org.dataandsearch.sai.Node;
import org.dataandsearch.sai.indexing.Index;
import org.dataandsearch.sai.indexing.IndexRetriever;
import org.dataandsearch.sai.indexing.generators.path.Path1;


/**
 * An optimized index retriever for indices generated by Path1 or Path1Lookup
 * index generators. 
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Path1Retriever extends IndexRetriever {
    private final Path1 gen;



    public Path1Retriever(DBInterface db,
            Class<? extends Feature> ... featureTypes) {
        super(db);
        gen = new Path1(db, featureTypes);
    }


    @Override
    public Set<Index> retrieveIndices(Graph g) {
        return findOriginalLinkIndices(getDB(), gen.generateIndices(g)).a1();
    }

    public static Set<Index> removeDuplicates(Set<Index> indices) {
        Map<String,Index> m = new Map<String,Index>();
        for(Index i : indices) {
            Edge e = i.edgeSet().getFirstElement();
            Node fn = i.getEdgeSource(e);
            Node tn = i.getEdgeTarget(e);
            String str = 
                    fn.getFeatures().getFirstElement().getID() + 
                    "-" + (e.getFeatures().size() != 0 ? 
                        e.getFeatures().getFirstElement().getID() : "") +
                    "->>" + tn.getFeatures().getFirstElement().getID();
            m.put(str, i);
        }
        return m.values();
    }

    public static T2<Set<Index>, Set<Index>> findOriginalLinkIndices(
            DBInterface db, Set<Index> indices) {
        Set<Index> discoveredIndices = new Set<Index>();
        indices = removeDuplicates(indices);
        for(Index i : indices.copy()) {
            Edge e = i.edgeSet().getFirstElement();
            Node fn = i.getEdgeSource(e);
            Node tn = i.getEdgeTarget(e);
            String sql = "SELECT gi.id FROM graph_instances gi, " +
                    "edge_instances e, node_features fnf, node_features tnf " +
                    (e.getFeatures().size() != 0 ? ", edge_features ef " : "") +
                    " WHERE "+
                    "fnf.node_id = e.from_node_id AND " +
                    "tnf.node_id = e.to_node_id AND " +
                    "tnf.graph_id = e.graph_id AND " +
                    "fnf.graph_id = e.graph_id AND " +
                    (e.getFeatures().size() != 0 ?
                    "ef.graph_id = e.graph_id AND " +
                    "ef.edge_id = e.id AND " : "") +
                    "gi.id = e.graph_id AND " +
                    " gi.is_index = TRUE AND " +
                    "fnf.feature_id = " + fn.getFeatures().getFirstElement().getID() +
                    " AND tnf.feature_id = " + tn.getFeatures().getFirstElement().getID() +
                    (e.getFeatures().size() != 0 ?
                    " AND ef.feature_id = " +
                    e.getFeatures().getFirstElement().getID() : "") +
                    " LIMIT 1";
            List<Map<String,String>> rows = db.getQueryResults(sql);
            if(rows.size() > 0) {
                discoveredIndices.add(
                        (Index)db.loadStructureFromDatabase(
                          Integer.parseInt(rows.get(0).get("id"))));
                indices.remove(i);
            }
        }
        return Tuple.makeTuple(discoveredIndices, indices);
    }

}