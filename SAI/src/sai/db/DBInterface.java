package sai.db;

import java.util.Iterator;
import java.util.Set;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;

public interface DBInterface {
	public void disconnect();
	public boolean isConnected();

	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f);
    public Iterator<Integer> getGraphIDIterator();
	public Iterator<Integer> retrieveGraphsWithFeature(Feature f);
	public Iterator<Integer> retrieveGraphsWithFeatureName(String name);
    public Set<Integer> getHiddenGraphs();
    public void hideGraph(int graphID);
    public void unhideGraph(int graphID);
    public void deleteGraph(int graphID);
	public int addGraph(Graph g);
	
	public int getDatabaseSize();
}
