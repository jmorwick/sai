package net.sourcedestination.sai.reporting;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;

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
    public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f) {
        if(hiddenGraphs.contains(graphID))
            return null;

        return getWrappedDB().retrieveGraph(graphID, f);
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
