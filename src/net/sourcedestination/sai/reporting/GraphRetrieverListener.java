package net.sourcedestination.sai.reporting;

import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.retrieval.GraphRetriever;
import static net.sourcedestination.sai.util.StreamUtil.listen;

/** a wrapper for a GraphRetriever which records statistics about all queries made.
 * Note that reporting is handled in a thread-safe manner, so it is possible 
 * that reporting will effect the performance of a parallel retrieval. 
 * 
 * @author jmorwick
 *
 */
public class GraphRetrieverListener<DB extends DBInterface>  implements GraphRetriever<DB> {

	private final GraphRetriever<DB> wrappedRetriever;
	private final Log log;
	
	public GraphRetrieverListener(GraphRetriever<DB> retriever, Log log) {
		this.wrappedRetriever = retriever;
		this.log = log;
	}
	
	public GraphRetriever<DB> getWrappedRetriever() {
		return wrappedRetriever;
	}
	
	@Override
	public Stream<Integer> retrieve(DB db, Graph q) {
		QueryRecord qr = new QueryRecord(q, db);
		log.recordQueryRecord(qr);
		return listen(wrappedRetriever.retrieve(db, q), qr::recordRetrievedGraphID);
	}
	

}
