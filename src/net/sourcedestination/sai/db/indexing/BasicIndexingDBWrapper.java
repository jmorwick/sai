package net.sourcedestination.sai.db.indexing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.DBWrapper;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.db.graph.ImmutableGraph;

import java.util.stream.Stream;

public class BasicIndexingDBWrapper<I>  extends DBWrapper implements Index<I> {

    private final Multimap<I,Integer> internalIndexImplementation = HashMultimap.create();
    private final IndexGenerator<I> gen;


	public BasicIndexingDBWrapper(DBInterface wrappedDB, IndexGenerator<I> gen) {
		super(wrappedDB);
		this.gen = gen;
	}
	
	@Override
	public void addGraph(int graphId, Graph g) {
	    gen.apply(g).forEach( i ->        // generate / load indexes
                internalIndexImplementation.put(i,graphId));
		getWrappedDB().addGraph(graphId, new ImmutableGraph(g)); // call super method
	}

    @Override
    public Stream<Integer> getRelatedGraphIds(I index) {
        return internalIndexImplementation.get(index).stream();
    }

}
