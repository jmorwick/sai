package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.AverageClusteringCoefficient;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "AverageClusteringCoefficient". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 4/20/2018. */
public class AverageClusteringCoefficientTest {

    @Test
    public void testLexicalCompatibility() {

        /* This is a method that tests whether or not the
         "AverageClusteringCoefficient" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        AverageClusteringCoefficient stat = new AverageClusteringCoefficient();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(7.0/12.0, stat.processGraph(db.retrieveGraph(9)), 0.005);
    }

}
