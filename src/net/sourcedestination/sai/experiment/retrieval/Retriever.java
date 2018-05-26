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

package net.sourcedestination.sai.experiment.retrieval;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.indexing.GraphIndexGenerator;
import net.sourcedestination.sai.util.Task;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static net.sourcedestination.funcles.tuple.Tuple.makeTuple;

/**
 * Implementations of this class are tasked with generating (or retrieving)
 * indices for a stored graph.
 *
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
@FunctionalInterface
public interface Retriever<Q> {

	static Logger logger = LogManager.getLogger(Retriever.class);

    Stream<Integer> retrieve(Q q);

    default int size() {
        return -1;
    }

    static Retriever<Integer> hideGraphBeforeRetrieval(GraphHidingDB db, Retriever<Graph> r) {
        return (id) -> {
            Graph q = db.retrieveGraph(id);
            db.hideGraph(id);
            Stream<Integer> results = r.retrieve(q);
            db.unhideGraph(id);
            return results;
        };
    }

    static Stream<Integer> rerank(Stream<Integer> graphIds, DBInterface db, ResultQualityMetric q) {
        return graphIds
                .map(id -> {
                    double s = q.apply(db.retrieveGraph(id));
                    logger.info("Considered Graph ID #"+id+" has similarity " + s + " to query");
                    return makeTuple(id, s);
                })
                .sorted(Comparator.comparing(Tuple2::_2))
                .map(Tuple2::_1);
    }

    static <Q> Stream<Integer> retrieveByIndexCount(Retriever<Q> index, Stream<Q> indexValues) {
        Multiset<Integer> relatedGraphs = indexValues.map(i -> index.retrieve(i)) // transform indexes in to streams of related graph ids
                .reduce(Stream::concat).get() //concatenate streams of graph ids together
                // combine all graph id's in to a multiset
                .collect(Collectors.toCollection(ConcurrentHashMultiset::create));

        double maxCount = relatedGraphs.stream().mapToInt(relatedGraphs::count).max().getAsInt();
        return relatedGraphs.entrySet().stream() // stream this multiset
                //log the similarity to the query
                .map(e -> {
                    logger.info("Considered Graph ID #"+e.getElement()+" has similarity " +
                            (e.getCount() / maxCount) + " to query");
                    return e;
                })
                //sort by multiplicity (negated for descending order)
                .sorted((l,r) -> -Integer.compare(l.getCount(), r.getCount()))
                // convert from multiset entries to graph id's
                .map(Multiset.Entry::getElement);
    }

    static <Q> Stream<Integer> retrieveByIndexCount(Retriever<Q> index, GraphIndexGenerator<Q> gen, Graph query) {
        return retrieveByIndexCount(index, gen.apply(query));
    }

    static <Q> Retriever<Graph> indexCountRetrieverFactory(Retriever<Q> index, GraphIndexGenerator<Q> gen) {
        return query -> retrieveByIndexCount(index, gen, query);
    }

    static Retriever simpleSequentialRetrieverFactory(DBInterface db) {
        return new Retriever() {
            @Override
            public Stream<Integer> retrieve(Object query) {
                return db.getGraphIDStream().map(gid -> {
                    logger.info("retrieved graph #"+gid+" from " + db.toString());
                    return gid;
                });
            }
        };
    }

    static <Q> Task retrievalExperiment(Retriever<Q> r, QueryGenerator<Q> gen) {
        return new Task() {

            final AtomicInteger progress = new AtomicInteger(0);

            @Override
            public Object get() {
                gen.get().forEach( q -> {
                    logger.info("Issueing query #"+q.hashCode());
                    gen.getExpectedResults(q).forEach(id -> {
                        logger.info("Expecting graph #" + id + " for query #"+q.hashCode());
                    });
                    r.retrieve(q).forEach(id -> {
                        logger.info("retrieved graph #"+id+" for query #"+q.hashCode());
                    });
                    progress.incrementAndGet();
                });
                return null;
            }

            @Override
            public int getProgressUnits() {
                return progress.get();
            }

            @Override
            public int getTotalProgressUnits() {
                return r.size();
            }
        };
    }
}