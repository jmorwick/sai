package net.sourcedestination.sai.db;

import java.util.stream.Stream;

import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;

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
	public Graph retrieveGraph(int graphID) {
		return db.retrieveGraph(graphID);
	}

	@Override
	public Stream<Integer> getGraphIDStream() {
		return db.getGraphIDStream();
	}

	@Override
	public void deleteGraph(int graphID) {
		db.deleteGraph(graphID);
	}

	@Override
	public void addGraph(int graphId, Graph g) {
		db.addGraph(graphId, g);
	}

	@Override
	public int getDatabaseSize() {
		return db.getDatabaseSize();
	}

}
