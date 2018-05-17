package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.analysis.metrics.NodesPerGraph;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "NodesPerGraph". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 3/2/2018. */
public class NodesPerGraphTest {

    @Test
    public void testLexicalCompatibility() throws AccessDeniedException {

        /* This is a method that tests whether or not the
         "NodesPerGraph" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        NodesPerGraph stat = new NodesPerGraph();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(4, stat.apply(db.retrieveGraph(1)), 0);
    }
}
