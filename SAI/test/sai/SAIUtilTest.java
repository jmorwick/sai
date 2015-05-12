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
	public void testFeatureRetention() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		Feature f1 = new Feature("a", "1");
		Feature f2 = new Feature("a", "2");
		Feature f3 = new Feature("a", "1");
		Feature f4 = new Feature("b", "1");

		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").test(f1));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").test(f2));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").test(f3));
		assertTrue(SAIUtil.featureWhiteListFilter("a", "b").test(f4));
		assertTrue(SAIUtil.featureWhiteListFilter("a").test(f1));
		assertTrue(SAIUtil.featureWhiteListFilter("a").test(f2));
		assertTrue(SAIUtil.featureWhiteListFilter("a").test(f3));
		assertTrue(!SAIUtil.featureWhiteListFilter("a").test(f4));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").test(f1));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").test(f2));
		assertTrue(!SAIUtil.featureWhiteListFilter("b").test(f3));
		assertTrue(SAIUtil.featureWhiteListFilter("b").test(f4));
		
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).test(f1));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).test(f2));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).test(f3));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a","b")).test(f4));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).test(f1));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).test(f2));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).test(f3));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("a")).test(f4));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).test(f1));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).test(f2));
		assertTrue(!SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).test(f3));
		assertTrue(SAIUtil.featureWhiteListFilter(Sets.newHashSet("b")).test(f4));
		
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
