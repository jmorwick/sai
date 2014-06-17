package sai.indexing;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import sai.db.BasicDBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.MutableGraph;
import sai.retrieval.GraphRetriever;

public class BasicPath1IndexRetriever implements GraphRetriever<BasicDBInterface> {

	private IndexGenerator<Graph> gen;
	
	public BasicPath1IndexRetriever(String ... featureNames) {
		gen = new Path1IndexGenerator<Graph>(featureNames);
	}
	
	@Override
	public Iterator<Integer> retrieve(BasicDBInterface db, Graph q) {
		Set<Integer> indexes = Sets.newHashSet();
		for(Graph i : gen.generateIndices(db, MutableGraph.getFactory(), q))
			indexes.add(db.addGraph(i));
		return indexes.iterator();
	}

}
