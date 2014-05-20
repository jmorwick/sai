package sai.db;

import java.util.Iterator;
import java.util.Set;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.Index;

public interface DBInterface {
	public void connect();
	public void disconnect();
	public boolean isConnected();
	
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f);
    public Iterator<Graph> getGraphIterator();
    public Set<Integer> getHiddenGraphs();
    public void hideGraph(int graphID);
    public void unhideGraph(int graphID);
    public void deleteGraph(int graphID);
	
	public void addIndex(Graph g, Index i); 
	public Set<Integer> retrieveIndices(int graphID);
	public Set<Integer> retrieveIndexedGraphs(int indexID);
    public Iterator<Index> getIndexIterator();
    
	public Feature getFeature(String featureClass, int featureID);
	public Set<String> getFeatureClasses();
	public Set<Integer> getFeatureIDs();
	public Set<Integer> getFeatureIDs(String featureClass);
	
	public int getDatabaseSize();
	public int getDatabaseSizeWithoutIndices();
}
