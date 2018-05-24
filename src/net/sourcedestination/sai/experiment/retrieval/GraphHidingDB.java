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
public interface GraphHidingDB extends DBInterface {

    void hideGraph(int graphId);

    void unhideGraph(int graphId);

    static GraphHidingDB wrap(DBInterface db, String name) {
        return new GraphHidingDBWrapper(db, name);
    }

}


class GraphHidingDBWrapper extends DBWrapper implements GraphHidingDB {

    private final Set<Integer> hiddenGraphs;
    private final String name;

    public GraphHidingDBWrapper(DBInterface db, String name) {
        super(db);
        hiddenGraphs = Sets.newHashSet();
        this.name = name;
    }

    @Override
    public Graph retrieveGraph(int graphID) {
        if (hiddenGraphs.contains(graphID))
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

    @Override
    public void hideGraph(int graphID) {
        hiddenGraphs.add(graphID);
    }

    @Override
    public void unhideGraph(int graphID) {
        hiddenGraphs.remove(graphID);
    }
}