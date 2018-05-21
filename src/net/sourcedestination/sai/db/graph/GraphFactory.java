package net.sourcedestination.sai.db.graph;

public interface GraphFactory<G extends Graph> {

	/** creates a deep copy of the graph g
	 * 
	 * @param g the graph from which to copy features/content
	 * @return a new graph with the old content
	 */
	public G copy(Graph g);
}
