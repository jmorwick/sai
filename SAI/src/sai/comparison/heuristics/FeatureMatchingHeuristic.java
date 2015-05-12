package sai.comparison.heuristics;

import java.util.function.Function;

import info.kendall_morwick.funcles.tuple.Pair;
import sai.graph.Feature;


public interface FeatureMatchingHeuristic extends Function<Pair<Feature>,Double> {

}
