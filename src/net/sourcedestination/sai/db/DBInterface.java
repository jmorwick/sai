package net.sourcedestination.sai.db;

import java.util.Set;
import java.util.stream.Stream;

import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.MutableGraph;

public interface DBInterface {
	public void disconnect();
	public boolean isConnected();

	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f);
	public int addGraph(Graph g);
    public void deleteGraph(int graphID);
	public int getDatabaseSize();
	
    public Stream<Integer> getGraphIDStream();
	public Stream<Integer> retrieveGraphsWithFeature(Feature f);
	public Stream<Integer> retrieveGraphsWithFeatureName(String name);

    public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
  	 fromDB.getGraphIDStream().forEach(
  			 id -> toDB.addGraph(fromDB.retrieveGraph(id, MutableGraph::new)));
    }
}
