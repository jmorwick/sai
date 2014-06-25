package sai.indexing;

import java.util.Iterator;
import java.util.Set;

import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.MutableGraph;

public class FeatureIndexingDBWrapper implements DBInterface {

	private final DBInterface db;
	private final FeatureIndexGenerator gen;
	
	public FeatureIndexingDBWrapper(DBInterface wrappedDB, FeatureIndexGenerator gen) {
		this.db = wrappedDB;
		this.gen = gen;
	}
	
	public DBInterface getWrappedDB() { return db; }
	
	@Override
	public void disconnect() {
		db.disconnect();
	}

	@Override
	public boolean isConnected() {
		return db.isConnected();
	}

	@Override
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f) {
		return db.retrieveGraph(graphID, f);
	}

	@Override
	public Iterator<Integer> getGraphIDIterator() {
		return db.getGraphIDIterator();
	}

	@Override
	public Iterator<Integer> retrieveGraphsWithFeature(Feature f) {
		return db.retrieveGraphsWithFeature(f);
	}

	@Override
	public Iterator<Integer> retrieveGraphsWithFeatureName(String name) {
		return db.retrieveGraphsWithFeatureName(name);
	}

	@Override
	public Set<Integer> getHiddenGraphs() {
		return db.getHiddenGraphs();
	}

	@Override
	public void hideGraph(int graphID) {
		db.hideGraph(graphID);
	}

	@Override
	public void unhideGraph(int graphID) {
		db.unhideGraph(graphID);
	}

	@Override
	public void deleteGraph(int graphID) {
		db.deleteGraph(graphID);
	}

	@Override
	public int addGraph(Graph g) {
		MutableGraph g1 = new MutableGraph(g);
		for(Feature f : gen.apply(g))
			g1.addFeature(f);
			
		return db.addGraph(g1);
	}

	@Override
	public int getDatabaseSize() {
		return db.getDatabaseSize();
	}

}
