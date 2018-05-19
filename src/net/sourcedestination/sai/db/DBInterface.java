package net.sourcedestination.sai.db;

import java.util.stream.Stream;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;

public interface DBInterface {
	public void disconnect();
	public boolean isConnected();

	public Graph retrieveGraph(int graphID);
	public void addGraph(int graphID, Graph g);
    public void deleteGraph(int graphID);
	public int getDatabaseSize();
	
    public Stream<Integer> getGraphIDStream();
	public Stream<Integer> retrieveGraphsWithFeature(Feature f);
	public Stream<Integer> retrieveGraphsWithFeatureName(String name);

    public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
  	 fromDB.getGraphIDStream().forEach(
  			 id -> toDB.addGraph(id, fromDB.retrieveGraph(id)));
    }
}
