package net.sourcedestination.sai.db.graph;

import java.util.function.Function;

@FunctionalInterface
public interface GraphTransformation<G extends Graph> extends Function<Graph,G> {
}
