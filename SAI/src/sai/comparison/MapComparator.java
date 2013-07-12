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
import sai.Node;
import sai.Graph;

/**
 * TODO: eliminate and replace this class, or redesign it
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated public abstract class MapComparator {
    private final DBInterface db;
    private final Graph s1;
    private final Graph s2;
    private final Map<Node, Node> m1;
    private final Map<Node, Node> m2;
    private final long time;
    private final long startTime;
    private Function<Judgement, T5<DBInterface, Graph, Graph, Map<Node, Node>, Map<Node, Node>>> f=null;
    private ResultContainer<Judgement> rc = null;

    public enum Judgement { FIRST_IS_BETTER, SECOND_IS_BETTER, EQUIVALENT, UNKNOWN };

    private MapComparator(DBInterface db, Graph s1, Graph s2, Map<Node,Node> m1, Map<Node,Node> m2) {
        this(db, s1, s2, m1, m2, -1);
    }

    public MapComparator(DBInterface db, Graph s1, Graph s2, Map<Node,Node> m1, Map<Node,Node> m2,
            Function<Judgement, T5<DBInterface, Graph, Graph, Map<Node,Node>, Map<Node,Node>>> f) {
        this(db, s1, s2, m1, m2, -1, f);
    }

    public MapComparator(DBInterface db, Graph s1, Graph s2, Map<Node,Node> m1, Map<Node,Node> m2, long time,
            Function<Judgement, T5<DBInterface, Graph, Graph, Map<Node,Node>, Map<Node,Node>>> f) {
        this(db, s1, s2, m1, m2, time);
        this.f = f;
        this.rc = f.r(makeTuple(db, s1, s2, m1, m2), this);
        if(time != -1)
            this.rc.suggestProcessingTime(time);
    }

    private MapComparator(DBInterface db, Graph s1, Graph s2, Map<Node,Node> m1, Map<Node,Node> m2, long time) {
        this.db = db;
        this.s1 = s1;
        this.s2 = s2;
        this.m1 = m1;
        this.m2 = m2;
        this.time = time;
        this.startTime = System.currentTimeMillis();
    }

    public final Graph getFromGraph() { return s1; }
    public final Graph getToGraph() { return s2; }
    public final DBInterface getDB() { return db; }
    public final Map<Node,Node> getMap1() { return m1; }
    public final Map<Node,Node> getMap2() { return m2; }
    public final long getRemainingTime() {
        if(time == -1) return -1;
        if(System.currentTimeMillis() - startTime > time) return 0;
        return time - System.currentTimeMillis() + startTime;
    }

    public final boolean done() {
        return getJudgement() != null;
    }

    public final boolean judged1Better() {
        return getJudgement() == Judgement.FIRST_IS_BETTER;
    }

    public final  boolean judged2Better() {
        return getJudgement() == Judgement.SECOND_IS_BETTER;
    }

    public final boolean judgedEquivalent() {
        return getJudgement() == Judgement.EQUIVALENT;
    }


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
        if(f != null) {
            if(rc.done()) {
                if(rc.getResult() == null)
                    return Judgement.UNKNOWN;
                return rc.getResult();
            }
        }

        return null;

    }
}