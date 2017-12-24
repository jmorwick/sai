package net.sourcedestination.sai.comparison.matching;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.sai.graph.Graph;

public interface GraphMatching {
	public Graph getGraph1();
	public Graph getGraph2();
	public Integer getMatchedNodeInGraph2(int g1NodeID);
	public Integer getMatchedNodeInGraph1(int g2NodeID);
	public Integer getMatchedEdgeInGraph2(int g1NodeID);
	public Integer getMatchedEdgeInGraph1(int g2NodeID);

	public default Stream<Pair<Integer>> getAllEdgeMatches() {
		return getGraph1().getEdgeIDs()
				.filter(eid -> getMatchedEdgeInGraph2(eid) != null)
				.map(eid -> Pair.makePair(eid, getMatchedEdgeInGraph2(eid)));
	}

    public default Set<Pair<Integer>> getAllEdgeMatchesAsSet() {
        return getAllEdgeMatches().collect(Collectors.toSet());
    }

	public default Stream<Pair<Integer>> getAllNodeMatches() {
		return getGraph1().getNodeIDs()
				.filter(nid -> getMatchedNodeInGraph2(nid) != null)
				.map(nid -> Pair.makePair(nid, getMatchedNodeInGraph2(nid)));
	}

    public default Set<Pair<Integer>> getAllNodeMatchesAsSet() {
        return getAllNodeMatches().collect(Collectors.toSet());
    }
}
