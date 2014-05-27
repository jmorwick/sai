package sai.db;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Set;

import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;

public interface DBInterface {
	public void connect() throws AccessDeniedException;
	public void disconnect() throws AccessDeniedException, FileNotFoundException;
	public boolean isConnected();
	
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f);
    public Iterator<Integer> getGraphIDIterator();
    public Set<Integer> getHiddenGraphs();
    public void hideGraph(int graphID);
    public void unhideGraph(int graphID);
    public void deleteGraph(int graphID);

    public Iterator<Integer> getIndexIDIterator();
	public void addIndex(int graphID, int indexID); 
	public Set<Integer> retrieveIndexIDs(int graphID);
	public Set<Integer> retrieveIndexedGraphIDs(int indexID);
    
	public Feature getFeature(int featureID);
	public Set<String> getFeatureNames();
	public Set<Integer> getFeatureIDs();
	public Set<Integer> getFeatureIDs(String featureClass);
	public void setCompatible(Feature fa, Feature fb);
	public void setNotCompatible(Feature fa, Feature fb);
	public boolean isCompatible(Feature fa, Feature fb);
	
	public int getDatabaseSize();
	public int getDatabaseSizeWithoutIndices();
}
