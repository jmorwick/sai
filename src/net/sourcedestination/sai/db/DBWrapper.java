package net.sourcedestination.sai.db;

import java.util.Set;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;

/** a starter class for creating custom DB wrappers with default behavior for each required method.
 * 
 * @author jmorwick
 *
 */
public class DBWrapper<D extends DBInterface> implements DBInterface {

	private final D db;
	
	public DBWrapper(D wrappedDB) {
		this.db = wrappedDB;
	}
	
	public D getWrappedDB() { return db; }
	
	@Override
	public void disconnect() {
		db.disconnect();
	}

	@Override
	public boolean isConnected() {
		return db.isConnected();
	}

	@Override
	public Graph retrieveGraph(int graphID) {
		return db.retrieveGraph(graphID);
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
	public void deleteGraph(int graphID) {
		db.deleteGraph(graphID);
	}

	@Override
	public void addGraph(int graphID, Graph g) {
		db.addGraph(graphID, g);
	}

	@Override
	public int getDatabaseSize() {
		return db.getDatabaseSize();
	}

}
