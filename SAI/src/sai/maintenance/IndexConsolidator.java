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

package sai.maintenance;

import java.util.Iterator;
import sai.DBInterface;
import sai.indexing.Index;

/**
 * A maintenance task which locates equivalent indices and collapses them into 
 * a single index.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class IndexConsolidator extends MaintenanceTask {
    private Iterator<Index> progress1;
    private Iterator<Index> progress2;
    private Index i1;

    DBInterface db;

    public IndexConsolidator(DBInterface db) {
        this.db = db;
        progress1 = db.getIndexIterator();
        progress2 = db.getIndexIterator();
        i1 = progress1.next();
    }

    public void combineIndices(Index i1, Index i2) {
        db.updateDB("UPDATE graph_indices SET index_id = "+i1.getID()+
                " WHERE index_id = " + i2.getID());
        db.updateDB("DELETE FROM graph_indices WHERE graph_id = index_id ");
        db.removeStructureFromDatabase(i2);
    }

    public boolean isDone() {
        return !progress1.hasNext() && !progress2.hasNext();
    }

    public void nextIteration() {
        if(!progress2.hasNext()) {
            if(progress1.hasNext()) {
                progress2 = db.getIndexIterator();
                i1 = progress1.next();
            } else {
                return;
            }
        }
        Index i2 = progress2.next();
        if(i1.getID() == i2.getID()) return;
        if(db.indexedBy(i1, i2) && db.indexedBy(i2, i1))
            combineIndices(i1,i2);
    }
}