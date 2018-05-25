package net.sourcedestination.sai.analysis;

import java.util.Map;

public class GraphRetrievalMetricProcessor {

    static ExperimentLogProcessor retrievalTimeFactory() {
        return new ExperimentLogProcessor() {
            @Override
            public String getPattern() {
                return null; // TODO: determine pattern for reading both query issued and initial response
            }

            @Override
            public void processLogMessage(String... groups) {
                    // TODO: find difference between query issued times and response times
            }

            @Override
            public Map<String, Object> get() {
                return null; // TODO: inidividual times for each query and overall time
            }
        };
    }
}
