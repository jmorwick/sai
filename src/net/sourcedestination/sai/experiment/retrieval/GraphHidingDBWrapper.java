package net.sourcedestination.sai.experiment.retrieval;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.db.graph.Graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by jmorwick on 6/7/17.
 */
public class GraphHidingDBWrapper extends DBWrapper {

    private final Set<Integer> hiddenGraphs;

    public GraphHidingDBWrapper(DBInterface db) {
        super(db);
        hiddenGraphs = Sets.newHashSet();
    }

    @Override
    public Graph retrieveGraph(int graphID) {
        if(hiddenGraphs.contains(graphID))
            return null;

        return getWrappedDB().retrieveGraph(graphID);
    }

    @Override
    public Stream<Integer> getGraphIDStream() {
        return getWrappedDB().getGraphIDStream()
                .filter(id -> !hiddenGraphs.contains(id));
    }


    public Set<Integer> getHiddenGraphs() {
        return new HashSet<>(hiddenGraphs);
    }

    public void hideGraph(int graphID) {
        hiddenGraphs.add(graphID);
    }

    public void unhideGraph(int graphID) {
        hiddenGraphs.remove(graphID);
    }
}
