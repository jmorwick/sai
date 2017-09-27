package net.sourcedestination.sai.reporting.stats;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.task.Task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DBSize extends FastDBStatistic {
    public double computeStat(DBInterface db) {
        return (double)db.getDatabaseSize();
    }
}