package net.sourcedestination.sai.reporting;

import java.util.stream.Stream;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphFactory;
import net.sourcedestination.sai.graph.MutableGraph;
import static net.sourcedestination.sai.util.StreamUtil.listen;

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
		listen(s, qr::recordRetrievedGraphID);
		log.recordQueryRecord(qr);
		return s;
	}

	@Override
	public Stream<Integer> retrieveGraphsWithFeatureName(String name) {
		QueryRecord qr = new QueryRecord(name, getWrappedDB());
		Stream<Integer> s = getWrappedDB().retrieveGraphsWithFeatureName(name);
		listen(s, qr::recordRetrievedGraphID);
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
