package sai;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.Feature;

import com.google.common.collect.Sets;

public class SAIUtilTest {

	@Test
	public void testIteratorToCollection() {
		Set<Integer> test1 = Sets.newHashSet();
		for(int x : SAIUtil.iteratorToCollection(new Iterator<Integer>() {
					private int i = 0;
					@Override
					public boolean hasNext() {
						return i < 5;
					}

					@Override
					public Integer next() {
						return ++i;
					}
				}))
			test1.add(x);
		Set<Integer> test2 = Sets.newHashSet();
		for(int x : SAIUtil.iteratorToCollection(Sets.newHashSet(1, 2, 3, 4, 5).iterator()))
			test2.add(x);

		assertEquals(Sets.newHashSet(1, 2, 3, 4, 5), test1);
		assertEquals(Sets.newHashSet(1, 2, 3, 4, 5), test2);
	}

	@Test
	public void testFeatureRetention() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		db.connect();
		Feature f1 = new Feature("a", "1");
		Feature f2 = new Feature("a", "2");
		Feature f3 = new Feature("a", "1");
		Feature f4 = new Feature("b", "1");

		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").apply(f1));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").apply(f2));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").apply(f3));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").apply(f4));
		assertTrue(SAIUtil.featureWhiteListFilter("a").apply(f1));
		assertTrue(SAIUtil.featureWhiteListFilter("a").apply(f2));
		assertTrue(SAIUtil.featureWhiteListFilter("a").apply(f3));
		assertTrue(!SAIUtil.featureWhiteListFilter("a").apply(f4));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").apply(f1));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").apply(f2));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").apply(f3));
		assertTrue(SAIUtil.featureWhiteListFilter("b").apply(f4));
		
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).apply(f1));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).apply(f2));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).apply(f3));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).apply(f4));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).apply(f1));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).apply(f2));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).apply(f3));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).apply(f4));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).apply(f1));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).apply(f2));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).apply(f3));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).apply(f4));
		
		Set<Feature> all = Sets.newHashSet(f1, f2, f3, f4);
		Set<Feature> as = Sets.newHashSet(f1, f2, f3);
		Set<Feature> bs = Sets.newHashSet(f4);
		Set<Feature> none = Sets.newHashSet();
		assertEquals(none, SAIUtil.retainOnly(all));
		assertEquals(as, SAIUtil.retainOnly(all, "a"));
		assertEquals(bs, SAIUtil.retainOnly(all, "b"));
		assertEquals(all, SAIUtil.retainOnly(all, "a", "b"));
		assertEquals(none, SAIUtil.retainOnly(as));
		assertEquals(as, SAIUtil.retainOnly(as, "a"));
		assertEquals(none, SAIUtil.retainOnly(as, "b"));
		assertEquals(as, SAIUtil.retainOnly(as, "a", "b"));
		assertEquals(none, SAIUtil.retainOnly(bs));
		assertEquals(none, SAIUtil.retainOnly(bs, "a"));
		assertEquals(bs, SAIUtil.retainOnly(bs, "b"));
		assertEquals(bs, SAIUtil.retainOnly(bs, "a", "b"));
	}

}
