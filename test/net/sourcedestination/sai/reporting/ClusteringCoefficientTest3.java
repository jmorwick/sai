package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.graph.ClusteringCoefficient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/* This is a test class for "ClusteringCoefficient". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 4/24/2018. */
public class ClusteringCoefficientTest3 {

    @Test
    public void testClusteringCoefficient1() {

        /* This is a method that tests whether or not the
         "ClusteringCoefficient" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        ClusteringCoefficient stat = new ClusteringCoefficient();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(3.0 / 6.0, stat.apply(db.retrieveGraph(11)), 0.005);
    }

}
