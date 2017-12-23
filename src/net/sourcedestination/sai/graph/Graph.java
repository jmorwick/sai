package net.sourcedestination.sai.graph;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Graph {
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

	/** if Node nid has only one feature, this method returns it. Otherwise it returns null.
	 *
	 * @param nid
	 * @return
	 */
	public default Feature getNodeFeature(int nid) {
		Set<Feature> features = getNodeFeaturesSet(nid);
		return features.size() == 1 ? features.iterator().next() : null;
	}

	/** if Edge eid has only one feature, this method returns it. Otherwise it returns null.
	 *
	 * @param eid
	 * @return
	 */
	public default Feature getEdgeFeature(int eid) {
		Set<Feature> features = getEdgeFeaturesSet(eid);
		return features.size() == 1 ? features.iterator().next() : null;
	}

	/** if Node nid has only one feature with given featureName, this method returns it. Otherwise it returns null.
	 *
	 * @param nid
	 * @return
	 */
	public default Feature getNodeFeature(String featureName, int nid) {
		Set<Feature> features = getNodeFeatures(nid)
				.filter(f -> f.getName().equals(featureName))
				.collect(Collectors.toSet());
		return features.size() == 1 ? features.iterator().next() : null;
	}

	/** if Edge eid has only one feature with given featureName, this method returns it. Otherwise it returns null.
	 *
	 * @param eid
	 * @return
	 */
	public default Feature getEdgeFeature(String featureName, int eid) {
		Set<Feature> features = getEdgeFeatures(eid)
				.filter(f -> f.getName().equals(featureName))
				.collect(Collectors.toSet());
		return features.size() == 1 ? features.iterator().next() : null;
	}

	public static final String INDEXES_FEATURE_NAME = "indexes";
	public static final String SAI_ID_NAME = "SAI-id";

	public static final Feature INDEX = new Feature("index graph", "true");
	public static final Feature DIRECTED = new Feature("directed graph", "true");
	public static final Feature TREE = new Feature("tree graph", "true");

	public default <G extends Graph> G copyWithoutEdge(GraphFactory<G> gf, int edgeID) {
        MutableGraph t = new MutableGraph(this);
        if(t.getEdgeIDs().anyMatch(eid -> eid == edgeID))
        	t.removeEdge(edgeID);
        return gf.copy(t);
	}
	
	public default <G extends Graph> G copyWithoutNode(GraphFactory<G> gf, int nodeID) {
        MutableGraph t = new MutableGraph(this);
        if(t.getNodeIDs().anyMatch(nid -> nid == nodeID))
        	t.removeNode(nodeID);
        return gf.copy(t);
	}

	/** generates all edges whose source node is nid.
	 *  Default implementation is O(E)
	 */
	public default Stream<Integer> getIncidentToEdges(int nid) {
		return getEdgeIDs().filter(eid -> getEdgeSourceNodeID(eid) == nid);
	}

	/** generates all edges whose target node is nid.
	 *  Default implementation is O(E)
	 */
	public default Stream<Integer> getIncidentFromEdges(int nid) {
		return getEdgeIDs().filter(eid -> getEdgeTargetNodeID(eid) == nid);
	}

	/** generates all edges incident on the node nid.
	 *  Default implementation is O(E)
	 */
	public default Stream<Integer> getIncidentEdges(int nid) {
		return getEdgeIDs()
				.filter(eid -> getEdgeTargetNodeID(nid) == eid || getEdgeSourceNodeID(nid) == eid);
	}

	/** returns the feature of the given feature-class associated with this feature set.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned.
     * @param features the set of features to search
     * @return the feature with the matching name (if any)
     */
    public static Feature getFeature(Set<Feature> features, String featureName) {
        for(Feature f : features) {
            if(f.getName().equals(featureName))
                return f;
        }
        return null;
    }
    
    /** returns the feature of the given feature-class associated with this feature set.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned.
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
		return new Feature(INDEXES_FEATURE_NAME, graphID+"");
	}

	public static Feature getIDFeature(int graphID) {
		return new Feature(SAI_ID_NAME, graphID+"");
	}
}
