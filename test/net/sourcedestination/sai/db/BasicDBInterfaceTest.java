package net.sourcedestination.sai.db;

import static net.sourcedestination.sai.db.GraphIdAutoAssigning.addSurrogateIdAssigner;
import static org.junit.Assert.*;
import static net.sourcedestination.sai.graph.Graph.*;
import static net.sourcedestination.sai.graph.SampleGraphs.*;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.MutableGraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class BasicDBInterfaceTest {
	
	private static Set<File> tempFiles;

	@Before
	public void setUp() throws Exception {
		tempFiles = Sets.newHashSet();
	}

	@After
	public void tearDown() throws Exception {
		//delete all temporary files
		for(File f : tempFiles)
			f.delete();
	}
	
	private static Set<Integer> getIndexedGraphIDs(DBInterface db, int iid) {
		return db.retrieveGraph(iid).getFeatures()
				// only keep index features
				.filter(f -> f.getName().equals(INDEXES_FEATURE_NAME))
				// get the id's of the indexed graphs
				.map(f -> Integer.parseInt(f.getValue()))
				.collect(Collectors.toSet()); // return them as a set of graph id's
	}

	@Test
	public void testGraphIterator() throws AccessDeniedException {
		GraphFactory gf = MutableGraph::new;
		GraphIdAutoAssigning db = addSurrogateIdAssigner(new BasicDBInterface());
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid2 = db.addGraph(getSmallGraph2());
		int gid3 = db.addGraph(getSmallGraph3());
		int gid4 = db.addGraph(getSmallGraph4());
		assertEquals(4, db.getDatabaseSize());
		
		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid2, gid3, gid4);
		Iterator<Integer> gi = db.getGraphIDStream().iterator();
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(4, Sets.union(total, observed).size());
		assertTrue(!gi.hasNext());
	}

	@Test
	public void testDeletingGraphs() throws AccessDeniedException {
		GraphIdAutoAssigning db = addSurrogateIdAssigner(new BasicDBInterface());
		assertEquals(0, db.getDatabaseSize());
		int gid1 = db.addGraph(getSmallGraph1());
		int gid3 = db.addGraph(getSmallGraph3());
		db.deleteGraph(gid3);
		int gid4 = db.addGraph(getSmallGraph4());

		Set<Integer> observed = Sets.newHashSet();
		Set<Integer> total = Sets.newHashSet(gid1, gid4);
		Iterator<Integer> gi = db.getGraphIDStream().iterator();
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(gi.hasNext());
		observed.add(gi.next());
		assertEquals(2, Sets.union(total, observed).size());
		assertTrue(!gi.hasNext());
	}
	
}
