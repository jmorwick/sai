package sai.db;

import java.util.Set;
import java.util.stream.Stream;

import sai.db.DBInterface;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;

/** a starter class for creating custom DB wrappers with default behavior for each required method.
 * 
 * @author jmorwick
 *
 */
public class DBWrapper implements DBInterface {

	private final DBInterface db;
	
	public DBWrapper(DBInterface wrappedDB) {
		this.db = wrappedDB;
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
	public Stream<Integer> getGraphIDStream() {
		return db.getGraphIDStream();
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeature(Feature f) {
		return db.retrieveGraphsWithFeature(f);
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeatureName(String name) {
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
		return db.addGraph(g);
	}

	@Override
	public int getDatabaseSize() {
		return db.getDatabaseSize();
	}

}
