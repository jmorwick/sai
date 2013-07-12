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

import java.util.logging.Level;
import java.util.logging.Logger;
import sai.DBInterface;
import sai.Graph;

/**
 * This class may not be retained in the final version 2.0, but I am still
 * considering its inclusion.
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public abstract class SubgraphComparator {
    private Graph g1;
    private Graph g2;
    private boolean subgraph = false;
    private final DBInterface db;
    private long time;
    private long startTime;
    private Function<Judgement, SubgraphComparator> f;
    private ResultContainer<Judgement> rc;

    public enum Judgement { IS_SUBGRAPH, IS_NOT_SUBGRAPH, UNKNOWN };

    public SubgraphComparator(DBInterface db,
            Graph potentialSubgraph,
            Graph potentialSupergraph) {
        this(db, potentialSubgraph, potentialSupergraph, -1);
    }

    public SubgraphComparator(DBInterface db,
            Graph potentialSubgraph,
            Graph potentialSupergraph,
            long time) {
        this.g1 = potentialSubgraph;
        this.g2 = potentialSupergraph;
        this.db = db;
        this.startTime = System.currentTimeMillis();
        this.time = time;
    }
    public SubgraphComparator(DBInterface db,
            Graph potentialSubgraph,
            Graph potentialSupergraph,
            long time,
            Function<Judgement, SubgraphComparator> f) {
        this(db, potentialSubgraph, potentialSupergraph);
        this.f = f;
        this.rc = f.r(this, this);
        if(time != -1)
            this.rc.suggestProcessingTime(time);

    }
    public SubgraphComparator(DBInterface db,
            Graph potentialSubgraph,
            Graph potentialSupergraph,
            Function<Judgement, SubgraphComparator> f) {
        this(db, potentialSubgraph, potentialSupergraph, -1, f);
    }

    public final DBInterface getDB() { return db; }
    public final Graph getPotentialSubgraph() { return g1; }
    public final Graph getPotentialSupergraph() { return g2; }


    public final boolean done() { return getJudgement() != null; }
    public final boolean isSubgraph() { return getJudgement() == Judgement.IS_SUBGRAPH; }
    public final boolean isNotSubgraph() { return getJudgement() == Judgement.IS_NOT_SUBGRAPH; }


    public final synchronized void waitTillDone() {
        waitTillDone(-1);
    }

    public final synchronized void waitTillDone(long maxComparisonTime) {
        if(done()) return;

        try {
            if(maxComparisonTime >= 0) wait(maxComparisonTime);
            else wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(SubgraphComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void kill() {
        if(rc != null) rc.kill();
    }

    public Judgement getJudgement() {
        if(rc != null) {
            if(rc.done()) {
                if(rc.getResult() == null)
                    return Judgement.UNKNOWN;
                return rc.getResult();
            }
        }
        return null;
    }

    
}