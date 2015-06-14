package net.sourcedestination.sai.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.sourcedestination.funcles.tuple.Pair;
import net.sourcedestination.funcles.tuple.Triple;
import net.sourcedestination.funcles.tuple.Tuple2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

// TODO: add methods for retrieving data from report
public class Log {
	
	private String taskName;
	private Date startTime;
	private Date endTime;
	private String report;
	private Map<String,String> params;
	private Multimap<String,Pair<Double>> plots2d = HashMultimap.create();
	private Multimap<String,Triple<Double>> plots3d = HashMultimap.create();
	private List<QueryRecord> queries = new ArrayList<QueryRecord>();
	private List<AdditionRecord> additions = new ArrayList<AdditionRecord>();
	private List<DeletionRecord> deletions = new ArrayList<DeletionRecord>();
	
	public Log(String taskName, Date startTime, Date endTime, String report, 
			                            Tuple2<String,String> ... params) {
		this.taskName = taskName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.report = report;
		this.params = new HashMap<String,String>();
		for(Tuple2<String,String> t : params) {
			this.params.put(t.a1(), t.a2());
		}
		
	}
	
	public void recordQueryRecord(QueryRecord record) {
		queries.add(record);
	}
	
	public void recordAddition(DBInterface db, int graphID, Graph g) {
		additions.add(new AdditionRecord(db, graphID, g));
	}
	
	public void recordDeletion(DBInterface db, int graphID, Graph g) {
		deletions.add(new DeletionRecord(db, graphID, g));
	}
	
	public void recordDataPoint(String plotName, double x, double y) {
		plots2d.put(plotName, Pair.makePair(x, y));
	}
	
	public void recordDataPoint(String plotName, double x, double y, double z) {
		plots3d.put(plotName, Triple.makeTriple(x, y, z));
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public String getReport() {
		return report;
	}
	
	public Map<String,String> getParameters() {
		return params;
	}
	
}
