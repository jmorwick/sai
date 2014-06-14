package sai.retrieval;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import sai.SAIUtil;
import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;

public class RetrievalUtil {
	
	public static GraphRetriever createPhase1Retriever(
			final GraphRetriever indexRetriever,
			final IndexBasedGraphRetriever ibRetriever
			) {
		return new GraphRetriever() {

			@Override
			public Iterator<Integer> retrieve(DBInterface db, Graph q) {
				Set<Integer> indices = Sets.newHashSet(indexRetriever.retrieve(db, q));
				System.out.println(indices);
				return ibRetriever.retrieve(db, indices);
			}
		};
	}
	
	public static <G extends Graph> Iterator<G> twoPhasedRetrieval(
			final GraphRetriever phase1,
			DBInterface db, 
    		GraphFactory<G> gf,
    		final Ordering<G> o,
    		G query,
    		final int window1,
    		final int window2) {
				final Set<G> graphs = Sets.newHashSet();
				Iterator<Integer> gi = phase1.retrieve(db, query);
				for(int i=0; i<window1; i++)
					graphs.add(db.retrieveGraph(gi.next(), gf));
				return o.greatestOf(graphs, window2).iterator();
			
	}
}
