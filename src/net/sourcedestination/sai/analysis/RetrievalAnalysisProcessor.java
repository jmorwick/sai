package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.reporting.Report;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static net.sourcedestination.sai.analysis.ExperimentLogProcessor.parseDate;

public class RetrievalAnalysisProcessor implements ExperimentLogProcessor {

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
        LocalDateTime t = parseDate(groups, 1);

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
    public Report get() {
        Map<String,Long> queryTimes = new HashMap<>();
        for(Integer qid :  startTimes.keySet()) {
            if(endTimes.containsKey(qid)) {
                queryTimes.put(""+qid, ChronoUnit.NANOS.between(startTimes.get(qid), endTimes.get(qid)));
            }
        }
        Report report = new Report();
        report.put("queries", queryTimes.size());
        report.put("average time", queryTimes.values().stream().mapToLong(x->x).average().getAsDouble());
        report.put("times", queryTimes);
        return report;
    }

}
