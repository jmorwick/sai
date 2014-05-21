/* Copyright 2011 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    SAI is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SAI is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */

package sai.comparison.subgraphcomparators;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Multimap;

import sai.comparison.Util;
import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.Node;

/**
 * This class may not be included in 2.0; I'm still considering its inclusion
 *
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */

//TODO: completely rewrite
public class CompleteSubgraphComparator implements BinaryRelation<Graph> {

	public static boolean compare(DBInterface db, Graph g1, Graph g2, 
			BinaryRelation<Set<Feature>> featureSetComparator) {
		CompleteSubgraphComparator csc = 
				new CompleteSubgraphComparator(db, featureSetComparator);
		return Funcles.apply(csc, g1, g2);
	}

	private BinaryRelation<Set<Feature>> featureSetComparator;

	public CompleteSubgraphComparator(final DBInterface db, 
			BinaryRelation<Set<Feature>> featureSetComparator) {
		this.featureSetComparator = featureSetComparator;
	}


	@Override
	public boolean apply(Pair<Graph> args) {
		Graph sub = args.a1();
		Graph sup = args.a2();
		Multimap<Node, Node> possibilities = Util.nodeCompatibility(
				featureSetComparator, sub,
				sup);

		//make sure the features for the graphs themselves are compatible
		//TODO: determine why eclipse thinks the import IS NOT WORKING below... :(
		//should be: if(!apply(featureSetComparator,
		if(!info.kendall_morwick.funcles.Funcles.apply(featureSetComparator,
				sub.getFeatures(),
				sup.getFeatures())) {
			return false;
		}

		Comparator<Node> nc = new Comparator<Node>() {

			@Override
			public int compare(Node n1, Node n2) {
				return n1.getID() - n2.getID();
			}
		};
		
		Iterator<Map<Node,Node>> i = Util.getMappingIterator(possibilities, nc, nc);
		for(Map<Node,Node> map : Util.iteratorToCollection(i)) {
			if(map.size() < possibilities.size()) {
				return false;
			} else if(Util.matchedEdges(
					featureSetComparator,
					sub,
					sup,
					map) ==
					sub.getEdges().size() &&
					map.size() == sup.getNodes().size()) {
				return true;
			}
		}

		return false; // TODO: is this an error? I was throwing an exception here
	}

}
