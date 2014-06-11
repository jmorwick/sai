package sai.db;

import sai.db.BasicDBInterface;
import sai.db.DBInterface;
import sai.graph.GraphFactory;
import sai.graph.SampleGraphs;

public class SampleDBs {
	public static DBInterface getEmptyDB(GraphFactory<?> gf) {
		return new BasicDBInterface(gf);
	}

	public static DBInterface smallGraphsDB(GraphFactory<?> gf) {
		DBInterface db = getEmptyDB(gf);
		db.addGraph(SampleGraphs.getSmallGraph1());
		db.addGraph(SampleGraphs.getSmallGraph2());
		db.addGraph(SampleGraphs.getSmallGraph3());
		db.addGraph(SampleGraphs.getSmallGraph4());
		return db;
	}
	

	public static DBInterface smallGraphsDBWithIndices(GraphFactory<?> gf) {
		DBInterface db = getEmptyDB(gf);
		db.addGraph(SampleGraphs.getSmallGraph1());
		db.addGraph(SampleGraphs.getSmallGraph2());
		db.addGraph(SampleGraphs.getSmallGraph3());
		db.addGraph(SampleGraphs.getSmallGraph4());
		return db;
	}
}
