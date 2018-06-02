package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.util.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogFileProcessor implements Task<Map<String, Object>> {

    private static Logger logger = Logger.getLogger(LogFileProcessor.class.getCanonicalName());

    private final ExperimentLogProcessor[] processors;
    private final Stream<String> in;
    private long bytesInLogFile = -1;
    private int linesInLogFile = -1;

    private long bytesRead = 0;
    private int linesRead = 0;

    public LogFileProcessor(Stream<String> in, ExperimentLogProcessor ... processors) {
        this.in = in;
        this.processors = processors;
    }

    public LogFileProcessor(Stream<String> in, int lines, ExperimentLogProcessor ... processors) {
        this(in, processors);
        this.linesInLogFile = lines;

    }

    public LogFileProcessor(Path path, ExperimentLogProcessor ... processors) throws IOException {
        this(Files.lines(path), processors);
        this.bytesInLogFile = path.toFile().length();
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

            in.forEach(line -> {
                bytesRead += line.length() + 1;
                linesRead++;
                line = line.trim();
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
            });

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
