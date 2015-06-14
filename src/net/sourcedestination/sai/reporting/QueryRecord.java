package net.sourcedestination.sai.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.retrieval.GraphRetriever;

/** record of a query made to a DB, potentially through a GraphRetriever.
 * 
 * @author jmorwick
 *
 */
public class QueryRecord {
	
	private Object query;
	private DBInterface db;
	private List<Integer> retrievedGraphIDs = new ArrayList<Integer>();
	
	/** creates a new QueryRecord for the given DB and query. This is a mutable object which may 
	 * continue to be updated after the constructor is completed.
	 * 
	 * @param query the query made to the database (could be an ID (integer), Feature, Graph, etc...)
	 * @param db the database being queried
	 */
	public QueryRecord(Object query, DBInterface db) {
		this.query = query;
		this.db = db;
	}
	
	/** called by a listener whenever a graph is lazily retrieved from the DB
	 * 
	 * @param gid ID of the retrieved graph within the DB
	 */
	public void recordRetrievedGraphID(int gid) {
		retrievedGraphIDs.add(gid);
	}
	
	/** streams all ID's of retrieved graphs recorded by this record.
	 * Stream returns these id's in the order that they were retrieved. 
	 * @return all ID's of retrieved graphs
	 */
	public Stream<Integer> getRetrievedGraphIDs() {
		return retrievedGraphIDs.stream();
	}
	
	/** The query issued to the DB for which graphs are being retrieved. 
	 * The query could be an integer if a graph is being retrieved by its ID. 
	 * It could be a String in the case that graphs are being retrieved by 
	 * the name of a feature. It could be a Feature, also, or a Graph, or 
	 * possibly any other object or a previously mentioned object used in 
	 * a novel context. 
	 * 
	 * @return
	 */
	public Object getQuery() {
		return query;
	}
	
	/** returns the DB being queried.
	 * 
	 * @return the DB being queried
	 */
	public DBInterface getDB() { return db; }
}
