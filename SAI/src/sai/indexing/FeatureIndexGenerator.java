package sai.indexing;

import java.util.Set;

import sai.graph.Feature;
import sai.graph.Graph;

import com.google.common.base.Function;

@FunctionalInterface
public interface FeatureIndexGenerator extends Function<Graph,Set<Feature>> {

}
