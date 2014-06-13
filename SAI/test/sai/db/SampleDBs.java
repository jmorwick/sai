package sai.db;

import java.nio.file.AccessDeniedException;

import sai.db.BasicDBInterface;
import sai.db.DBInterface;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.graph.SampleGraphs;

public class SampleDBs {
	public static BasicDBInterface getEmptyDB(GraphFactory<?> gf) {
		return new BasicDBInterface(gf);
	}

	public static BasicDBInterface smallGraphsDB(GraphFactory<?> gf) {
		BasicDBInterface db = getEmptyDB(gf);
		db.addGraph(SampleGraphs.getSmallGraph1());
		db.addGraph(SampleGraphs.getSmallGraph2());
		db.addGraph(SampleGraphs.getSmallGraph3());
		db.addGraph(SampleGraphs.getSmallGraph4());
		return db;
	}
	

	public static BasicDBInterface smallGraphsDBWithCorrectIndices(GraphFactory<?> gf) throws AccessDeniedException {
		BasicDBInterface db = getEmptyDB(gf);
		db.connect();
		int g1 = db.addGraph(SampleGraphs.getSmallGraph1());
		int g2 = db.addGraph(SampleGraphs.getSmallGraph2());
		int g3 = db.addGraph(SampleGraphs.getSmallGraph3());
		int g4 = db.addGraph(SampleGraphs.getSmallGraph4());
		
		int ab = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
		int ad = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "d", "a"));
		int bc = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a"));
		int bd = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "d", "a"));
		int cd = db.addGraph(SampleGraphs.getOneEdgeIndex("c", "d", "a"));

		db.addIndex(g1, ab); //5
		db.addIndex(g1, bc); //7
		db.addIndex(g1, cd); //9
		db.addIndex(g1, ad); //6
		
		db.addIndex(g2, ab); //5
		db.addIndex(g2, bc); //7
		db.addIndex(g2, ad); //6
		db.addIndex(g2, bd); //8

		db.addIndex(g3, ab); //5
		
		db.addIndex(g4, ab); //5
		db.addIndex(g4, ad); //6
		return db;
	}
}
