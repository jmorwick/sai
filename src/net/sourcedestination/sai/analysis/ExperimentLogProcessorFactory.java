package net.sourcedestination.sai.analysis;

import java.util.function.Supplier;

@FunctionalInterface
public interface ExperimentLogProcessorFactory extends Supplier<ExperimentLogProcessor> {
}
