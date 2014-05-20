package sai.graph;

import sai.db.DBInterface;

public interface GraphFactory<G extends Graph> {
	
	/** creates a graph with no nodes, edges, or features, no DBInterface (null) 
	 * and an ID of -1. 
	 * 
	 * @param directed
	 * @param multi
	 * @param pseudo
	 * @return
	 */
	public G createEmptyGraph(boolean directed, boolean multi, boolean pseudo);

	/** creates a copy of the graph g which cannot be altered. It will have the 
	 * same ID and DBInterface as g, and all node and edge instances will also 
	 * be copied. Feature instances will not be copied (these references will be 
	 * preserved). 
	 * 
	 * @param g
	 * @return
	 */
	public G immutableCopy(G g);
	

	/** creates a copy of the graph g which cannot be altered. It will have the 
	 * the given ID and DBInterface, and all node and edge instances will also 
	 * be copied. Feature instances will not be copied (these references will be 
	 * preserved). 
	 * 
	 * @param g
	 * @return
	 */
	public G immutableCopy(G g, int id, DBInterface db);
	
	/** creates a mutable copy of the given graph g. The returned graph will 
	 * have -1 as its ID and null as its DBInterface. All node instances and 
	 * edge instances contained within the graph will also be copied, but 
	 * Feature instances will not be copied (these references will be preserved).
	 * 
	 * @param g
	 * @return
	 */
	public G mutableCopy(G g);
}
