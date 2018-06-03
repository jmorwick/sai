package net.sourcedestination.sai.analysis;

import java.util.Map;
import java.util.function.Supplier;

/** interface for a class that processes log results for a report */
public interface ExperimentLogProcessor extends Supplier<Map<String,Object>> {

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
}