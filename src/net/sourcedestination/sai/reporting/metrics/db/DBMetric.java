package net.sourcedestination.sai.reporting.metrics.db;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

import java.util.function.Function;

/**
 * TODO: comment / license
 */
public interface DBMetric extends Function<DBInterface, Task<Double>> {

}
