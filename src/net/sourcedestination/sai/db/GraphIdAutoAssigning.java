package net.sourcedestination.sai.db;

import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.graph.GraphSerializer;

public interface GraphIdAutoAssigning extends DBInterface {

    /** determines the next available surrogate id for a database.
     * If this is used consistently, it shouldn't take more than one call to getDatabaseSize()
     * @param db
     * @return
     */
    public static int getNextAvailableSurrogateId(DBInterface db) {
        int id = db.getDatabaseSize();
        if(db.retrieveGraph(id) != null) id++;
        return id;
    }

    public static GraphIdAutoAssigning makeDbAutoAssignGraphIds(DBInterface db,
                                                         Function2<DBInterface,Graph,Integer> gen) {

        // create wrapper for db that implements this interface
        class AutoIdAssiginingDB extends DBWrapper implements GraphIdAutoAssigning {

            public AutoIdAssiginingDB() {
                super(db);
            }

            public int generateGraphId(Graph g) {
                return gen.apply(db, g);
            }
        };

        return new AutoIdAssiginingDB();
    }

    public static GraphIdAutoAssigning addNaturalIdAssigner(DBInterface db) {
        return makeDbAutoAssignGraphIds(db, (db2, g) -> GraphSerializer.canonicalId(g));
    }

    public static GraphIdAutoAssigning addSurrogateIdAssigner(DBInterface db) {
        return makeDbAutoAssignGraphIds(db, (db2, g) -> getNextAvailableSurrogateId(db));
    }

    int generateGraphId(Graph g);

    default int addGraph(Graph g) {
        int graphID = generateGraphId(g);
        this.addGraph(graphID, g);
        return graphID;
    }
}
