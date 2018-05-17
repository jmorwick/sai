package net.sourcedestination.sai.analysis;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.DoubleStream;

public class GraphMetricsProcessor implements ExperimentLogProcessor {

    static Logger logger = LogManager.getLogger(GraphMetricsProcessor.class);


    public enum AggregationType {
        MIN {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.average();
            }
        },
        MAX {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.max();
            }
        },
        AVERAGE {
            public OptionalDouble aggregate(DoubleStream s) {
                return s.min();
            }
        };

        public abstract OptionalDouble aggregate(DoubleStream s);
    }

    private final Map<String, DBInterface> dbs;
    private final Map<String,GraphMetric> metrics;
    private final Map<String,AggregationType> aggregationTypes;
    private final Map<String,List<Double>> metricValues;

    public GraphMetricsProcessor(Map<String, DBInterface> dbs,
                                 Tuple3<String, AggregationType, GraphMetric> ... metrics) {
        this.dbs = dbs;
        this.metrics = new HashMap<>();
        this.aggregationTypes = new HashMap<>();
        this.metricValues = new HashMap<>();
        for(Tuple3<String, AggregationType, GraphMetric> t : metrics) {
            aggregationTypes.put(t._1, t._2);
            this.metrics.put(t._1, t._3);
            metricValues.put(t._1, new ArrayList<>());
        }

        logger.info("created with dbs: " + dbs.keySet() +
                " and metrics " + this.metrics.keySet());
    }

    @Override
    public String getPattern() {
        return "retrieved Graph ID #(\\d+) from (.*)$";
    }

    @Override
    public void processLogMessage(String ... groups) {
        final int gid = Integer.parseInt(groups[1]);
        final String dbname = groups[2];
        final DBInterface db = dbs.get(dbname);
        final Graph g = db.retrieveGraph(gid);

        for(String metricName : metrics.keySet()) {
            metricValues.get(metricName).add(metrics.get(metricName).apply(g));
        }
    }

    @Override
    public Map<String,Object> get() {
        Map<String,Object> results = new HashMap<>();

        for(String metricName : metrics.keySet()) {
            OptionalDouble d = (aggregationTypes.containsKey(metricName) ?
                    aggregationTypes.get(metricName) :
                    AggregationType.AVERAGE).aggregate(
                    metricValues.get(metricName).stream()
                            .mapToDouble(x -> x));
            results.put(metricName,
                    d.isPresent() ?
                            ""+d.getAsDouble() :
                            "n/a"
            );
        }

        return results;
    }
}
