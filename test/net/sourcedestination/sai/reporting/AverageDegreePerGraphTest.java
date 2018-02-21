package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.stats.AverageDegreePerGraph;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "AverageDegreePerGraph". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 2/3/2018. */
public class AverageDegreePerGraphTest {

    @Test
    public void testLexicalCompatibility() throws AccessDeniedException {

        /* This is a method that tests whether or not the
         "AverageDegreePerGraph" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        AverageDegreePerGraph stat = new AverageDegreePerGraph();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(2, stat.processGraph(db.retrieveGraph(1)), 0);
    }
}
