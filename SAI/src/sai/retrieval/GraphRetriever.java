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

package org.dataandsearch.sai.retrieval;

import info.kendallmorwick.util.Set;
import java.util.Iterator;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Graph;
import org.dataandsearch.sai.indexing.Index;

/** This class is used to provide custom algorithms for ordering and retrieving
 * graphs from the database in accordance with a set of Indices.  The algorithm
 * will select graphs as a function of which of the indicated indices are
 * associated with each graph.
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public abstract class GraphRetriever {
    private final DBInterface db;

    public GraphRetriever(DBInterface db) {
        this.db = db;
    }

    public DBInterface getDB() { return db; }

    public abstract Iterator<Graph> retrieve(Set<Index> indices);
}