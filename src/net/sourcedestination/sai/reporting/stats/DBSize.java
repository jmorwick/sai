package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.task.Task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**  TODO: add test
 *   TODO: comment / license
 */
public class DBSize implements DBStatistic {
    public Task<Double> apply(DBInterface db) {
        return DBStatistic.returnImmediately((double)db.getDatabaseSize());
    }
}