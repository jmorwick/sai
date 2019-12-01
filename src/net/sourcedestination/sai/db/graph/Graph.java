package net.sourcedestination.sai.db.graph;

import com.google.common.collect.Streams;
import net.sourcedestination.sai.util.StreamUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.sourcedestination.sai.util.StreamUtil.extendList;

/*
 The core graph interface for use within SAI.
 New methods added by amorehead on 4/9/18.
*/

public interface Graph {
    public static final String INDEXES_FEATURE_NAME = "indexes";
    public static final String SAI_ID_NAME = "SAI-id";
    public static final Feature INDEX = new Feature("index graph", "true");
    public static final Feature DIRECTED = new Feature("directed graph", "true");
    public static final Feature TREE = new Feature("tree graph", "true");

    /**
     * returns the feature of the given feature-class associated with this feature set.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned.
     *
     * @param features the set of features to search
     * @return the feature with the matching name (if any)
     */
    public static Feature getFeature(Set<Feature> features, String featureName) {
        for (Feature f : features) {
            if (f.getName().equals(featureName))
                return f;
        }
        return null;
    }

    /**
     * returns the feature of the given feature-class associated with this feature set.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned.
     *
     * @param features the set of features to search
     * @return the feature with the matching name (if any)
     */
    public static Feature getFeature(Stream<Feature> features, String featureName) {
        Optional<Feature> optional =
                features.filter(f -> f.getName().equals(featureName)).findFirst();
        return optional.orElse(null);
    }

    public static Feature getFeature(String name, String value) {
        return new Feature(name, value);
    }

    public static Feature getIndexesFeature(int graphID) {
        return new Feature(INDEXES_FEATURE_NAME, graphID + "");
    }

    public static Feature getIDFeature(int graphID) {
        return new Feature(SAI_ID_NAME, graphID + "");
    }

    public Stream<Integer> getEdgeIDs();

    public Stream<Integer> getNodeIDs();

    public Stream<Feature> getFeatures();

    public Stream<Feature> getNodeFeatures(int n);

    public Stream<Feature> getEdgeFeatures(int e);

    public int getEdgeSourceNodeID(int edgeID);

    public int getEdgeTargetNodeID(int edgeID);

    public default Set<Integer> getEdgeIDsSet() {
        return getEdgeIDs().collect(Collectors.toSet());
    }

    public default Set<Integer> getNodeIDsSet() {
        return getNodeIDs().collect(Collectors.toSet());
    }

    public default Set<Feature> getFeaturesSet() {
        return getFeatures().collect(Collectors.toSet());
    }

    public default Set<Feature> getNodeFeaturesSet(int n) {
        return getNodeFeatures(n).collect(Collectors.toSet());
    }

    public default Set<Feature> getEdgeFeaturesSet(int e) {
        return getEdgeFeatures(e).collect(Collectors.toSet());
    }

    public default Feature getFeature(String featureName) {
        var features = getFeatures()
                .filter(f -> f.getName().equals(featureName))
                .collect(Collectors.toSet());
        return features.size() == 1 ? features.iterator().next() : null;
    }

    /**
     * if Node nid has only one feature, this method returns it. Otherwise it returns null.
     *
     * @param nid
     * @return
     */
    public default Feature getNodeFeature(int nid) {
        var features = getNodeFeaturesSet(nid);
        return features.size() == 1 ? features.iterator().next() : null;
    }

    /**
     * if Edge eid has only one feature, this method returns it. Otherwise it returns null.
     *
     * @param eid
     * @return
     */
    public default Feature getEdgeFeature(int eid) {
        var features = getEdgeFeaturesSet(eid);
        return features.size() == 1 ? features.iterator().next() : null;
    }

    /**
     * if Node nid has only one feature with given featureName, this method returns it. Otherwise it returns null.
     *
     * @param nid
     * @return
     */
    public default Feature getNodeFeature(String featureName, int nid) {
        var features = getNodeFeatures(nid)
                .filter(f -> f.getName().equals(featureName))
                .collect(Collectors.toSet());
        return features.size() == 1 ? features.iterator().next() : null;
    }

