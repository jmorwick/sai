package sai.test;

import sai.db.BasicDBInterface;
import sai.db.DBInterface;
import sai.graph.GraphFactory;

public class SampleDBs {
	public static DBInterface getEmptyDB(GraphFactory<?> gf) {
		return new BasicDBInterface(gf);
	}
}
