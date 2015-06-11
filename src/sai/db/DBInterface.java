package sai.db;

import java.util.Set;
import java.util.stream.Stream;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;

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
	
	//TODO: remove these and create a wrapper which deals with the issue
    public Set<Integer> getHiddenGraphs(); 
    public void hideGraph(int graphID);
    public void unhideGraph(int graphID);
    
    
}
