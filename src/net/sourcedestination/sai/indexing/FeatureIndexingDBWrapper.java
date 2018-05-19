package net.sourcedestination.sai.indexing;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.db.GraphIdAutoAssigning;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.graph.MutableGraph;

public class FeatureIndexingDBWrapper extends DBWrapper<GraphIdAutoAssigning> {
	private final FeatureIndexGenerator gen;
	
	public FeatureIndexingDBWrapper(GraphIdAutoAssigning wrappedDB, FeatureIndexGenerator gen) {
		super(wrappedDB);
		this.gen = gen;
	}

	public void addGraph(int graphId, Graph g) {
		var g1 = new MutableGraph(g);
		for(Feature f : gen.apply(g))
			g1.addFeature(f);
			
		getWrappedDB().addGraph(graphId, new ImmutableGraph(g1));
	}

}
