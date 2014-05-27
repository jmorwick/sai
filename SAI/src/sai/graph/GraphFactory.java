package sai.graph;

public interface GraphFactory<G extends Graph> {

	/** creates a copy of the graph g which cannot be altered. It will have the 
	 * same ID as g, and all node and edge instances will also be copied. Feature 
	 * instances will not be copied (these references will be preserved). 
	 * 
	 * @param g the graph from which to copy features/content
	 * @return a new graph with the old content
	 */
	public G copy(Graph g);
	

	/** creates a copy of the graph g which cannot be altered. It will have the 
	 * the given ID and all node and edge instances will also be copied. Feature 
	 * instances will not be copied (these references will be preserved). 
	 * 
	 * @param g the graph from which to copy features/content
	 * @param id the new id for the new graph
	 * @return a new graph with (mostly) the old content
	 */
	public G copy(Graph g, int id);
}
