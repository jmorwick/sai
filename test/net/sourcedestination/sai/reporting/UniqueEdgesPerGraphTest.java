package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.UniqueEdgesPerGraph;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "UniqueEdgesPerGraph". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Created by amorehead on 2/7/2018. */
public class UniqueEdgesPerGraphTest {

    @Test
    public void testLexicalCompatibility() throws AccessDeniedException {

        /* This is a method that tests whether or not the
         "UniqueEdgesPerGraph" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        UniqueEdgesPerGraph stat = new UniqueEdgesPerGraph();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(4, stat.processGraph(db.retrieveGraph(1)), 0);
    }
}
