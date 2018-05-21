package net.sourcedestination.sai.experiment.retrieval;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Multiset;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Feature;

import com.google.common.collect.ConcurrentHashMultiset;
import net.sourcedestination.sai.db.indexing.Index;

@FunctionalInterface
public interface FeatureIndexBasedRetriever {

    public Stream<Integer> retrieve(DBInterface db, Stream<Feature> indices);
    
    /**
     * A retriever which ranks graphs by the number of specified indices they are
     * related with.
     */
    public static Stream<Integer> retreiveByBasicFeatureIndexCount(
			final Index<Feature> retrieveGraphsWithFeature,
			Stream<Feature> indices) {

        Multiset<Integer> retrievedGraphIds = indices
        		// retrieve graphs with each index
        		.map(retrieveGraphsWithFeature::getRelatedGraphIds)
        		// combine all streams of graphs together
                .reduce(Stream::concat).get()
                // combine all graph id's in to a multiset
                .collect(Collectors.toCollection(ConcurrentHashMultiset::create));

        double max = retrievedGraphIds.entrySet().stream()
				.mapToInt(entry -> entry.getCount())
				.max().getAsInt();

        return retrievedGraphIds.entrySet().stream() // stream this multiset
				// log similarities
				.map(entry -> {
					GraphRetriever.logger.info("considered graph #"+entry.getElement() + " has " +
							(entry.getCount() / max) + " similarity to query");
					return entry;
				})
        		// sort by multiplicity
				.sorted((l,r) -> -Integer.compare(l.getCount(), r.getCount()))
				// convert from multiset entries to graph id's
				.map(Multiset.Entry::getElement);
    }
}
