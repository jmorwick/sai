package net.sourcedestination.sai.reporting;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

/** holds a record of a graph being added to a DB.
 * 
 * @author jmorwick
 *
 */
public class AdditionRecord extends Tuple3<DBInterface,Integer,Graph> {

	/** creates a new record of the addition of a graph.
	 * 
	 * @param db the DB the graph was added to
	 * @param id the ID of the added graph within the DB
	 * @param g the added graph 
	 */
	public AdditionRecord(DBInterface db, Integer id, Graph g) {
		super(db, id, g);
	}

	/** the DB the graph was added to. 
	 * 
	 * @return the DB the graph was added to
	 */
	public DBInterface getDB() { return _1; }

	/** the ID of the added graph within the DB.
	 * 
	 * @return the ID of the added graph within the DB
	 */
	public int getID() { return _2; }

	
	/** the added graph (potentially a copy).
	 * 
	 * @return the added graph 
	 */
	public Graph getGraph() { return _3; }
}
