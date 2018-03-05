package net.sourcedestination.sai.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

//TODO: add plotters to Log for creating statistical graphs
//TODO: allow saving and loading of Logs
//TODO: add a time-stamped event log for general events (starting/stopping of subtasks, etc)
//TODO: create UI in web app for viewing logs
//TODO?: make constructors and recording methods private, create with a factory that assigns the recorders appropriately
/** Logs hold information and statistics about the usage of a DBInterface and/or a 
 * GraphRetriever. Information is recorded with a DBWrapper or a RetrieverWrapper 
 * for a particular experiment and can be viewed from these Log objects through 
 * the SAI webapp. 
 * 
 * @author jmorwick
 */
public class Log {
	
	private final String taskName;
	private Date startTime;
	private Date endTime;
	private final Map<String,String> params;
	private final Multimap<String,Pair<Double>> plots2d = HashMultimap.create();
	private final Multimap<String,Triple<Double>> plots3d = HashMultimap.create();
	private final List<QueryRecord> queries = new ArrayList<>();
	private final List<AdditionRecord> additions = new ArrayList<>();
	private final List<DeletionRecord> deletions = new ArrayList<>();
	private final List<ClassificationRecord> classifications = new ArrayList<>();
	
	
	/** Constructs a new Log object which is ready to record data from a task.
	 * @param taskName name of the task being run
	 * @param params parameter values for the task being run
	 */
	public Log(String taskName, Tuple2<String,String> ... params) {
		this.taskName = taskName;
		this.startTime = new Date();
		this.params = new HashMap<>();
		for(Tuple2<String,String> t : params) {
			this.params.put(t._1, t._2);
		}
	}
	

	/** incorporates all data from the provided log in this log.
	 * 
	 * @param log log to incorporate data from
	 */
	public void include(Log log) {
		if(this.startTime.compareTo(log.startTime) > 0)
			this.startTime = log.startTime;
		
		if(this.endTime.compareTo(log.endTime) < 0)
			this.endTime = log.endTime;
		
		this.params.putAll(log.params);
		this.plots2d.putAll(log.plots2d);
		this.plots3d.putAll(log.plots3d);
		this.queries.addAll(log.queries);
		this.additions.addAll(log.additions);
		this.deletions.addAll(log.deletions);
		this.classifications.addAll(log.classifications);
	}
	
	/** adds a new record of a query being made to this log. 
	 * This is expected only to be called by a DB or retriever wrapper.
	 * @param record record to be added
	 */
	public void recordQueryRecord(QueryRecord record) {
		queries.add(record);
	}

	/** adds a new record of a graph being added to the DB to this log. 
	 * This is expected only to be called by a DB or retriever wrapper.
	 */
	public void recordAddition(DBInterface db, int graphID, Graph g) {
		additions.add(new AdditionRecord(db, graphID, g));
	}

	/** adds a new record of a graph being deleted from the DB to this log. 
	 * This is expected only to be called by a DB or retriever wrapper.
	 */
	public void recordDeletion(DBInterface db, int graphID, Graph g) {
		deletions.add(new DeletionRecord(db, graphID, g));
	}
	
	/** records a point on a 2d plot within this log
	 * 
	 * @param plotName name of the plot the point will be recorded on
	 * @param x x coordinate of point
	 * @param y y coordinate of point
	 */
	public void recordDataPoint(String plotName, double x, double y) {
		plots2d.put(plotName, Pair.makePair(x, y));
	}

	/** records a point on a 3d plot within this log
	 * 
	 * @param plotName name of the plot the point will be recorded on
	 * @param x x coordinate of point
	 * @param y y coordinate of point
	 * @param z z coordinate of point
	 */
	public void recordDataPoint(String plotName, double x, double y, double z) {
		plots3d.put(plotName, Triple.makeTriple(x, y, z));
	}

	public void recordClassification(DBInterface db, int gid, String result, String expected) {
		classifications.add(new ClassificationRecord(db, gid, result, expected));
	}
	
	/** returns the name of the task for which this log is recording data.
	 * 
	 * @return the name of the task for which this log is recording data
	 */
	public String getTaskName() {
		return taskName;
	}
	
	/** returns the time at which this log began recording data.
	 * 
	 * @return the time at which this log began recording data
	 */
	public Date getStartTime() {
		return startTime;
	}
	
	/** get parameter values the logged task was started with.
	 * 
	 * @return parameter values the logged task was started with
	 */
	public Map<String,String> getParameters() {
		return params;
	}
	
	/** returns the number of queries recorded in the log.
	 * 
	 * @return the number of queries recorded in the log
	 */
	public int getNumQueryRecords() { return queries.size(); }
	
	/** returns records of each query recorded in this log.
	 * 
	 * @return records of each query recorded in this log
	 */
	public Stream<QueryRecord> getQueryRecords() { return queries.stream(); }

	public QueryRecord getQueryRecord(int id) { return queries.get(id); }
	
	/** returns the number of graphs added to the db while logging.
	 * 
	 * @return  the number of graphs added to the db while logging
	 */
	public int getNumAdditionRecords() { return additions.size(); }
	
	/** returns records of all graphs added to the db while logging.
	 * 
	 * @return  records of all graphs added to the db while logging.
	 */
	public Stream<AdditionRecord> getAdditionRecords() { return additions.stream(); }

	
	/** returns the number of graphs deleted from the db while logging.
	 * 
	 * @return  the number of graphs deleted from the db while logging
	 */
	public int getNumDeletionRecords() { return deletions.size(); }
	
	/** returns records of the graphs deleted from the db while logging.
	 * 
	 * @return  the number of graphs deleted from the db while logging
	 */
	public Stream<DeletionRecord> getDeletionRecords() { return deletions.stream(); }
	
}
