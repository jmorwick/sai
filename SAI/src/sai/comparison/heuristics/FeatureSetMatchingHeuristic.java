package sai.comparison.heuristics;

import info.kendall_morwick.funcles.T3;

import java.util.Map;
import java.util.Set;

import sai.graph.Feature;

import com.google.common.base.Function;

public interface FeatureSetMatchingHeuristic extends 
	Function<T3<Set<Feature>,Set<Feature>,Map<Feature,Feature>>,Double> {

}
