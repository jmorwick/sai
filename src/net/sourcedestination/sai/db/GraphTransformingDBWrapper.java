package net.sourcedestination.sai.db;

import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.GraphTransformation;
import net.sourcedestination.sai.db.graph.GraphWrapper;

public class GraphTransformingDBWrapper<G extends Graph> extends DBWrapper {

    public static <G extends Graph> GraphTransformingDBWrapper<G> wrap(DBInterface db, GraphTransformation<G> transformation) {
        return new GraphTransformingDBWrapper(db, transformation);
    }

    public static GraphTransformingDBWrapper<GraphWrapper<? extends Graph>>
        maskFeature(DBInterface db, String featureName) {
            return new GraphTransformingDBWrapper<GraphWrapper<? extends Graph>>(
                db,
                g -> GraphWrapper.maskFeature(g, featureName)
            );
    }

    private final GraphTransformation<G> transformation;

    public GraphTransformingDBWrapper(DBInterface wrappedDB, GraphTransformation<G> transformation) {
        super(wrappedDB);
        this.transformation = transformation;
    }

    @Override
    public G retrieveGraph(int id) {
        return transformation.apply(super.retrieveGraph(id));
    }

    @Override
    public String toString() {
        return super.toString() + "-transforms-"+transformation;
    }
}
