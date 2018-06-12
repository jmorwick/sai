package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.reporting.Report;

import java.time.LocalDateTime;
import java.util.function.Supplier;

/** interface for a class that processes log results for a report */
public interface ExperimentLogProcessor extends Supplier<Report> {

    // TODO: modify pattern to allow some components to be optional
    public static String DATE_PATTERN = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d).(\\d\\d\\d)";

    /** returns a regular-expression to match against log messages
     *
     * This expression should use memory parentheses for any components
     * of the message that need to be processed (rather than parsing them
     * out manually later).
     */
    public String getPattern();

    /** processes a log message parsed by the regular expression from getPattern
     *
     * Each of the group values from the memory parentheses will be passed in
     * as String arguments in order.
     * @param groups
     */
    public void processLogMessage(String ... groups);

    interface Factory<T extends ExperimentLogProcessor> extends Supplier<T> {}

    static LocalDateTime parseDate(String[] groups, int firstDateGroup) {
        // TODO: add validation and throw exception for violations
        return LocalDateTime.of(
                groups[firstDateGroup+1] != null ?
                        Integer.parseInt(groups[firstDateGroup+1]) : 0,    // year
                groups[firstDateGroup+2] != null ?
                        Integer.parseInt(groups[firstDateGroup+2]) : 0,    // month
                groups[firstDateGroup+3] != null ?
                        Integer.parseInt(groups[firstDateGroup+3]) : 0,    // day
                groups[firstDateGroup+4] != null ?
                        Integer.parseInt(groups[firstDateGroup+4]) : 0,    // hour
                groups[firstDateGroup+5] != null ?
                        Integer.parseInt(groups[firstDateGroup+5]) : 0,    // minute
                groups[firstDateGroup+6] != null ?
                        Integer.parseInt(groups[firstDateGroup+6]) : 0,    // second
                groups[firstDateGroup+7] != null ?
                        1000000*Integer.parseInt(groups[firstDateGroup+7]) : 0    // nanoseconds
        );
    }
}