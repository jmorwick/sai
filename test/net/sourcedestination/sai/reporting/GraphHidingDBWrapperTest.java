package net.sourcedestination.sai.reporting;

import com.google.common.collect.Sets;
import net.sourcedestination.sai.db.BasicDBInterface;
import org.junit.Test;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import static net.sourcedestination.sai.graph.SampleGraphs.*;
import static org.junit.Assert.assertEquals;

public class GraphHidingDBWrapperTest {

    @Test
    public void testHidingGraphs() throws AccessDeniedException {
        BasicDBInterface db = new BasicDBInterface();
        GraphHidingDBWrapper db2 = new GraphHidingDBWrapper(db);
        assertEquals(0, db2.getDatabaseSize());
        int gid1 = db2.addGraph(getSmallGraph1());
        int gid2 = db2.addGraph(getSmallGraph2());
        int gid3 = db2.addGraph(getSmallGraph3());
        int gid4 = db2.addGraph(getSmallGraph4());

        Set<Integer> observed = Sets.newHashSet(db2.getGraphIDStream().iterator());
        Set<Integer> expected = Sets.newHashSet(gid1, gid2, gid3, gid4);
        assertEquals(expected, observed);

        db2.hideGraph(gid2);
        expected = Sets.newHashSet(gid1, gid3, gid4);
        observed = Sets.newHashSet(db2.getGraphIDStream().iterator());
        assertEquals(expected, observed);

        db2.hideGraph(gid3);
        expected = Sets.newHashSet(gid1, gid4);
        observed = Sets.newHashSet(db2.getGraphIDStream().iterator());
        assertEquals(expected, observed);

        db2.unhideGraph(gid2);
        expected = Sets.newHashSet(gid1, gid2, gid4);
        observed = Sets.newHashSet(db2.getGraphIDStream().iterator());
        assertEquals(expected, observed);

        db2.hideGraph(gid4);
        expected = Sets.newHashSet(gid1, gid2);
        observed = Sets.newHashSet(db2.getGraphIDStream().iterator());
        assertEquals(expected, observed);
    }
}
