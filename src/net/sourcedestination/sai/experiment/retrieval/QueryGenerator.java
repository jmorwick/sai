package net.sourcedestination.sai.experiment.retrieval;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;

import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface QueryGenerator<Q> extends Supplier<Stream<Q>> {
    @Override
    Stream<Q> get();

    default Stream<Integer> getExpectedResults(Q query) {
        return Stream.empty();
    }

    default int size() {
        return -1;
    }

    public static <Q> QueryGenerator<Q> of(Q ... queries) {
        return () -> Stream.of(queries);
    }


    public static  QueryGenerator<Graph> graphsFrom(DBInterface db) {
        Stream<Integer> ids = db.getGraphIDStream();
        return () -> ids.map(id -> {
            return db.retrieveGraph(id);
        });
    }

    public static QueryGenerator<Integer> idsFrom(DBInterface db) {
        return () -> db.getGraphIDStream();
    }
}
