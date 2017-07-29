package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

import java.util.function.Function;

/**
 *   TODO: comment / license
 */
public interface DBStatistic extends Function<DBInterface,Task<Double>> {

    /** DBStatistics can call this function if the computation is trivial and doesn't need to be run asynchronously.
     *
     * @param stat
     * @return A task tracking the generation of this statistic.
     */
    public static Task<Double> returnImmediately(Double stat) {
        return new Task<Double> () {
            public Double get() { return stat; }

            @Override
            public double getPercentageDone() { return 100.0; }
        };
    }
}
