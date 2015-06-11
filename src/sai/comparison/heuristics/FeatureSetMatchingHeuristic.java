package sai.comparison.heuristics;

import info.kendall_morwick.funcles.tuple.Tuple3;

import java.util.Map;
import java.util.Set;

import sai.graph.Feature;

import com.google.common.base.Function;

@FunctionalInterface
public interface FeatureSetMatchingHeuristic extends 
	Function<Tuple3<Set<Feature>,Set<Feature>,Map<Feature,Feature>>,Double> {

}
