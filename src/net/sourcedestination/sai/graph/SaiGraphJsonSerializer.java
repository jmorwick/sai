package net.sourcedestination.sai.graph;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Function;

/**
 * Created by jmorwick on 6/18/17.
 */
public class SaiGraphJsonSerializer implements GraphSerializer {
    public String apply(Graph g) {

        // helper for encoding features into JSON arrays
        Function<Feature,JSONObject> featureToJSON = f -> {
            var obj = new JSONObject();

            obj.put("name", f.getName());
            obj.put("value", f.getValue());
            return obj;
        };

        // encode nodes
        JSONArray nodes = new JSONArray();
        g.getNodeIDs().map( nodeID -> {
            var node = new JSONObject();
            var features = new JSONArray();

            node.put("ID", nodeID);
            g.getNodeFeatures(nodeID).map(featureToJSON).forEach(features::put);
            node.put("features", features);
            return node;
        }).forEach(nodes::put);

        // encode edges
        JSONArray edges = new JSONArray();
        g.getEdgeIDs().map( edgeID -> {
            var edge = new JSONObject();
            var features = new JSONArray();

            edge.put("ID", edgeID);
            edge.put("fromID", g.getEdgeSourceNodeID(edgeID));
            edge.put("toID", g.getEdgeTargetNodeID(edgeID));
            g.getEdgeFeatures(edgeID).map(featureToJSON).forEach(features::put);
            edge.put("features", features);
            return edge;
        }).forEach(edges::put);

        // encode features attached to graph
        var globalFeatures = new JSONArray();
        g.getFeatures().map(featureToJSON).forEach(globalFeatures::put);

        // build complete JSON object
        var graph = new JSONObject();
        graph.put("features", globalFeatures);
        graph.put("nodes", nodes);
        graph.put("edges", edges);

        return graph.toString();
    }
}
