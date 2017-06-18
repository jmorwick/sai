package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

/**  TODO: add test
 *   TODO: comment / license
 */
public class DBSize implements DBStatistic {
    public Task<Double> apply(DBInterface db) {
        return DBStatistic.returnImmediately((double)db.getDatabaseSize());
    }
}