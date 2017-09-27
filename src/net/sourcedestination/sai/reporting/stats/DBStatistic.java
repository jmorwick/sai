package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.task.Task;

import java.util.function.Function;

/**
 *   TODO: comment / license
 */
public interface DBStatistic extends Function<DBInterface,Task<Double>> {

}
