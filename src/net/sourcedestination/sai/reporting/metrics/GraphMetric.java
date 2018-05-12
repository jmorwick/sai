package net.sourcedestination.sai.reporting.metrics;

import net.sourcedestination.sai.graph.Graph;

import java.util.function.Function;

/**
 * A metric for individual graphs
 *
 * Created by amorehead on 2/7/18.
 * Modified by jbmorwick
 */

@FunctionalInterface
public interface GraphMetric extends Function<Graph, Double> {

}
