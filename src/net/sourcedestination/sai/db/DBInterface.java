package net.sourcedestination.sai.db;

import java.util.stream.Stream;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

public interface DBInterface {
	void disconnect();
	boolean isConnected();

	Graph retrieveGraph(int graphID);
	void addGraph(int graphId, Graph g);
    void deleteGraph(int graphID);
	int getDatabaseSize();
	
    Stream<Integer> getGraphIDStream();
	Stream<Integer> retrieveGraphsWithFeature(Feature f);
	Stream<Integer> retrieveGraphsWithFeatureName(String name);

    public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
    	fromDB.getGraphIDStream().forEach(
    			id -> toDB.addGraph(fromDB.retrieveGraph(id))
		);
    }

    default int addGraph(Graph g) {
		int id = getDatabaseSize();
		if(retrieveGraph(id) != null) id++;
		addGraph(id, g);
		return id;
	}
}
