package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.sai.db.graph.Graph;

import java.util.function.Function;

public interface ClassificationModel extends Function<Graph, String> {
}
