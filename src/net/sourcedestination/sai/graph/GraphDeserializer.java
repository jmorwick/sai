package net.sourcedestination.sai.graph;

import java.util.function.Function;

/**
 * Created by jmorwick on 6/18/17.
 */
public interface GraphDeserializer<G extends Graph> extends Function<String,G> {
}
