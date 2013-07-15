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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import sai.DBInterface;
import sai.Graph;
import sai.indexing.Index;
import sai.retrieval.GraphRetriever;

/**
 * A retriever which ranks graphs by the number of specified indices they are
 * related with.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class SimpleCountRetriever extends GraphRetriever {

    private Set<Integer> lastConsideredGraphIDs = new HashSet<Integer>();
    private Set<Integer> retrievedGraphIDs = new HashSet<Integer>();

    public SimpleCountRetriever(DBInterface db) {
        super(db);
    }

    public Iterator<Graph> retrieve(Set<Index> indices) {
        final Multiset<Integer> ranks = HashMultiset.create();
        for (Index i : indices) {
            for (Integer gid : i.getIndexedGraphIDs()) {
                ranks.add(gid);
            }
        }
        
        retrievedGraphIDs = new HashSet<Integer>();
        lastConsideredGraphIDs = ranks.elementSet();

        for (Integer id : getDB().getIgnoredIDs()) {
            ranks.remove(id);
        }

        return new Iterator<Graph>() {

            public boolean hasNext() {
                return ranks.size() > 0;
            }

            public Graph next() {
                if(!hasNext()) throw new IllegalStateException("Cannot retrieve next id -- there aren't any left");
                int gid = Multisets.copyHighestCountFirst(ranks).iterator().next();
                ranks.remove(gid);
                retrievedGraphIDs.add(gid);
                return getDB().loadStructureFromDatabase(gid);
            }

            public void remove() {
            }
        };
    }
    
}