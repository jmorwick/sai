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
package sai.retrieval;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.MutableGraph;
import sai.retrieval.IndexBasedGraphRetriever;

/**
 * A retriever which ranks graphs by the number of specified indices they are
 * related with.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class BasicCountRetriever implements IndexBasedGraphRetriever {

    private Set<Integer> retrievedGraphIDs = Sets.newHashSet();

    public Iterator<Integer> retrieve(final DBInterface db, Set<Integer> indices) {
        final Multiset<Integer> ranks = HashMultiset.create();
        final GraphFactory<MutableGraph> gf = MutableGraph.getFactory();
        for (int iid : indices) {
            for (Feature f : SAIUtil.retainOnly(
            		db.retrieveGraph(iid, gf).getFeatures(),
            		"indexes")) {
            	ranks.add(Integer.parseInt(f.getValue()));
            }
        }
        
        retrievedGraphIDs = Sets.newHashSet();

        for (Integer id : db.getHiddenGraphs()) {
            ranks.remove(id);
        }
        return new Iterator<Integer>() {

            public boolean hasNext() {
                return ranks.size() > 0;
            }

            public Integer next() {
                if(!hasNext()) throw new IllegalStateException("Cannot retrieve next id -- there aren't any left");
                int gid = Multisets.copyHighestCountFirst(ranks).iterator().next();
                ranks.remove(gid, ranks.count(gid));
                retrievedGraphIDs.add(gid);
                return gid;
            }

            public void remove() {
            }
        };
    }
}