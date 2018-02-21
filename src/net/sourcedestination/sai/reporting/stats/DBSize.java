package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;

/* A DB metric that can be used to
 compute the size of a given database of graphs.
 Created by amorehead on 2/21/18. */
public class DBSize extends FastDBMetric {
    public double computeStat(DBInterface db) {
        return (double) db.getDatabaseSize();
    }
}