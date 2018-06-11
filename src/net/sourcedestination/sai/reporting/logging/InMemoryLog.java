package net.sourcedestination.sai.reporting.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Stream;


import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

public class InMemoryLog extends InteractiveAppender {

    private List<String> log = new Vector<>();
    private boolean listening = false;

    @Override
    protected void append(ILoggingEvent event) {

        if(listening)
            log.add(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeStamp()),
                        ZoneId.systemDefault()).format(ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                    +  "  [" + event.getThreadName() + "] " +
                    event.getLoggerName() + " : "
                    + event.toString());
    }

    @Override
    public void startListening() {
        log = new Vector<>();
        listening = true;
    }

    @Override
    public Supplier<Stream<String>> stopListening() {
        listening = false;
        List<String> logForClosure = this.log; // fields aren't captured for closures
        return () -> logForClosure.stream();
    }

    @Override
    public boolean isListening() { return listening; }
}