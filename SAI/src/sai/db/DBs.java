package sai.db;

import sai.SAIUtil;
import sai.graph.GraphFactory;
import sai.graph.MutableGraph;
import sai.graph.MutableGraphFactory;

public class DBs {
    
 public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
		GraphFactory<MutableGraph> gf = new MutableGraphFactory();
		for(int graphID : SAIUtil.iteratorToCollection(fromDB.getGraphIDIterator()))
			toDB.addGraph(fromDB.retrieveGraph(graphID, gf));
 }
}
