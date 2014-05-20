/* Copyright 2011 Joseph Kendall-Morwick

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

package sai.indexing;

import java.util.Set;

import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Index;

/**
 * Implementations of this class are tasked with generating (or retrieving)
 * indices for a stored graph.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public interface IndexGenerator<G extends Graph> {

    public Set<Index> generateIndices(DBInterface db, GraphFactory<G> gf, Graph s);

}