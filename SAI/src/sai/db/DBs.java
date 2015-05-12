package sai.db;

import sai.graph.MutableGraph;

public class DBs {
    
  public static void copyDBs(DBInterface fromDB, DBInterface toDB) {
	 fromDB.getGraphIDStream().forEach(
			 id -> toDB.addGraph(fromDB.retrieveGraph(id, MutableGraph::new)));
  }
}
