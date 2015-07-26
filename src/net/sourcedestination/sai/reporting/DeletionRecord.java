package net.sourcedestination.sai.reporting;

import net.sourcedestination.funcles.tuple.Tuple3;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;

/** holds a record of a graph being deleted from a DB.
 * 
 * @author jmorwick
 *
 */
public class DeletionRecord extends Tuple3<DBInterface,Integer,Graph> {

	/** creates a new record of the deletion of a graph.
	 * 
	 * @param db the DB the graph was deleted from
	 * @param id the ID of the deleted graph within the DB
	 * @param g the deleted graph 
	 */
	public DeletionRecord(DBInterface db, Integer id, Graph g) {
		super(db, id, g);
	}

	/** the DB the graph was deleted from. 
	 * 
	 * @return the DB the graph was deleted from
	 */
	public DBInterface getDB() { return _1; }
	
	/** the ID of the deleted graph within the DB.
	 * 
	 * @return the ID of the deleted graph within the DB
	 */
	public int getID() { return _2; }
	
	/** the deleted graph (potentially a copy).
	 * 
	 * @return the deleted graph 
	 */
	public Graph getGraph() { return _3; }
}
