package net.sourcedestination.sai.db;

import java.nio.file.AccessDeniedException;

import net.sourcedestination.sai.db.BasicDBInterface;
import net.sourcedestination.sai.graph.SampleGraphs;

public class SampleDBs {
    public static BasicDBInterface getEmptyDB() {
        return new BasicDBInterface();
    }

    public static BasicDBInterface smallGraphsDB() {
        BasicDBInterface db = getEmptyDB();
        db.addGraph(SampleGraphs.getSmallGraph1());
        db.addGraph(SampleGraphs.getSmallGraph2());
        db.addGraph(SampleGraphs.getSmallGraph3());
        db.addGraph(SampleGraphs.getSmallGraph4());
        db.addGraph(SampleGraphs.getSmallGraph5());
        db.addGraph(SampleGraphs.getSmallGraph6());
        db.addGraph(SampleGraphs.getSmallGraph7());
        db.addGraph(SampleGraphs.getSmallGraph8());
        db.addGraph(SampleGraphs.getSmallGraph9());
        db.addGraph(SampleGraphs.getSmallGraph10());
        db.addGraph(SampleGraphs.getSmallGraph11());
        return db;
    }


    public static BasicDBInterface smallGraphsDBWithCorrectIndices() {
        BasicDBInterface db = getEmptyDB();
        int g1 = db.addGraph(SampleGraphs.getSmallGraph1()); //1
        int g2 = db.addGraph(SampleGraphs.getSmallGraph2()); //2
        int g3 = db.addGraph(SampleGraphs.getSmallGraph3()); //3
        int g4 = db.addGraph(SampleGraphs.getSmallGraph4()); //4

        int ab = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a")); // 5
        int ad = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "d", "a")); // 6
        int bc = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a")); // 7
        int bd = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "d", "a")); // 8
        int cd = db.addGraph(SampleGraphs.getOneEdgeIndex("c", "d", "a")); // 9

        db.addIndex(g1, ab); //5
        db.addIndex(g1, bc); //7
        db.addIndex(g1, bd); //8
        db.addIndex(g1, cd); //9

        db.addIndex(g2, ab); //5
        db.addIndex(g2, ad); //6
        db.addIndex(g2, bc); //7
        db.addIndex(g2, bd); //8

        db.addIndex(g3, ab); //5

        db.addIndex(g4, ab); //5
        db.addIndex(g4, ad); //6
        return db;
    }

    public static BasicDBInterface smallGraphsDBWithIncorrectIndices() {
        BasicDBInterface db = getEmptyDB();
        int g1 = db.addGraph(SampleGraphs.getSmallGraph1());
        int g2 = db.addGraph(SampleGraphs.getSmallGraph2());
        int g3 = db.addGraph(SampleGraphs.getSmallGraph3());
        int g4 = db.addGraph(SampleGraphs.getSmallGraph4());

        int ab = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "b", "a"));
        int ad = db.addGraph(SampleGraphs.getOneEdgeIndex("a", "d", "a"));
        int bc = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "c", "a"));
        int bd = db.addGraph(SampleGraphs.getOneEdgeIndex("b", "d", "a"));
        int cd = db.addGraph(SampleGraphs.getOneEdgeIndex("c", "d", "a"));

        db.addIndex(g4, ab); //5
        db.addIndex(g4, bc); //7
        db.addIndex(g4, cd); //9
        db.addIndex(g4, bd); //6

        db.addIndex(g3, ab); //5
        db.addIndex(g3, bc); //7
        db.addIndex(g3, ad); //6
        db.addIndex(g3, bd); //8

        db.addIndex(g2, ab); //5

        db.addIndex(g1, ab); //5
        db.addIndex(g1, ad); //6
        return db;
    }
}
