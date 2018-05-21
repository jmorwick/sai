package net.sourcedestination.sai.db.indexing;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.db.graph.Feature;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.ImmutableGraph;
import net.sourcedestination.sai.db.graph.MutableGraph;

public class FeatureIndexingDBWrapper extends DBWrapper {
	private final FeatureIndexGenerator gen;
	
	public FeatureIndexingDBWrapper(DBInterface wrappedDB, FeatureIndexGenerator gen) {
		super(wrappedDB);
		this.gen = gen;
	}
	
	@Override
	public int addGraph(Graph g) {
		var g1 = new MutableGraph(g);
		for(Feature f : gen.apply(g))
			g1.addFeature(f);
			
		return getWrappedDB().addGraph(new ImmutableGraph(g1));
	}

}
