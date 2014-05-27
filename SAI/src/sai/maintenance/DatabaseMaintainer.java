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

import java.util.HashMap;
import java.util.Map;

import sai.db.DBInterface;

import com.google.common.base.Supplier;

/**
 * implementations of this class are intended to be run during idle time to 
 * maintain aspects of the database, such as index to graph relationships and 
 * removing duplicate indices.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class DatabaseMaintainer implements Runnable {
    private Map<Supplier<MaintenanceTask>, MaintenanceTask> tasks =
            new HashMap<Supplier<MaintenanceTask>, MaintenanceTask> ();
    private DBInterface db;

    public DatabaseMaintainer(DBInterface db, Supplier<MaintenanceTask> ... factories) {
        for(Supplier<MaintenanceTask> f : factories)
            tasks.put(f, f.get());
    }

    public void nextIteration() {
        for(Supplier<MaintenanceTask> f : tasks.keySet()) {
            MaintenanceTask t = tasks.get(f);
            if(!t.isDone()) {
                t.nextIteration();
            }
        }
    }

    public boolean isDone() {
        for(Supplier<MaintenanceTask> f : tasks.keySet()) {
            MaintenanceTask t = tasks.get(f);
            if(!t.isDone()) {
                return false;
            }
        }
        return true;
    }

    public void run() {
        
    }


}