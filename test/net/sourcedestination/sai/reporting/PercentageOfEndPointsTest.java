package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.graph.PercentageOfEndPoints;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "PercentageofEndPoints". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Created by amorehead on 2/12/2018. */
public class PercentageOfEndPointsTest {

    @Test
    public void testLexicalCompatibility() throws AccessDeniedException {

        /* This is a method that tests whether or not the
         "PercentageOfEndPoints" class is implemented
          correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        PercentageOfEndPoints stat = new PercentageOfEndPoints();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(0.25, stat.apply(db.retrieveGraph(1)), 0);

    }
}
