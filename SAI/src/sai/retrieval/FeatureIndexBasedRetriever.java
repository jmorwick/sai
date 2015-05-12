package sai.retrieval;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ConcurrentHashMultiset;

import sai.db.DBInterface;
import sai.graph.Feature;

@FunctionalInterface
public interface FeatureIndexBasedRetriever {
	
    public abstract Stream<Integer> retrieve(DBInterface db, Stream<Feature> indices);
    
    /**
     * A retriever which ranks graphs by the number of specified indices they are
     * related with.
     */
    public static Stream<Integer> retreiveByBasicFeatureIndexCount(
    		final DBInterface db, Stream<Feature> indices) {
        return indices
        		// retrieve graphs with each index
        		.map(index -> db.retrieveGraphsWithFeature(index))
        		// combine all streams of graphs together
                .reduce(Stream::concat).get()
                // combine all graph id's in to a multiset
                .collect(Collectors.toCollection(ConcurrentHashMultiset::create))
                .entrySet().stream() // stream this multiset
        		//sort by multiplicity
        		.sorted((l,r) -> l.getCount() > r.getCount() ? l.getElement() : r.getElement())
        		// convert from multiset entries to graph id's
        		.map(e -> e.getElement()); 
    }
}