    /**
     * if Edge eid has only one feature with given featureName, this method returns it. Otherwise it returns null.
     *
     * @param eid
     * @return
     */
    public default Feature getEdgeFeature(String featureName, int eid) {
        var features = getEdgeFeatures(eid)
                .filter(f -> f.getName().equals(featureName))
                .collect(Collectors.toSet());
        return features.size() == 1 ? features.iterator().next() : null;
    }

    public default Graph copyWithoutEdge(int edgeID) {
        var t = new MutableGraph(this);
        if (t.getEdgeIDs().anyMatch(eid -> eid == edgeID))
            t.removeEdge(edgeID);
        return t;
    }

    public default Graph copyWithoutNode(int nodeID) {
        MutableGraph t = new MutableGraph(this);
        if (t.getNodeIDs().anyMatch(nid -> nid == nodeID))
            t.removeNode(nodeID);
        return t;
    }

    /**
     * generates all edges whose source node is nid.
     * Default implementation is O(E)
     */
    public default Stream<Integer> getIncidentToEdges(int nid) {
        return getEdgeIDs().filter(eid -> getEdgeSourceNodeID(eid) == nid);
    }

    /**
     * generates all edges whose target node is nid.
     * Default implementation is O(E)
     */
    public default Stream<Integer> getIncidentFromEdges(int nid) {
        return getEdgeIDs().filter(eid -> getEdgeTargetNodeID(eid) == nid);
    }

    /**
     * generates all edges incident on the node nid.
     * Default implementation is O(E)
     */
    public default Stream<Integer> getIncidentEdges(int nid) {
        return getEdgeIDs()
                .filter(eid -> getEdgeTargetNodeID(eid) == nid || getEdgeSourceNodeID(eid) == nid);
    }

    /* The following method indicates whether a given
     graph contains a specified node. */
    public default boolean hasNode(int nid) {
        return this.getNodeIDs().anyMatch(nid2 -> nid == nid2);
    }

    /* The following method indicates whether a given
     graph contains a specified edge. */
    public default boolean hasEdge(int eid) {
        return this.getEdgeIDs().anyMatch(eid2 -> eid == eid2);
    }

    /* The following method determines whether or not two
     given nodes are connected to each other by an edge. */
    public default boolean areConnectedNodes(int nid1, int nid2) {
        return this.getIncidentEdges(nid1).anyMatch(eid -> this.getEdgeSourceNodeID(eid) == nid2 || this.getEdgeTargetNodeID(eid) == nid2);
    }

    /** streams all paths through the graph as lists starting with a node ID
     * and following with a sequence of pairs of edge ID's and node ID's in the path.
     *
     * Only paths of the specified length or shorter are returned.
     * @param depth maximum length of the paths to be discovered
     */
    public default Stream<List<Integer>> allPaths(long depth) {

        Predicate<List<Integer>> duplicateNode = ls -> {
            var nodeIds = new HashSet<Integer>();
            for(int i=0; i<ls.size(); i+= 2) nodeIds.add(ls.get(i));
            return nodeIds.size() == (ls.size() + 1) / 2; // fewer unique node ID's than in the list
        };

        if(depth == 1) return getNodeIDs().map(nid -> List.of(nid));
        var terminals = allPaths(depth - 1)
                .flatMap(path -> getIncidentToEdges(path.get(path.size() - 1))
                        .map(eid -> extendList(extendList(path, eid), getEdgeTargetNodeID(eid)))
                        .filter(duplicateNode));
        return Streams.concat(allPaths(depth-1), terminals).distinct();
    }

    /** streams all paths through the graph as lists starting with a node ID
     * and following with a sequence of pairs of edge ID's and node ID's in the path.
     */
    public default Stream<List<Integer>> allPaths() {
        return allPaths(getNodeIDs().count())
                .filter(path -> path.size() > 1); // remove paths wtihout edges
    }
}
