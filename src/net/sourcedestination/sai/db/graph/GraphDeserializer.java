package net.sourcedestination.sai.db.graph;

import net.sourcedestination.funcles.consumer.Consumer2;
import net.sourcedestination.sai.db.DBPopulator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by jmorwick on 6/18/17.
 */
@FunctionalInterface
public interface GraphDeserializer extends Function<String,Graph> {

    public static GraphDeserializer saiJsonDecoderFactory() {
        return GraphDeserializer::saiJsonDecode;
    }

    public static MutableGraph saiJsonDecode(String json) {
        return saiJsonDecode(new JSONObject(json));
    }

    public static MutableGraph saiJsonDecode(JSONObject parsedGraph) {
        var g = new MutableGraph();

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
        var nodes = parsedGraph.getJSONArray("nodes");
        Stream.iterate(0, n->n+1).limit(nodes.length())
                .map(nodes::getJSONObject)
                .forEach(n -> {
                    var nodeID = n.getInt("ID");
                    g.addNode(nodeID);
                    processFeatures.accept(
                            f -> g.addNodeFeature(nodeID, f),
                            n.getJSONArray("features"));
                });

        // process edges
        var edges = parsedGraph.getJSONArray("edges");
        Stream.iterate(0, n->n+1).limit(edges.length())
                .map(edges::getJSONObject)
                .forEach(e -> {
                    var edgeID = e.getInt("ID");
                    g.addEdge(edgeID, e.getInt("fromID"), e.getInt("toID"));
                    processFeatures.accept(
                            f -> g.addEdgeFeature(edgeID, f),
                            e.getJSONArray("features"));
                });
        return g;
    }
}
