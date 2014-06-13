package sai.indexing;

import java.util.Set;

import com.google.common.collect.Sets;

import sai.db.BasicDBInterface;
import sai.graph.BasicGraphFactory;
import sai.graph.BasicGraphWrapper;
import sai.graph.Graph;
import sai.graph.GraphFactory;

public class BasicPath1Retriever implements IndexRetriever<BasicDBInterface> {

	private IndexGenerator<Graph> gen = new Path1<Graph>();
	private GraphFactory<BasicGraphWrapper> gf = new BasicGraphFactory();
	
	@Override
	public Set<Integer> retrieveIndices(BasicDBInterface db, Graph q) {
		Set<Integer> indexes = Sets.newHashSet();
		for(Graph i : gen.generateIndices(db, gf, q))
			indexes.add(db.addGraph(i));
		return indexes;
	}

}
