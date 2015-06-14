package net.sourcedestination.sai.util;

import static org.junit.Assert.*;
import static net.sourcedestination.sai.util.StreamUtil.listen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Lists;

public class TestStreamUtil {

	@Test
	public void testStreamFromCollection() {
		List<Integer> ls1 = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		List<Integer> ls2 = new ArrayList<Integer>();
		listen(ls1.stream(), ls2::add).forEach(x -> assertEquals((int)x, ls2.size()));
		assertEquals(ls1, ls2);
	}

	@Test
	public void testStreamFromIterator() {
		List<Double> ls1 = new ArrayList<Double>();
		List<Double> ls2 = new ArrayList<Double>();
		listen(Stream.generate(Math::random).limit(10), ls1::add).forEach(ls2::add);
		assertEquals(ls1, ls2);
	}
	
	@Test
	public void testIteratorFromStream() {
		List<Double> ls1 = new ArrayList<Double>();
		List<Double> ls2 = new ArrayList<Double>();
		Stream<Double> s = listen(Stream.generate(Math::random).limit(10), ls1::add);
		for(Iterator<Double> i = s.iterator(); i.hasNext(); ls2.add(i.next()));
		assertEquals(ls1, ls2);
	}

}
