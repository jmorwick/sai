package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

/**
 * Created by jmorwick on 9/26/17.
 */
public abstract class FastDBStatistic implements DBStatistic {

    public abstract double computeStat(DBInterface db);

    public Task<Double> apply(DBInterface db) {
        return new Task<Double> () {
            public Double get() { return computeStat(db); }

            @Override
            public double getPercentageDone() { return 100.0; }
        };
    }
}
