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

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ConcurrentHashMultiset;

import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graphs;
import sai.graph.MutableGraph;

/** This class is used to provide custom algorithms for ordering and retrieving
 * graphs from the database in accordance with a set of Indices.  The algorithm
 * will select graphs as a function of which of the indicated indices are
 * associated with each graph.
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
public abstract interface GraphIndexBasedRetriever {
    public abstract Stream<Integer> retrieve(DBInterface db, Stream<Integer> indices);
    

    /**
     * A retriever which ranks graphs by the number of specified indices they are
     * related with.
     */
    public static Stream<Integer> retrieveByBasicGraphIndexCount(
    		DBInterface db, Stream<Integer> indices) {
    	return indices.map(index -> // retrieve the index graphs with specified id's
    		db.retrieveGraph(index, MutableGraph::new).getFeatures().stream().
    		// find the features indicating what graphs they index
    		filter(f -> f.getName().equals(Graphs.INDEXES_FEATURE_NAME))
    		// get the id's of the graphs they index out of the features
    		//TODO: figure out why I need a cast below
    		.map((Function<Feature,Integer>) f -> Integer.parseInt(f.getValue())))
    	.reduce(Stream::concat).get() //concatenate streams of graph id's together
        // combine allgraph id's in to a multiset
        .collect(Collectors.toCollection(ConcurrentHashMultiset::create))
        .entrySet().stream() // stream this multiset
		//sort by multiplicity
		.sorted((l,r) -> l.getCount() > r.getCount() ? l.getElement() : r.getElement())
		// convert from multiset entries to graph id's
		.map(e -> e.getElement());
    	
    }
}