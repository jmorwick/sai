package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.util.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogProcessingTask implements Task<Map<String, Object>> {

    private static Logger logger = Logger.getLogger(LogProcessingTask.class.getCanonicalName());

    private final ExperimentLogProcessor[] processors;
    private final Stream<String> in;
    private long bytesInLogFile = -1;
    private int linesInLogFile = -1;

    private long bytesRead = 0;
    private int linesRead = 0;

    public LogProcessingTask(Stream<String> in, ExperimentLogProcessor ... processors) {
        this.in = in;
        this.processors = processors;
    }

    public LogProcessingTask(Stream<String> in, int lines, ExperimentLogProcessor ... processors) {
        this(in, processors);
        this.linesInLogFile = lines;

    }

    public LogProcessingTask(Path path, ExperimentLogProcessor ... processors) throws IOException {
        this(Files.lines(path), processors);
        this.bytesInLogFile = path.toFile().length();
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> reportModel = new HashMap<>();
        try {
            Map<ExperimentLogProcessor, Pattern> compiledPatterns = new HashMap<>();
            for (var processor : processors) {
                String pattern = processor.getPattern();
                logger.info("using " +processor + " to process messages matching: " + pattern);
                compiledPatterns.put(processor, Pattern.compile(pattern));
            }


            logger.info("beginning log analysis");
            in.forEach(line -> {
                bytesRead += line.length() + 1;
                linesRead++;
                line = line.trim();
                logger.info("examining line #" + linesRead + ": " + line);
                for (Map.Entry<ExperimentLogProcessor, Pattern> e : compiledPatterns.entrySet()) {
                    Matcher m = e.getValue().matcher(line);
                    logger.info("using " + e.getKey()+ " to check message");
                    if (m.find()) {
                        String[] groups = new String[m.groupCount()+1];
                        for (int i = 0; i < groups.length; i++)
                            groups[i] = m.group(i);
                        logger.info("match found: " + Arrays.toString(groups));
                        try {
                            e.getKey().processLogMessage(groups);
                        } catch (Exception ex) {
                            logger.throwing(getClass().getCanonicalName(),
                                    "error analyzing line: " + line, ex);
                        }
                    } else {
                        logger.info("no match");
                    }
                }
            });

            logger.info("log analysis complete");
            for (var processor : processors) {
                reportModel.putAll(processor.get());
            }
            logger.info("report compiled");
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "error encountered duing exeuction of log analyzer", ex);
        }
        return reportModel;
    }

    /** returns kilobytes read from the log input stream
     */
    @Override
    public int getProgressUnits() {
        return bytesInLogFile != -1 ? (int)(bytesRead / 1024) : linesRead;
    }

    /** returns size of log stream (if known) in kilobytes.
     * If the size is not known, -1 is returned.
     */
    @Override
    public int getTotalProgressUnits() {
        return bytesInLogFile != 0 ? (int)(bytesInLogFile / 1024) : linesInLogFile;
    }
}
