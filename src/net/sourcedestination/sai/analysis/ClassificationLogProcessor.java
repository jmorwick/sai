package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.reporting.Report;

import java.time.LocalDateTime;

import static net.sourcedestination.sai.analysis.ExperimentLogProcessor.parseDate;

public class ClassificationLogProcessor implements ExperimentLogProcessor {

    public static String ISSUING_QUERY_PATTERN = "(issuing query \\#(\\d+))";


    @Override
    public String getPattern() {
        return "("+DATE_PATTERN+")?" +
                // TODO: add patterns for 4 different classification log messages
                "(.*)"; // gobbles up any unnecessary extra information
    }


    @Override
    public void processLogMessage(String... groups) {
        // find difference between query issued times and response times
        LocalDateTime t = parseDate(groups, 1);

        if(groups[10] != null && groups[10].trim().startsWith("in experiment #")) {
            // TODO: determine which of 4 possible log messages this is
        }
    }

    @Override
    public Report get() {
        Report report = new Report();
        // TODO: add classification stats to report
        return report;
    }
}
