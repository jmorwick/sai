package net.sourcedestination.sai.reporting.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.List;

public abstract class InteractiveAppender extends AppenderBase<ILoggingEvent> {

    public abstract void startListening();

    public abstract List<String> stopListening();

    public abstract boolean isListening();
}