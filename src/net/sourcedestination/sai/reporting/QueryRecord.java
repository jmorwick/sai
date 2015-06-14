package net.sourcedestination.sai.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.retrieval.GraphRetriever;

public class QueryRecord {
	
	private Object query;
	private DBInterface db;
	private GraphRetriever<?> ri;
	private List<Integer> retrievedGraphIDs = new ArrayList<Integer>();
	
	public QueryRecord(Object query, DBInterface db) {
		this.query = query;
		this.db = db;
		ri = null;
	}
	
	public QueryRecord(Object query, DBInterface db, GraphRetriever<?> ri) {
		this.query = query;
		this.db = db;
		this.ri = ri;
	}
	
	public void recordRetrievedGraphID(int gid) {
		retrievedGraphIDs.add(gid);
	}
	
	public Stream<Integer> getRetrievedGraphIDs() {
		return retrievedGraphIDs.stream();
	}
	
	public Object getQuery() {
		return query;
	}
	
	public DBInterface getDB() { return db; }
	
	public GraphRetriever<?> getGraphRetreiver() { return ri; }
}
