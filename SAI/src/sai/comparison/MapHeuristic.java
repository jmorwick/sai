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

package sai.comparison;


import java.util.Map;

import sai.comparison.mapgenerators.search.GraphMapping;
import sai.graph.Graph;
import sai.graph.Node;
import info.kendall_morwick.funcles.T2;
import info.kendall_morwick.funcles.T3;
import info.kendall_morwick.funcles.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * This class houses a method for judging the utility of a mapping between
 * two graphs.  These are mainly used for ranking retrieval candidates.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public interface MapHeuristic extends Function<T3<Graph,Graph,GraphMapping>,Double>{

}