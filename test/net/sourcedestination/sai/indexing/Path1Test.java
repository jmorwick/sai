package net.sourcedestination.sai.indexing;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.SampleGraphs;

import org.junit.Test;

import com.google.common.collect.Sets;

import static net.sourcedestination.sai.indexing.Path1IndexGenerator.PATH1NAME;
import static net.sourcedestination.sai.indexing.Path1IndexGenerator.encodeValue;
import static net.sourcedestination.sai.indexing.Path1IndexGenerator.generatePath1IndexFeatures;
import static org.junit.Assert.*;

public class Path1Test {

	@Test
	public void testPath1Generation() throws AccessDeniedException {
		Set<Feature> expected = Sets.newHashSet();
		expected.add(new Feature(PATH1NAME, "[test:a], [test:b]"));
		expected.add(new Feature(PATH1NAME, "[test:b], [test:c]"));
		expected.add(new Feature(PATH1NAME, "[test:c], [test:d]"));
		expected.add(new Feature(PATH1NAME, "[test:b], [test:d]"));
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
