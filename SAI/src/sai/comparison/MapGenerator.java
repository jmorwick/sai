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

import info.kendall_morwick.funcles.T2;
import sai.Graph;
import sai.Node;
import com.google.common.base.Function;

/**
 * A shortcut type for a function generating mappings between two graphs
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public abstract interface MapGenerator extends 
		Function<T2<Graph,Graph>,Map<Node,Node>> {
}