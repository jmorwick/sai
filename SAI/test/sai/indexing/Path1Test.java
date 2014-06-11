package sai.indexing;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import org.junit.Test;

import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.BasicGraphFactory;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.SampleGraphs;

public class Path1Test {

	@Test
	public void testPath1() throws AccessDeniedException {
		GraphFactory gf = new BasicGraphFactory();
		DBInterface db = SampleDBs.getEmptyDB(gf);
		IndexGenerator gen = new Path1("test");
		db.connect();
		Set<Graph> indices = gen.generateIndices(db, gf, SampleGraphs.getSmallGraph1());
		
		assertEquals(4, indices.size());
		boolean seen1 = false;
		boolean seen2 = false;
		boolean seen3 = false;
		boolean seen4 = false;
		for(Graph i : indices) {
			assertEquals(1, i.getEdgeIDs().size());
			int eid = i.getEdgeIDs().iterator().next();
			assertEquals(1, i.getEdgeFeatures(eid).size());
			Feature ef = i.getEdgeFeatures(eid).iterator().next();
			assertEquals(1, i.getNodeFeatures(i.getEdgeSourceNodeID(eid)).size());
			Feature n1f = i.getNodeFeatures(i.getEdgeSourceNodeID(eid)).iterator().next();
			assertEquals(1, i.getNodeFeatures(i.getEdgeTargetNodeID(eid)).size());
			Feature n2f = i.getNodeFeatures(i.getEdgeTargetNodeID(eid)).iterator().next();
			assertEquals("test", ef.getName());
			assertEquals("test", n1f.getName());
			assertEquals("test", n2f.getName());

			if(!seen1 && n1f.getValue().equals("a") && n2f.getValue().equals("b")) {
				seen1 = true;
			} else if(!seen2 && n1f.getValue().equals("b") && n2f.getValue().equals("c")) {
				seen2 = true;
			} else if(!seen3 && n1f.getValue().equals("c") && n2f.getValue().equals("d")) {
				seen3 = true;
			} else if(!seen4 && n1f.getValue().equals("b") && n2f.getValue().equals("d")) {
				seen4 = true;
			} else {
				fail("unexpected edge: " + n1f + ", " + ef + ", " + n2f);
			}
		}
	}

}
