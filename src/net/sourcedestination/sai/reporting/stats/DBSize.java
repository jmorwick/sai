package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;


public class DBSize extends FastDBStatistic {
    public double computeStat(DBInterface db) {
        return (double) db.getDatabaseSize();
    }
}