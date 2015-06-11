package sai.comparison.compatibility;

import static org.junit.Assert.*;

import java.nio.file.AccessDeniedException;

import org.junit.Test;

import com.google.common.collect.Sets;

import sai.db.DBInterface;
import sai.db.SampleDBs;
import sai.graph.Feature;
import static sai.comparison.compatibility.FeatureSetCompatibilityChecker.greedy1To1Checker;
import static sai.comparison.compatibility.FeatureSetCompatibilityChecker.many1To1Checker;
import static sai.comparison.compatibility.FeatureCompatibilityChecker.areLexicallyCompatible;
import static info.kendall_morwick.funcles.Funcles.apply;

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
		assertTrue(apply(c, f1, f1));
		assertTrue(!apply(c, f1, f2));
		assertTrue(apply(c, f1, f3));
		assertTrue(!apply(c, f1, f4));
		assertTrue(!apply(c, f2, f3));
		assertTrue(!apply(c, f2, f4));
		assertTrue(!apply(c, f3, f4));

		assertTrue(!apply(c, f2, f1));
		assertTrue(apply(c, f3, f1));
		assertTrue(!apply(c, f4, f1));
		assertTrue(!apply(c, f3, f2));
		assertTrue(!apply(c, f4, f2));
		assertTrue(!apply(c, f4, f3));
	}
	
	private static FeatureCompatibilityChecker p = 
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
		assertTrue(apply(c, Sets.<Feature>newHashSet(), Sets.<Feature>newHashSet()));
		assertTrue(apply(c, Sets.<Feature>newHashSet(), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1), Sets.newHashSet(f1, f2)));
		assertTrue(!apply(c, Sets.newHashSet(f1, f2), Sets.newHashSet(f1)));
		assertTrue(!apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f1)));
		assertTrue(!apply(c2, Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f4)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!apply(c, Sets.newHashSet(f5, f6), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(apply(c, Sets.newHashSet(f5, f6), Sets.newHashSet(f5, f6, f7, f8)));
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
		assertTrue(apply(c, Sets.<Feature>newHashSet(), Sets.<Feature>newHashSet()));
		assertTrue(apply(c, Sets.<Feature>newHashSet(), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1), Sets.newHashSet(f1, f2)));
		assertTrue(!apply(c, Sets.newHashSet(f1, f2), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f4)));
		assertTrue(apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f2, f1, f7, f4, f3)));
		assertTrue(!apply(c2, Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f4)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7)));
		assertTrue(apply(c, Sets.newHashSet(f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(apply(c, Sets.newHashSet(f1, f4), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(!apply(c, Sets.newHashSet(f5, f6), Sets.newHashSet(f1, f3, f7, f8)));
		assertTrue(apply(c, Sets.newHashSet(f5, f6), Sets.newHashSet(f5, f6, f7, f8)));
	}

}
