package net.sourcedestination.sai.reporting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sourcedestination.funcles.tuple.Tuple2;

// TODO: add methods for retrieving data from report
// TODO: make this a class?
public interface Report {
	
	public String getTaskName();
	public Date getStartTime();
	public Date getEndTime();
	public String getReport();
	public Map<String,String> getParameters();
	
	// TODO: develop/reuse chart datastructure, add getters
	
	public static Report getBasicReport(String taskName, Date startTime, Date endTime, String report, 
			                            Tuple2<String,String> ... params) {
		Map<String,String> paramMap = new HashMap<String,String>();
		for(Tuple2<String,String> t : params) paramMap.put(t.a1(), t.a2());
		return new Report() {

			@Override
			public String getTaskName() {
				return taskName;
			}

			@Override
			public Date getStartTime() {
				return startTime;
			}

			@Override
			public Date getEndTime() {
				return endTime;
			}

			@Override
			public String getReport() {
				return report;
			}

			@Override
			public Map<String, String> getParameters() {
				return paramMap;
			}
			
		};
	}
}
