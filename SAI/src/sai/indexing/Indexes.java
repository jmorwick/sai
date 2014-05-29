package sai.indexing;

import sai.db.DBInterface;
import sai.graph.Feature;

public class Indexes {
	public static final Feature getIndexTag(DBInterface db) {
		return db.getFeature("index", "true");
	}
}
