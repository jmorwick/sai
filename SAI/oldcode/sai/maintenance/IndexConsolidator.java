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

import sai.db.DBInterface;

/**
 * A maintenance task which locates equivalent indices and collapses them into 
 * a single index.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class IndexConsolidator extends MaintenanceTask {
    private Iterator<Integer> progress1;
    private Iterator<Integer> progress2;
    private int i1;

    DBInterface db;

    public IndexConsolidator(DBInterface db) {
        this.db = db;
        progress1 = db.getIndexIDIterator();
        progress2 = db.getIndexIDIterator();
        i1 = progress1.next();
    }

    public void combineIndices(int i1, int i2) {
    	for(int gid : db.retrieveIndexedGraphIDs(i1)) {
    		db.addIndex(gid, i2);
    	}
        db.deleteGraph(i1);
    }

    public boolean isDone() {
        return !progress1.hasNext() && !progress2.hasNext();
    }

    public void nextIteration() {
        if(!progress2.hasNext()) {
            if(progress1.hasNext()) {
                progress2 = db.getIndexIDIterator();
                i1 = progress1.next();
            } else {
                return;
            }
        }
        int i2 = progress2.next();
        if(i1 == i2) return;
        if(db.retrieveIndexedGraphIDs(i1).contains(i2) && 
           db.retrieveIndexedGraphIDs(i2).contains(i1))
            combineIndices(i1,i2);
    }
}