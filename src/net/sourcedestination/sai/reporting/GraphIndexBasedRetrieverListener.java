package net.sourcedestination.sai.reporting;

import static net.sourcedestination.sai.util.StreamUtil.listen;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.retrieval.GraphIndexBasedRetriever;

/** a wrapper for a GraphIndexBasedRetriever which records statistics about all queries made.
 * Note that reporting is handled in a thread-safe manner, so it is possible 
 * that reporting will effect the performance of a parallel retrieval. 
 * 
 * @author jmorwick
 *
 */
public class GraphIndexBasedRetrieverListener implements GraphIndexBasedRetriever {
	private final GraphIndexBasedRetriever wrappedRetriever;
	private final Log log;
	
	public GraphIndexBasedRetrieverListener(GraphIndexBasedRetriever retriever, Log log) {
		this.wrappedRetriever = retriever;
		this.log = log;
	}
	
	public GraphIndexBasedRetriever getWrappedRetriever() {
		return wrappedRetriever;
	}
	
	@Override
	public Stream<Integer> retrieve(DBInterface db, Stream<Integer> indices) {
		Set<Integer> indiciesRecord = new HashSet<>();
		indices = listen(indices, indiciesRecord::add);
		QueryRecord qr = new QueryRecord(indiciesRecord, db);
		log.recordQueryRecord(qr);
		return listen(wrappedRetriever.retrieve(db, indices), qr::recordRetrievedGraphID);
	}
	

}
