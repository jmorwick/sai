package sai.comparison.heuristics;

import info.kendall_morwick.function.Function2;
import sai.graph.Feature;

// TODO: not referenced anywhere yet -- consider eliminating?

@FunctionalInterface
public interface FeatureMatchingHeuristic extends Function2<Feature, Feature, Double> {

}
