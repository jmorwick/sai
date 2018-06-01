package net.sourcedestination.sai.reporting.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.List;
import java.util.Vector;

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
    public List<String> stopListening() {
        listening = false;
        return log;
    }

    @Override
    public boolean isListening() { return listening; }
}