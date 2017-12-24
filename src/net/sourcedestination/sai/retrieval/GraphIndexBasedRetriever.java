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

package net.sourcedestination.sai.retrieval;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Multiset;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.MutableGraph;
import static net.sourcedestination.sai.graph.Graph.INDEXES_FEATURE_NAME;

import com.google.common.collect.ConcurrentHashMultiset;

/** This class is used to provide custom algorithms for ordering and retrieving
 * graphs from the database in accordance with a set of Indices.  The algorithm
 * will select graphs as a function of which of the indicated indices are
 * associated with each graph.
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
@FunctionalInterface
public interface GraphIndexBasedRetriever {
    public Stream<Integer> retrieve(DBInterface db, Stream<Integer> indices);

    /**
     * A retriever which ranks graphs by the number of specified indices they are
     * related with.
     */
    public static Stream<Integer> retrieveByBasicGraphIndexCount(
    		DBInterface db, Stream<Integer> indices) {
    	return indices.map(index -> // retrieve the index graphs with specified id's
    		db.retrieveGraph(index).getFeatures().
    		// find the features indicating what graphs they index
    		filter(f -> f.getName().equals(INDEXES_FEATURE_NAME))
    		// get the id's of the graphs they index out of the features
    		.map(f -> Integer.parseInt(f.getValue())))
    	.reduce(Stream::concat).get() //concatenate streams of graph id's together
        // combine allgraph id's in to a multiset
        .collect(Collectors.toCollection(ConcurrentHashMultiset::create))
        .entrySet().stream() // stream this multiset
		//sort by multiplicity (negated for descending order)
		.sorted((l,r) -> -Integer.compare(l.getCount(), r.getCount()))
		// convert from multiset entries to graph id's
		.map(Multiset.Entry::getElement);
    }
}