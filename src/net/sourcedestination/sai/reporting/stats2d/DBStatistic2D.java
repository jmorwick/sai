package net.sourcedestination.sai.reporting.stats2d;

import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

import java.util.Set;
import java.util.function.Function;

/** DBStatistic implementations are used as plugins for determining statistics about databases of structures.
 * These statistics are determined to understand when some retrieval / indexing algorithms may be preferable.
 *
 *   TODO: comment / license
 */
public interface DBStatistic2D extends Function<DBInterface,Task<Set<Pair<Double>>>> {

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
