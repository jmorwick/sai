package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.util.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFileProcessor implements Task<Map<String, Object>> {

    private static Logger logger = Logger.getLogger(LogFileProcessor.class.getCanonicalName());

    private final ExperimentLogProcessor[] processors;
    private final Scanner in;
    private final long bytesInLogFile;

    private long bytesRead = 0;

    public LogFileProcessor(InputStream in, ExperimentLogProcessor ... processors) {
        this.processors = processors;
        this.in = new Scanner(in);
        this.bytesInLogFile = -1;
    }

    public LogFileProcessor(InputStream in, long bytesInLogFile, ExperimentLogProcessor ... processors) {
        this.processors = processors;
        this.in = new Scanner(in);
        this.bytesInLogFile = bytesInLogFile;
    }

    public LogFileProcessor(File f, ExperimentLogProcessor ... processors) throws FileNotFoundException {
        this.processors = processors;
        this.bytesInLogFile = f.length();
        this.in = new Scanner(f);
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> reportModel = new HashMap<>();
        try {
            logger.info("beginning log analysiss");
            Map<ExperimentLogProcessor, Pattern> compiledPatterns = new HashMap<>();
            for (var processor : processors) {
                compiledPatterns.put(processor, Pattern.compile(processor.getPattern()));
            }

            while (in.hasNextLine()) {
                String line = in.nextLine().trim();

                bytesRead += line.length() + 1;
                for (Map.Entry<ExperimentLogProcessor, Pattern> e : compiledPatterns.entrySet()) {
                    Matcher m = e.getValue().matcher(line);
                    if (m.find()) {
                        String[] groups = new String[m.groupCount()+1];
                        for (int i = 0; i < groups.length; i++)
                            groups[i] = m.group(i);

                        try {
                            e.getKey().processLogMessage(groups);
                        } catch (Exception ex) {
                            logger.throwing(getClass().getCanonicalName(),
                                    "error analyzing line: " + line, ex);
                        }
                    }
                }
            }

            for (var processor : processors) {
                reportModel.putAll(processor.get());
            }
            logger.info("log analysis complete");
        }catch(Exception ex) {
            logger.throwing(getClass().getCanonicalName(),
                    "unexpected error during execution of log analyzer", ex);
        }
        return reportModel;
    }

    /** returns kilobytes read from the log input stream
     */
    @Override
    public int getProgressUnits() {
        return (int)(bytesRead / 1024);
    }

    /** returns size of log stream (if known) in kilobytes.
     * If the size is not known, -1 is returned.
     */
    @Override
    public int getTotalProgressUnits() {
        return (int)(bytesInLogFile / 1024);
    }
}
