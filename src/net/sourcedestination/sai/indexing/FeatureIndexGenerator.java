package net.sourcedestination.sai.indexing;

import java.util.Set;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

import com.google.common.base.Function;

@FunctionalInterface
public interface FeatureIndexGenerator extends Function<Graph,Set<Feature>> {

}
