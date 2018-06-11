package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.experiment.retrieval.Retriever;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RetrievalAnalysisProcessor implements ExperimentLogProcessor {


    public static String DATE_PATTERN = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d).(\\d\\d\\d)";
    public static String ISSUING_QUERY_PATTERN = "(issuing query \\#(\\d+))";
    public static String EXPECTING_GRAPH_PATTERN =
            "(expecting graph \\#(\\d+)( from ([a-zA-Z0-9-]+))?( for query \\#(\\d+))?)";
    public static String RETRIEVED_GRAPH_PATTERN =
            "(retrieved graph \\#(\\d+)( from ([a-zA-Z0-9-]+))?( for query \\#(\\d+))?)";

    private HashMap<Integer,LocalDateTime> startTimes = new HashMap<>();
    private HashMap<Integer,LocalDateTime> endTimes = new HashMap<>();

    static ExperimentLogProcessor.Factory<RetrievalAnalysisProcessor>
    getFactory() {
        return () -> new RetrievalAnalysisProcessor();
    }

    @Override
    public String getPattern() {
        return "("+DATE_PATTERN+")?" +
                "(.*)" + // gobbles up any unnecessary extra information
                "("+ISSUING_QUERY_PATTERN+"|"+
                RETRIEVED_GRAPH_PATTERN+")" +
                "(.*)"; // gobbles up any unnecessary extra information
    }

    @Override
    public void processLogMessage(String... groups) {
        // find difference between query issued times and response times
        LocalDateTime t = LocalDateTime.of(
                groups[2] != null ? Integer.parseInt(groups[2]) : 0,    // year
                groups[3] != null ? Integer.parseInt(groups[3]) : 0,    // month
                groups[4] != null ? Integer.parseInt(groups[4]) : 0,    // day
                groups[5] != null ? Integer.parseInt(groups[5]) : 0,    // hour
                groups[6] != null ? Integer.parseInt(groups[6]) : 0,    // minute
                groups[7] != null ? Integer.parseInt(groups[7]) : 0,    // second
                groups[8] != null ? 1000000*Integer.parseInt(groups[7]) : 0    // nanoseconds
                );

        if(groups[10] != null && groups[10].trim().startsWith("issuing query")
                && groups[12] != null) {
            int queryId = Integer.parseInt(groups[12]);
            startTimes.put(queryId,t);    // remember when query was issued
        } else if(groups[10] != null && groups[10].trim().startsWith("retrieved graph")
                && groups[18] != null) {
            int graphId = Integer.parseInt(groups[14]);
            int queryId = Integer.parseInt(groups[18]);
            if(!endTimes.containsKey(queryId) || t.isAfter(endTimes.get(queryId))) {
                endTimes.put(queryId,t);
            }
        }
    }

    @Override
    public Map<String, Object> get() {
        Map<String,Long> queryTimes = new HashMap<>();
        for(Integer qid :  startTimes.keySet()) {
            if(endTimes.containsKey(qid)) {
                queryTimes.put(""+qid, ChronoUnit.NANOS.between(startTimes.get(qid), endTimes.get(qid)));
            }
        }
        Map<String,Object> report = new HashMap<>();
        report.put("queries", queryTimes.size());
        report.put("average time", queryTimes.values().stream().mapToLong(x->x).average().getAsDouble());
        report.put("times", queryTimes);
        return report;
    }

}
