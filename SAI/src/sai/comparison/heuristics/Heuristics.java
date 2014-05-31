package sai.comparison.heuristics;

import sai.comparison.matching.GraphMatching;

public class Heuristics {
	
	public static GraphMatchingHeuristic basicEdgeCount() {
		return new GraphMatchingHeuristic() {

			@Override
			public Double apply(GraphMatching m) {
				return (double)m.getAllEdgeMatches().size() / 
						(double)m.getGraph1().getEdgeIDs().size();
			}
			
		};
	}
}
