package net.sourcedestination.sai.db;

import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.GraphFactory;

public class GraphTransformingDBWrapper<G extends Graph> extends DBWrapper {

    public static <G extends Graph> GraphTransformingDBWrapper<G> wrap(DBInterface db, GraphFactory<G> transformation) {
        return new GraphTransformingDBWrapper(db, transformation);
    }

    private final GraphFactory<G> transformation;

    public GraphTransformingDBWrapper(DBInterface wrappedDB, GraphFactory<G> transformation) {
        super(wrappedDB);
        this.transformation = transformation;
    }

    @Override
    public G retrieveGraph(int id) {
        return transformation.copy(super.retrieveGraph(id));
    }

    @Override
    public String toString() {
        return super.toString() + "-transforms-"+transformation;
    }
}
