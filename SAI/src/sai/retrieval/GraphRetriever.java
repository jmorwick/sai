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

import java.util.Comparator;
import java.util.stream.Stream;

import com.google.common.base.Function;

import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;

/**
 * Implementations of this class are tasked with generating (or retrieving)
 * indices for a stored graph.
 *
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
public interface GraphRetriever<DB extends DBInterface> {

    public Stream<Integer> retrieve(DB db, Graph q);


	public static <DB extends DBInterface> GraphRetriever<DB> createPhase1Retriever(
			GraphRetriever<DB> indexRetriever,
			GraphIndexBasedRetriever ibRetriever
			) {
		return (db, q) -> ibRetriever.retrieve(db, indexRetriever.retrieve(db, q));
	}
	
	public static <DB extends DBInterface>  GraphRetriever<DB> createPhase1Retriever(
			final Function<Graph,Stream<Feature>> indexGenerator,
			final FeatureIndexBasedRetriever ibRetriever
			) {
		return (db, q) -> ibRetriever.retrieve(db, indexGenerator.apply(q));
	}
	
	public static <G extends Graph, DB extends DBInterface> Stream<G> twoPhasedRetrieval(
			final GraphRetriever<DB> phase1,
			DB db, 
    		GraphFactory<G> graphFactory,
    		final Comparator<G> ordering,
    		G query,
    		final int window1,
    		final int window2) {
				return phase1.retrieve(db, query) // TODO: should phase 1 be aware of window1?
				  .limit(window1)
				  .map(graphID -> db.retrieveGraph(graphID, graphFactory))
				  .sorted(ordering) // TODO: sorting should definitely be aware of window2
				  .limit(window2);
			
	}
}