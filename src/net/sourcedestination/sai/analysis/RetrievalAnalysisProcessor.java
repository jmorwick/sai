package net.sourcedestination.sai.analysis;

import java.util.Map;

public class RetrievalAnalysisProcessor {

    public static String DATE_PATTERN = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d).(\\d\\d\\d)";
    public static String ISSUING_QUERY_PATTERN = "(issuing query \\#(\\d+))";
    public static String EXPECTING_GRAPH_PATTERN =
            "(expecting graph \\#(\\d+)( from ([a-zA-Z0-9-]+)?( for query \\#(\\d+))?)";
    public static String RETRIEVED_GRAPH_PATTERN =
            "(retrieved graph \\#(\\d+)( from ([a-zA-Z0-9-]+)?( for query \\#(\\d+))?)";

    static ExperimentLogProcessor retrievalTimeFactory() {
        return new ExperimentLogProcessor() {
            @Override
            public String getPattern() {
                return "("+DATE_PATTERN+")?("+
                            ISSUING_QUERY_PATTERN+"|"+
                            RETRIEVED_GRAPH_PATTERN+")";
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
