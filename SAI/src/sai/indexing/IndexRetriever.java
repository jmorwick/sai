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

package org.dataandsearch.sai.indexing;

import info.kendallmorwick.util.Set;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Graph;

/**
 * Implementations of this class are intended to locate potential indices for 
 * a graph which is not necessarily stored in the database.  This class is not
 * intended to retrieve associated indices for stored graphs -- this is hanlded
 * by DBInterface.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public abstract class IndexRetriever {
    private final DBInterface db;

    public IndexRetriever(DBInterface db) {
        this.db = db;
    }

    public DBInterface getDB() { return db; }

    public abstract Set<Index> retrieveIndices(Graph g);
}