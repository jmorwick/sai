package net.sourcedestination.sai.graph;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Graph {
	// TODO: consider making all Sets into Streams
	public Stream<Integer> getEdgeIDs();
	public Stream<Integer> getNodeIDs();
	public Stream<Feature> getFeatures();
	public Stream<Feature> getNodeFeatures(int n);
	public Stream<Feature> getEdgeFeatures(int e);
	public int getEdgeSourceNodeID(int edgeID);
	public int getEdgeTargetNodeID(int edgeID);

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
	
	/** serializes this graph to a JSON encoded string
	 * 
	 * @return a JSON encoded String representing this graph
	 */
	public default String toJSON() {
		
		// helper for encoding features into JSON arrays
		Function<Feature,JSONObject> featureToJSON = f -> {
			JSONObject obj = new JSONObject();
			obj.put("name", f.getName());
			obj.put("value", f.getValue());
			return obj;
		};
		
		// encode nodes
		JSONArray nodes = new JSONArray();
		getNodeIDs().map( nodeID -> {
			JSONObject node = new JSONObject();
			node.put("ID", nodeID);
			JSONArray features = new JSONArray();
			getNodeFeatures(nodeID).map(featureToJSON).forEach(features::put);
			node.put("features", features);
			return node;
		}).forEach(nodes::put);
		
		// encode edges
		JSONArray edges = new JSONArray();
		getEdgeIDs().map( edgeID -> {
			JSONObject edge = new JSONObject();
			edge.put("ID", edgeID);
			edge.put("fromID", getEdgeSourceNodeID(edgeID));
			edge.put("toID", getEdgeTargetNodeID(edgeID));
			JSONArray features = new JSONArray();
			getNodeFeatures(edgeID).map(featureToJSON).forEach(features::put);
			edge.put("features", features);
			return edge;
		}).forEach(edges::put);
		
		// encode features attached to graph
		JSONArray globalFeatures = new JSONArray();
		getFeatures().map(featureToJSON).forEach(globalFeatures::put);
		
		// build complete JSON object
		JSONObject graph = new JSONObject();
		graph.put("features", globalFeatures);
		graph.put("nodes", nodes);
		graph.put("edges", edges);
		
		return graph.toString();
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
        return optional.isPresent() ? optional.get() : null;
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
