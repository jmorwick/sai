package sai.comparison.matching;

import info.kendall_morwick.funcles.tuple.Pair;

import java.util.Set;

import sai.graph.Graph;

public interface GraphMatching {
	public Graph getGraph1();
	public Graph getGraph2();
	public int getMatchedNodeInGraph2(int g1NodeID);
	public int getMatchedNodeInGraph1(int g2NodeID);
	public Set<Pair<Integer>> getAllNodeMatches();
	public int getMatchedEdgeInGraph2(int g1NodeID);
	public int getMatchedEdgeInGraph1(int g2NodeID);
	public Set<Pair<Integer>> getAllEdgeMatches();
}
