package net.sourcedestination.sai.graph;

import net.sourcedestination.funcles.consumer.Consumer2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by jmorwick on 6/18/17.
 */
public class SaiGraphJsonDeserializer implements GraphDeserializer<MutableGraph> {
    public MutableGraph apply(String json) {
        MutableGraph g = new MutableGraph();
        JSONObject parsedGraph = new JSONObject(json);

        // consumer which processes each feature in a JSONArray of features
        Consumer2<Consumer<Feature>,JSONArray> processFeatures =
                (consumeFeature, arr) ->
                        Stream.iterate(0, n->n+1).limit(arr.length())
                                .map(arr::getJSONObject)
                                .map(f -> new Feature(f.getString("name"), f.getString("value")))
                                .forEach(consumeFeature::accept);

        // proces global features
        processFeatures.accept(g::addFeature, parsedGraph.getJSONArray("features"));

        // process nodes
        JSONArray nodes = parsedGraph.getJSONArray("nodes");
        Stream.iterate(0, n->n+1).limit(nodes.length())
                .map(nodes::getJSONObject)
                .forEach(n -> {
                    int nodeID = n.getInt("ID");
                    g.addNode(nodeID);
                    processFeatures.accept(
                            f -> g.addNodeFeature(nodeID, f),
                            n.getJSONArray("features"));
                });

        // process edges
        JSONArray edges = parsedGraph.getJSONArray("edges");
        Stream.iterate(0, n->n+1).limit(edges.length())
                .map(edges::getJSONObject)
                .forEach(e -> {
                    int edgeID = e.getInt("ID");
                    g.addEdge(edgeID, e.getInt("fromID"), e.getInt("toID"));
                    processFeatures.accept(
                            f -> g.addEdgeFeature(edgeID, f),
                            e.getJSONArray("features"));
                });
        return g;
    }
}
