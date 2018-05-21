package net.sourcedestination.sai.db.indexing;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.SampleGraphs;

import org.junit.Test;

import com.google.common.collect.Sets;

import static net.sourcedestination.sai.db.indexing.Path1IndexGenerator.encodeValue;
import static net.sourcedestination.sai.db.indexing.Path1IndexGenerator.generatePath1IndexFeatures;
import static org.junit.Assert.*;

public class Path1Test {

	@Test
	public void testPath1Generation() throws AccessDeniedException {
		Set<String> expected = Sets.newHashSet();
		expected.add("[test:a], [test:b]");
		expected.add("[test:b], [test:c]");
		expected.add("[test:c], [test:d]");
		expected.add("[test:b], [test:d]");
		assertEquals(expected,
				generatePath1IndexFeatures(SampleGraphs.getSmallGraph1(), "test"));
	}


	@Test
	public void testEncodeValue() throws AccessDeniedException {

		assertEquals("blah", encodeValue("blah"));
		assertEquals("bl\\,ah", encodeValue("bl,ah"));
		assertEquals("bl\tah", encodeValue("bl\tah"));
		assertEquals("b\\\\lah", encodeValue("b\\lah"));
		assertEquals("\\:b\\\\l\\,a\\:h\\,", encodeValue(":b\\l,a:h,"));
	}
	

}
