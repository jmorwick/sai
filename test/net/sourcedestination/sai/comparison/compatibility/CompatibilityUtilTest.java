package net.sourcedestination.sai.comparison.compatibility;

import static net.sourcedestination.sai.comparison.compatibility.FeatureCompatibilityChecker.areLexicallyCompatible;
import static net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityChecker.greedy1To1Checker;
import static net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityChecker.many1To1Checker;
import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;

import net.sourcedestination.sai.comparison.compatibility.FeatureCompatibilityChecker;
import net.sourcedestination.sai.comparison.compatibility.FeatureSetCompatibilityChecker;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.SampleDBs;
import net.sourcedestination.sai.graph.Feature;

import org.junit.Test;

import com.google.common.collect.Sets;

public class CompatibilityUtilTest {

	@Test
	public void testLexicalCompatability() throws AccessDeniedException {
		DBInterface db = SampleDBs.getEmptyDB();
		Feature f1 = new Feature("a", "1");
		Feature f2 = new Feature("a", "2");
		Feature f3 = new Feature("a", "1");
		Feature f4 = new Feature("b", "1");

		assertTrue(areLexicallyCompatible(f1, f1));
		assertTrue(!areLexicallyCompatible(f1, f2));
		assertTrue(areLexicallyCompatible(f1, f3));
		assertTrue(!areLexicallyCompatible(f1, f4));
		assertTrue(!areLexicallyCompatible(f2, f3));
		assertTrue(!areLexicallyCompatible(f2, f4));
		assertTrue(!areLexicallyCompatible(f3, f4));

		assertTrue(!areLexicallyCompatible(f2, f1));
		assertTrue(areLexicallyCompatible(f3, f1));
		assertTrue(!areLexicallyCompatible(f4, f1));
		assertTrue(!areLexicallyCompatible(f3, f2));
		assertTrue(!areLexicallyCompatible(f4, f2));
		assertTrue(!areLexicallyCompatible(f4, f3));

		FeatureCompatibilityChecker c = FeatureCompatibilityChecker::areLexicallyCompatible;
		assertTrue(c.apply(f1, f1));
		assertTrue(!c.apply(f1, f2));
		assertTrue(c.apply(f1, f3));
		assertTrue(!c.apply(f1, f4));
		assertTrue(!c.apply(f2, f3));
		assertTrue(!c.apply(f2, f4));
		assertTrue(!c.apply(f3, f4));

		assertTrue(!c.apply(f2, f1));
		assertTrue(c.apply(f3, f1));
		assertTrue(!c.apply(f4, f1));
		assertTrue(!c.apply(f3, f2));
		assertTrue(!c.apply(f4, f2));
		assertTrue(!c.apply(f4, f3));
	}
	
	private static final FeatureCompatibilityChecker p =
			(a1, a2) -> {
					if(a1.getName().equals("b") && a2.getName().equals("a"))
						return a1.getValue().equals(a2.getValue());
					return areLexicallyCompatible(a1, a2);
			};
	
	@Test
	public void testGreedy1To1Checker() throws AccessDeniedException {
		FeatureSetCompatibilityChecker c = greedy1To1Checker(p);
		FeatureSetCompatibilityChecker c2 = greedy1To1Checker(
				FeatureCompatibilityChecker::areLexicallyCompatible);
		DBInterface db = SampleDBs.getEmptyDB();
		Feature f1 = new Feature("a", "1");
		Feature f2 = new Feature("a", "2");
		Feature f3 = new Feature("d", "1");
		Feature f4 = new Feature("b", "1");
		Feature f5 = new Feature("b", "2");
		Feature f6 = new Feature("c", "2");
		Feature f7 = new Feature("c", "1");
		Feature f8 = new Feature("c", "3");
		assertTrue(c.apply(Sets.newHashSet(), Sets.newHashSet()));
		assertTrue(c.apply(Sets.newHashSet(), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1), Sets.newHashSet(f1, f2)));
		assertTrue(!c.apply(Sets.newHashSet(f1, f2), Sets.newHashSet(f1)));
		assertTrue(!c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f1)));
		assertTrue(!c2.apply(Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f4)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!c.apply(Sets.newHashSet(f5, f6), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(c.apply(Sets.newHashSet(f5, f6), Sets.newHashSet(f5, f6, f7, f8)));
	}

	@Test
	public void testMany1To1Checker() throws AccessDeniedException {
		FeatureSetCompatibilityChecker c = many1To1Checker(p);
		FeatureSetCompatibilityChecker c2 = many1To1Checker(
				FeatureCompatibilityChecker::areLexicallyCompatible);
		DBInterface db = SampleDBs.getEmptyDB();
		Feature f1 = new Feature("a", "1");
		Feature f2 = new Feature("a", "2");
		Feature f3 = new Feature("d", "1");
		Feature f4 = new Feature("b", "1");
		Feature f5 = new Feature("b", "2");
		Feature f6 = new Feature("c", "2");
		Feature f7 = new Feature("c", "1");
		Feature f8 = new Feature("c", "3");
		assertTrue(c.apply(Sets.newHashSet(), Sets.newHashSet()));
		assertTrue(c.apply(Sets.newHashSet(), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1), Sets.newHashSet(f1, f2)));
		assertTrue(!c.apply(Sets.newHashSet(f1, f2), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f4)));
		assertTrue(c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f2, f1, f7, f4, f3)));
		assertTrue(!c2.apply(Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f4)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7)));
		assertTrue(c.apply(Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(c.apply(Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!c.apply(Sets.newHashSet(f5, f6), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(c.apply(Sets.newHashSet(f5, f6), Sets.newHashSet(f5, f6, f7, f8)));
	}

}
