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

import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.Set;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Graph;

/**
 * A graph index
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class Index extends Graph {

    private DBInterface db;

  public Index(DBInterface db) {
    super(db);
    this.db= db;
  }

    public Index(DBInterface db, Integer id) {
        super(db, id);
        this.db= db;
    }

    public boolean checkedForSubgraphRelationships() {
        return db.getQueryResults("SELECT * FROM graph_instances WHERE id="+getID()+" AND checked = TRUE").size() > 0;
    }

    public Set<Integer> getIndexedGraphIDs() {
        Set<Integer> ids = new Set<Integer>();
        for(Map<String,String> row : db.getQueryResults("SELECT * FROM graph_instances, graph_indices WHERE index_id = " + getID() + " AND graph_id = id AND IS_INDEX = FALSE")) {
            ids.add(Integer.parseInt(row.get("id")));
        }
        return ids;
    }

    public Set<Integer> getSuperIndexIDs() {
        Set<Integer> ids = new Set<Integer>();
        for(Map<String,String> row : db.getQueryResults("SELECT * FROM graph_instances, graph_indices WHERE index_id = " + getID() + " AND graph_id = id AND IS_INDEX = TRUE")) {
            ids.add(Integer.parseInt(row.get("id")));
        }
        return ids;
    }

}