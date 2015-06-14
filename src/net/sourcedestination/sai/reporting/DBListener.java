package net.sourcedestination.sai.reporting;

import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.MutableGraph;
import static net.sourcedestination.sai.util.StreamUtil.listen;

/** a wrapper for a DBInterface which records statistics about all queries made to the
 * wrapped database. Note that reporting is handled in a thread-safe manner, so it is possible 
 * that reporting will effect the performance of a parallel retrieval. 
 * 
 * @author jmorwick
 *
 */
public class DBListener extends DBWrapper {
	
	private final Log log;

	public DBListener(DBInterface wrappedDB, Log log) {
		super(wrappedDB);
		this.log = log;
	}

	@Override
	public <G extends Graph> G retrieveGraph(int graphID, GraphFactory<G> f) {
		QueryRecord qr = new QueryRecord(graphID, getWrappedDB());
		G result = getWrappedDB().retrieveGraph(graphID, f);
		if(result != null) 
			qr.recordRetrievedGraphID(graphID);
		log.recordQueryRecord(qr);
		return result;
	}

	@Override
	public int addGraph(Graph g) {
		int result = getWrappedDB().addGraph(g);
		log.recordAddition(getWrappedDB(), result, g);
		return result;
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeature(Feature f) {
		QueryRecord qr = new QueryRecord(f, getWrappedDB());
		Stream<Integer> s = getWrappedDB().retrieveGraphsWithFeature(f);
		s = listen(s, qr::recordRetrievedGraphID);
		log.recordQueryRecord(qr);
		return s;
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeatureName(String name) {
		QueryRecord qr = new QueryRecord(name, getWrappedDB());
		Stream<Integer> s = getWrappedDB().retrieveGraphsWithFeatureName(name);
		s = listen(s, qr::recordRetrievedGraphID);
		log.recordQueryRecord(qr);
		return s;
	}

	@Override
	public void deleteGraph(int graphID) {
		log.recordDeletion(getWrappedDB(), graphID, 
				getWrappedDB().retrieveGraph(graphID, MutableGraph::new));
		getWrappedDB().deleteGraph(graphID);
	}
}
