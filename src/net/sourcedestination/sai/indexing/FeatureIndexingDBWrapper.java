package net.sourcedestination.sai.indexing;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.graph.Feature;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.ImmutableGraph;
import net.sourcedestination.sai.graph.MutableGraph;

public class FeatureIndexingDBWrapper extends DBWrapper {
	private final FeatureIndexGenerator gen;
	
	public FeatureIndexingDBWrapper(DBInterface wrappedDB, FeatureIndexGenerator gen) {
		super(wrappedDB);
		this.gen = gen;
	}
	
	@Override
	public int addGraph(Graph g) {
		MutableGraph g1 = new MutableGraph(g);
		for(Feature f : gen.apply(g))
			g1.addFeature(f);
			
		return getWrappedDB().addGraph(new ImmutableGraph(g1));
	}

}
