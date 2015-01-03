package sai.db;

import sai.SAIUtil;
import sai.graph.GraphFactory;
import sai.graph.MutableGraph;

public class DBs {
    
 public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
		GraphFactory<MutableGraph> gf = MutableGraph.getFactory();
		for(int graphID : SAIUtil.iteratorToCollection(fromDB.getGraphIDIterator()))
			toDB.addGraph(fromDB.retrieveGraph(graphID, gf));
 }
}
