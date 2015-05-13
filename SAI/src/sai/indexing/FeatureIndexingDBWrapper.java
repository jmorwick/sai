package sai.indexing;

import sai.db.DBInterface;
import sai.db.DBWrapper;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.ImmutableGraph;
import sai.graph.MutableGraph;

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
