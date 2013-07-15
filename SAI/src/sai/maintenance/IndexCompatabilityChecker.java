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

import info.km.funcles.Funcles;
import info.km.funcles.T2;
import info.km.funcles.Tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

import sai.DBInterface;
import sai.Graph;
import sai.comparison.SubgraphComparator;
import sai.indexing.Index;

/**
 * Updates compatibility relationships between indices to assist in hierarchical 
 * index retrieval and index consolidation.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick
 */
public class IndexCompatabilityChecker extends MaintenanceTask {

    private List<Function<T2<Graph,Graph>,SubgraphComparator>> comparatorFactories =
            new ArrayList<Function<T2<Graph,Graph>,SubgraphComparator>>();
    private DBInterface db;
    private final long timeIncrement;
    private Index currentSubgraphCandidate;
    private Index currentSupergraphCandidate;
    private long elapsedTime = 0;
    boolean done = false;
    boolean complete = true;
    private Iterator<Index> progress1;
    private Iterator<Index> progress2;
    private List<SubgraphComparator> activeComparators = new ArrayList<SubgraphComparator>();
    private final long maxTime;

    public IndexCompatabilityChecker(DBInterface db, long maxTime,
            long timeIncrement, Function<T2<Graph,Graph>,SubgraphComparator> ... comparatorFactories) {
        this.timeIncrement = timeIncrement;
        this.maxTime = maxTime;
        this.db = db;
      
        if(comparatorFactories.length == 0) throw new IllegalArgumentException("Need at least one comparator");
        for(Function<T2<Graph,Graph>,SubgraphComparator> f : comparatorFactories) 
        	this.comparatorFactories.add(f);
        progress1 = db.getIndexIterator();
        progress2 = db.getIndexIterator();
        currentSubgraphCandidate = progress1.next();
        currentSupergraphCandidate = progress2.next();
        if(currentSubgraphCandidate == null) done = true;
        else {
            nextPair();
            if(!done) {
                for(Function<T2<Graph,Graph>,SubgraphComparator> f : comparatorFactories)
                    activeComparators.add(f.apply(Tuple.makeTuple((Graph)currentSubgraphCandidate, (Graph)currentSupergraphCandidate)));
            }
        }
        
    }

    private void nextPair() {
        activeComparators.clear();
        if(progress2.hasNext()) {
            elapsedTime = 0;
            currentSupergraphCandidate = progress2.next();
        } else if(progress1.hasNext()) {
            if(complete && !currentSubgraphCandidate.checkedForSubgraphRelationships()) {
                db.updateDB("UPDATE graph_instances SET checked = TRUE WHERE id = " + currentSubgraphCandidate.getID());
            }
            elapsedTime = 0;
            complete = true;
            progress2 = db.getIndexIterator();
            currentSubgraphCandidate = progress1.next();
            currentSupergraphCandidate = progress2.next();
        } else {
            if(complete) {
                db.updateDB("UPDATE graph_instances SET checked = TRUE WHERE id = " + currentSubgraphCandidate.getID());
            }
            done = true;
            return;
        }
        for(Function<T2<Graph,Graph>,SubgraphComparator> f : comparatorFactories)
            activeComparators.add(Funcles.apply(f, (Graph)currentSubgraphCandidate, (Graph)currentSupergraphCandidate));
        if(currentSubgraphCandidate.getID() == currentSupergraphCandidate.getID()) nextPair();
    }

    public void nextIteration() {
        while(!isDone() &&
              currentSubgraphCandidate.checkedForSubgraphRelationships() &&
              currentSupergraphCandidate.checkedForSubgraphRelationships() 
              )
            nextPair();


        for(SubgraphComparator c : Sets.newHashSet(activeComparators)) {
            c.waitTillDone(timeIncrement);
            elapsedTime += timeIncrement;
            if(elapsedTime > maxTime) {
                complete = false;
                c.kill();
                nextPair();
            }
            if(c.done()) {
                if(c.isSubgraph()) {
                    db.addIndex(currentSupergraphCandidate, currentSubgraphCandidate);
                }
                nextPair();
            }
        }
    }

    public boolean isDone() {
        return done;
    }
}