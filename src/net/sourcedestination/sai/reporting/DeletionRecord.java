package net.sourcedestination.sai.reporting;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

public class DeletionRecord extends Tuple3<DBInterface,Integer,Graph> {

	public DeletionRecord(DBInterface db, Integer id, Graph g) {
		super(db, id, g);
	}

	public DBInterface getDB() { return a1(); }
	public int getID() { return a2(); }
	public Graph getGraph() { return a3(); }
}
