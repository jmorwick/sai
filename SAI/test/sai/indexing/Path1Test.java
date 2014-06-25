package sai.indexing;

import java.nio.file.AccessDeniedException;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import sai.graph.Feature;
import sai.graph.SampleGraphs;
import static org.junit.Assert.*;
import static sai.indexing.Path1IndexGenerator.PATH1NAME;
import static sai.indexing.Path1IndexGenerator.encodeValue;
import static sai.indexing.Path1IndexGenerator.generatePath1IndexFeatures;

public class Path1Test {

	@Test
	public void testPath1Generation() throws AccessDeniedException {
		Set<Feature> expected = Sets.newHashSet();
		expected.add(new Feature(PATH1NAME, "test,a:test,a:test,b"));
		expected.add(new Feature(PATH1NAME, "test,b:test,a:test,c"));
		expected.add(new Feature(PATH1NAME, "test,c:test,a:test,d"));
		expected.add(new Feature(PATH1NAME, "test,b:test,a:test,d"));
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
