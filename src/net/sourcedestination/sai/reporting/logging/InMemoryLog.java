package net.sourcedestination.sai.reporting.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.List;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InMemoryLog extends InteractiveAppender {

    private List<String> log = new Vector<>();
    private boolean listening = false;

    @Override
    protected void append(ILoggingEvent event) {
        if(listening) log.add(event.toString());
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