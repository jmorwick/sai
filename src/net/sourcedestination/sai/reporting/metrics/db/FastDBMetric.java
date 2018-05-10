package net.sourcedestination.sai.reporting.metrics.db;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.reporting.metrics.db.DBMetric;
import net.sourcedestination.sai.task.Task;
import org.apache.log4j.Logger;

/**
 * Created by jmorwick on 9/26/17.
 */
public abstract class FastDBMetric implements DBMetric {

    private static final Logger logger = Logger.getLogger(DBMetric.class);

    public abstract double computeStat(DBInterface db);

    public FastDBMetric() {
        logger.info("Creating metric: " + this.getClass().getCanonicalName());
    }

    public Task<Double> apply(DBInterface db) {
        return new Task<Double>() {
            public Double get() {
                return computeStat(db);
            }

            @Override
            public double getPercentageDone() {
                return 100.0;
            }
        };
    }
}
