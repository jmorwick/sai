package net.sourcedestination.sai.comparison.distance;

import java.util.Map;
import java.util.Set;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.graph.Feature;

import com.google.common.base.Function;

@FunctionalInterface
public interface FeatureSetDistance extends
	Function<Tuple3<Set<Feature>,Set<Feature>,Map<Feature,Feature>>,Double> {

}
