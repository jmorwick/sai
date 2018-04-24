package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.AverageClusteringCoefficient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/* This is a test class for "AverageClusteringCoefficient". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 4/24/2018. */
public class AverageClusteringCoefficientTest2 {

    @Test
    public void testClusteringCoefficient1() {

        /* This is a method that tests whether or not the
         "AverageClusteringCoefficient" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        AverageClusteringCoefficient stat = new AverageClusteringCoefficient();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(3.0 / 5.0, stat.processGraph(db.retrieveGraph(10)), 0.005);
    }

}
