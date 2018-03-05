package net.sourcedestination.sai.learning;

import net.sourcedestination.sai.graph.Graph;

import java.util.function.Function;

public interface ClassificationModel extends Function<Graph, String> {
}
