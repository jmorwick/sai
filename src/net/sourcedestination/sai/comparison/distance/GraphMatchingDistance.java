/* Copyright 2011-2013 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    SAI is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SAI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */

package net.sourcedestination.sai.comparison.distance;

import net.sourcedestination.sai.comparison.matching.GraphMatching;

import com.google.common.base.Function;
import net.sourcedestination.sai.comparison.matching.MatchingEvaluator;
import net.sourcedestination.sai.comparison.matching.MatchingGenerator;
import net.sourcedestination.sai.graph.Graph;

/**
 * A graph distance metric which works with two components for generating and judging
 * matchings between two graphs.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class GraphMatchingDistance<M extends GraphMatching, G extends Graph>
		implements GraphDistance<G> {

	private final MatchingGenerator<? extends M, ? super G> gen;
	private final MatchingEvaluator<M> eval;

	public GraphMatchingDistance(MatchingGenerator<? extends M, ? super G> gen,
								 MatchingEvaluator<M> eval) {
		this.gen = gen;
		this.eval = eval;
	}

	@Override
	public Double apply(G g1, G g2) {
		return eval.evaluateMatching(gen.apply(g1, g2));
	}
}