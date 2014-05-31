package sai.retrieval;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;

public class RetrievalUtil {
	
	
	public static <G extends Graph> GraphRetriever<G> build2PhasedRetriever(
			final GraphRetriever<G> phase1,
			DBInterface db, 
    		GraphFactory<G> gf,
    		final Ordering<G> o,
    		final int window1,
    		final int window2) {
		return new GraphRetriever<G>() {

			@Override
			public Iterator<G> retrieve(DBInterface db, GraphFactory<G> gf,
					Set<Graph> indices) {
				final Set<G> graphs = Sets.newHashSet();
				Iterator<G> gi = phase1.retrieve(db, gf, indices);
				for(int i=0; i<window1; i++)
					graphs.add(gi.next());
				return o.greatestOf(graphs, window2).iterator();
			}
			
		};
	}
}
