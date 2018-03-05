package net.sourcedestination.sai.reporting;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.reporting.metrics.UniqueAttributesPerGraph;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;

/* This is a test class for "UniqueAttributesPerGraph". It passes if and only
 if the metric is fully accurate on the small graph database provided.
 Written by amorehead on 2/9/2018. */
public class UniqueAttributesPerGraphTest {

    @Test
    public void testLexicalCompatibility() throws AccessDeniedException {

        /* This is a method that tests whether or not the
         "UniqueAttributesPerGraph" class is implemented correctly or not. */
        DBInterface db = SampleDBs.smallGraphsDB();

        // This allows us to test the metric's class.
        UniqueAttributesPerGraph stat = new UniqueAttributesPerGraph();

        // The last parameter for "assertEquals" represents the amount of error allowed for the test results.
        assertEquals(5, stat.processGraph(db.retrieveGraph(1)), 0);
    }

}
