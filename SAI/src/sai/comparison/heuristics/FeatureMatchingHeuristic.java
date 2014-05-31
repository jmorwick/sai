package sai.comparison.heuristics;

import info.kendall_morwick.funcles.Pair;
import sai.graph.Feature;

import com.google.common.base.Function;

public interface FeatureMatchingHeuristic extends Function<Pair<Feature>,Double> {

}
