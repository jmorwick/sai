package net.sourcedestination.sai.experiment.retrieval;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

@FunctionalInterface
public interface QueryGenerator<Q> extends Supplier<Stream<Q>> {

    static Logger logger = LogManager.getLogger(Retriever.class);

    @Override
    Stream<Q> get();

    default Stream<Integer> getExpectedResults(Q query) {
        return Stream.empty();
    }

    default int size() {
        return -1;
    }

    public static <Q> QueryGenerator<Q> of(Q ... queries) {
        logger.info("creating a query generator holding queries: " + Arrays.toString(queries));
        return () -> {
            logger.info("generating queries from supplied list of queries");
            return Stream.of(queries);
        };
    }


    public static  QueryGenerator<Graph> graphsFrom(DBInterface db) {
        logger.info("creating query generator with graph queries from " + db);
        Stream<Integer> ids = db.getGraphIDStream();
        return () -> {
            logger.info("generating queries from all graphs in " + db);
            return ids.map(id -> db.retrieveGraph(id));

        };
    }

    public static QueryGenerator<Integer> idsFrom(DBInterface db) {
        logger.info("creating query generator with graph id queries from " + db);
        return () -> {
            logger.info("generating queries from all graph ids in " + db);
            return db.getGraphIDStream();
        };
    }
}
