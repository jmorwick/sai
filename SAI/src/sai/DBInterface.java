/* Copyright 2011-2013 Joseph Kendall-Morwick

This file is part of SAI: The Structure Access Interface.

SAI is free software: you can redistribute it and/or modify
it under the terms of the Lesser GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SAI is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
Lesser GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License
along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */
package sai;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;
import info.kendall_morwick.funcles.T2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import sai.comparison.featuresetcomparators.ManyTo1;
import sai.indexing.Index;
import sai.indexing.IndexGenerator;
import sai.indexing.IndexRetriever;

/**
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
public class DBInterface {

	
	private Map<Function,Cache> caches = Maps.newHashMap();
	private void clearCache(Function f) {
		if(caches.containsKey(f)) {
			caches.get(f).invalidateAll();
		}
	}
	private void clearCache(Function f, Object o) {
		if(caches.containsKey(f)) {
			caches.get(f).invalidate(o);
		}
	}
	
	
    //-------------------------  Basic Initialization --------------------------
    private static boolean driverLoaded = false;
    private DBInterface self = this; //used inside closures
    private BinaryRelation<Set<? extends Feature>> fsc; //feature-set comparator
    boolean directed = true;

    public DBInterface(String DBHost,
            String DBName,
            String DBUsername,
            String DBPassword,
            Map<String, Function<T2<Integer, String>,Feature>> featureFactories) {
        this(DBHost, DBName, DBUsername, DBPassword);
        this.featureFactories = featureFactories;
        if (this.featureFactories == null) {
            this.featureFactories = new HashMap<String, Function<T2<Integer, String>,Feature>>();
        }

    }

    public DBInterface(String DBHost,
            String DBName,
            String DBUsername,
            String DBPassword) {
        this.DBHost = DBHost;
        this.DBName = DBName;
        this.DBUsername = DBUsername;
        this.DBPassword = DBPassword;
        this.fsc = new ManyTo1();

        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        this.retrievalConnection = getConnection();
        if(retrievalConnection == null) {
            throw new IllegalStateException("Failed to connect to database");
        }
        try {
            this.retrievalStatement = retrievalConnection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //TODO: set up caching
    }
    //----------------  SQL Database related methods ---------------------------
    private String DBHost;
    private String DBName;
    private String DBUsername;
    private String DBPassword;
    private Map<String, Function<T2<Integer, String>, Feature>> featureFactories =
            new HashMap<String, Function<T2<Integer, String>,Feature>>();
    private Set<String> registeredFeatureClasses = new HashSet<String>();
    private String extensionSQL = "";
    private Statement retrievalStatement;
    private Connection retrievalConnection;

    /** returns true if the database connection is alive */
    public boolean testConnection() {
        Connection c = getConnection();
        releaseConnection(c);
        return c != null;
    }

    /** registers a feature class with this database instance.  If any special
     * database initialization operations are required by this Feature class,
     * they are performed now by executing SQL statements returned by the
     * Feature class' getInitSQL() method.  This is done only the first
     * time a Feature class is registered.
     * @param t
     */
    public void registerFeatureClass(Feature t) {
        if (registeredFeatureClasses.contains(t.getClass().getName())) {
            return;
        }
        //the feature has not been used since this DBInterface was created.
        registeredFeatureClasses.add(t.getClass().getName());  //note that it now has been used
        List<Map<String, String>> rs = getQueryResults("SELECT * FROM feature_instances WHERE featureclass like '" + t.getClass().getName() + "' LIMIT 1;");
        if (rs.size() == 0) { //if this feature is not present in the database, initialize its extra tables
            for (String statement : t.getInitSQL().split(";")) {
                if (!statement.trim().equals("")) {
                    updateDB(statement);
                }
            }
        }
    }

    /** creates and returns a database connection.  This method can be
     * over-ridden to provide connection pooling capabilities.
     * @return
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://"
                    + DBHost + "/" + DBName, DBUsername, DBPassword);
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return null;
    }

    /** closes a connection.  This method can be over-ridden to provide
     * connection pooling capabilities. 
     * @param connection
     */
    public void releaseConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized List<Map<String, String>> queryDB(String sql, boolean update) {
        List<Map<String, String>> ls = new ArrayList<Map<String, String>>();
        try {
            if (update) {
                retrievalStatement.executeUpdate(sql);
            } else {
                ResultSet rs = retrievalStatement.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    Map<String, String> m = new HashMap<String, String>();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        m.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                    ls.add(m);
                }
            }
        } catch (SQLException ex) {
            System.err.println(sql);
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ls;
    }

    /** executes a UPDATE SQL command on the persistent connection
     * 
     * @param sql
     */
    public void updateDB(String sql) {
        queryDB(sql, true);
    }

    /** executes a SELECT SQL statement on the interface's persitent connection
     * and returns a list of the result rows as maps from field name to value.
     * @param sql
     * @param update
     * @return
     */
    public List<Map<String, String>> getQueryResults(String sql) {
        return queryDB(sql, false);
    }

    /** clears any structures in the database and creates the basic tables for SAI
     * 
     */
    public void initializeDatabase() {
        try {
            String schemaSQL;
            schemaSQL = slurpStream(DBInterface.class.getResourceAsStream("/resources/sai-base.sql"));
            for (String statement : schemaSQL.split(";")) {
                if (!statement.trim().equals("")) {
                    updateDB(statement + ";");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** returns the last AUTO_INCREMENT value from the last INSERT SQL statement
     *
     * @return
     */
    public int getLastAutoIncrement() {
        try {
            int newID = 0;
            ResultSet rs = this.retrievalStatement.getGeneratedKeys();
            if (rs.next()) {
                newID = rs.getInt(1);
            } else {
                throw new RuntimeException("Error creating new graph in database");
            }
            return newID;
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new RuntimeException("Error creating new graph in database");
    }
    //---------------------  Caching methods and fields -----------------------
    private Set<Integer> ignoreIDs = new HashSet<Integer>();

    /** resets the cache of the method with the given name.
     * 
     * @param string
     */
    public void resetCache(String method) {
        if (method.equals("getFeatureName")) {
        	clearCache(getFeatureName);
        }
        if (method.equals("getFeatureID")) {
        	clearCache(getFeatureID);
        }
        if (method.equals("getFeatureInstances")) {
        	clearCache(getFeatureInstances);
        }
        if (method.equals("loadStructureFromDatabase")) {
        	clearCache(loadStructureFromDatabase);
        }
        if (method.equals("loadFeature")) {
        	clearCache(loadFeature);
        }
    }

    /** don't include the identified structure in query results and statistics */
    public void ignoreStructure(int id) {
        ignoreIDs.add(id);
        clearCache(getFeatureInstances);
    }

    /** once again include the identified structure in query results and statistics */
    public void unignoreStructure(int id) {
        ignoreIDs.remove(id);
        clearCache(getFeatureInstances);
    }

    /** returns id's of structures currently being ignored */
    public Set<Integer> getIgnoredIDs() {
        return Sets.newHashSet(ignoreIDs);
    }

    /*
    public void resetAllFeaturesCache() {
    getAllFeatures.clearCache();
    getAllFeaturesCache = null;
    }
     */
    //-------------------------- Basic loading/saving methods ------------------
    private String addSlashes(String tagName) {
        StringBuffer ret = new StringBuffer();
        for (String s : tagName.split("'")) {
            if (ret.length() > 0) {
                ret.append("\\'");
            }
            ret.append(s);
        }
        return ret.toString();
    }
    //create a function object so return values can be cached
    private Function<Integer,Feature> loadFeature = new Function<Integer,Feature>() {

        @Override
        public Feature apply(Integer ido) {
            int id = ido.intValue();
            String featureclass = "";
            Constructor<Feature> loadFeature;
            Feature t = null;
            List<Map<String, String>> rs =
                    getQueryResults("SELECT featureclass FROM feature_instances WHERE id = " + id);
            if (rs.size() > 0) {
                featureclass = rs.get(0).get("featureclass");
            } else {
                throw new IllegalArgumentException("No feature instance with id: " + id);
            }

            if (featureFactories.containsKey(featureclass)) {
                t = Funcles.apply(featureFactories.get(featureclass), id, "");
            } else {
                try {
                    loadFeature = (Constructor<Feature>) Class.forName(featureclass).getConstructor(java.lang.Integer.TYPE, DBInterface.class);
                    t = loadFeature.newInstance(id, self);
                } catch (IllegalAccessException ex) {
                    System.err.println(ex);
                    throw new RuntimeException("Factory method in " + featureclass + " does not conform to standard");
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Factory method in " + featureclass + " does not conform to standard");
                } catch (InvocationTargetException ex) {
                    System.err.println(ex.getCause());
                    ex.getTargetException().printStackTrace(System.err);
                    throw new RuntimeException("Factory method in " + featureclass + " threw an exception");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    //exception is thrown later
                } catch (ClassCastException ex) {
                    System.err.println(ex);
                    throw new RuntimeException("Factory method in " + featureclass + " does not conform to standard");
                } catch (InstantiationException ex) {
                    System.err.println(ex);
                    throw new RuntimeException("No stanrdard constructor for feature:  " + featureclass);
                }
            }
            if (t == null) {
                throw new RuntimeException("no factory to produce tag '" + featureclass + "'");
            }

            return t;
        }
    };

    /** retrieves a Feature insance with the indicated feature ID */
    public Feature loadFeature(int id) {
        return loadFeature.apply(id);
    }

    /** returns an iterator which iterates through every structure in the database */
    public Iterator<Graph> getStructureIterator() {
        return new Iterator<Graph>() {

            private int id = 0;

            public boolean hasNext() {
                String sql = "SELECT id FROM graph_instances WHERE is_index = FALSE AND id > " + id + " ORDER BY id LIMIT 1";
                return getQueryResults(sql).size() > 0;
            }

            public Graph next() {
                String sql = "SELECT id FROM graph_instances WHERE is_index = FALSE AND id > " + id + " ORDER BY id LIMIT 1";
                List<Map<String, String>> ls =
                        getQueryResults(sql);
                if (ls.size() > 0) {
                    id = Integer.parseInt(ls.get(0).get("id"));
                    return loadStructureFromDatabase(id);
                }
                return null;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
    private Function<Integer,Graph> loadStructureFromDatabase = new Function<Integer,Graph>() {

        @Override
        public Graph apply(Integer id) {
            //insure graph exists in the database
            List<Map<String, String>> rs = getQueryResults("SELECT * FROM graph_instances WHERE id = " + id);
            if (rs.size() == 0) {
                throw new IllegalArgumentException("No such graph with id=" + id);
            }

            boolean index = rs.get(0).get("is_index").equals("1");
            return index ? new Index(self, id) : new Graph(self, id);
        }
    };

    public Graph loadStructureFromDatabase(int id) {
        return (Graph) loadStructureFromDatabase.apply(id);
    }

    /** deletes the structure with the indicated id from the database */
    public void removeStructureFromDatabase(int id) {
        removeStructureFromDatabase(
                loadStructureFromDatabase(id));
        clearCache(loadStructureFromDatabase, id);
    }

    /** removes the supplied structure from the database. */
    public void removeStructureFromDatabase(Graph s) {
        int id = s.getID();
        updateDB("DELETE FROM graph_instances WHERE id = " + id);
        updateDB("DELETE FROM node_instances WHERE graph_id = " + id);
        updateDB("DELETE FROM edge_instances WHERE graph_id = " + id);
        updateDB("DELETE FROM graph_features WHERE graph_id = " + id);
        updateDB("DELETE FROM graph_indices WHERE graph_id = " + id);
        updateDB("DELETE FROM graph_indices WHERE index_id = " + id);
        for (Node n : s.vertexSet()) {
            updateDB("DELETE FROM node_features WHERE graph_id = " + id + 
                    " AND node_id = " + n.getID());
        }
        for (Edge e : s.edgeSet()) {
            updateDB("DELETE FROM edge_features WHERE graph_id = " + id + 
                    " AND edge_id = " + e.getID());
        }

    }
    private Function<T2<String, Integer>,String> getFeatureName =
            new Function<T2<String, Integer>,String>() {

                @Override
                public String apply(T2<String, Integer> args) {
                    String className = args.a1();
                    int id = args.a2();
                    String name = null;
                    String sql = "SELECT * FROM feature_instances WHERE "
                            + "id = '" + id + "' " + "AND "
                            + "featureclass LIKE '" + className + "';";
                    List<Map<String, String>> rs =
                            getQueryResults(sql);
                    if (rs.size() > 0) {
                        name = rs.get(0).get("name");
                    } else {
                        throw new RuntimeException("Feature " + id + " does not exist in DB");
                    }
                    return name;
                }
            };

    /** returns the textual 'name' of the feature with the specified id */
    public String getFeatureName(String className, int id) {
        return Funcles.apply(getFeatureName, className, id);
    }

    /** returns an existing database ID for this tag, or creates one if needed
     *
     * @param className Name of the tag class
     * @param tagName Name of this tag
     * @return id used for this tag in the database
     */
    public int getFeatureID(String className, String tagName) {
        return Funcles.apply(getFeatureID, className, tagName);
    }
    private Function<T2<String, String>,Integer> getFeatureID =
            new Function<T2<String, String>,Integer>() {

                @Override
                public Integer apply(T2<String, String> args) {
                    String className = args.a1();
                    String tagName = args.a2();
                    int tagID = -1;
                    Statement stmt;
                    List<Map<String, String>> rs =
                            getQueryResults("SELECT * FROM feature_instances WHERE "
                            + "name LIKE '" + addSlashes(tagName) + "' " + "AND "
                            + "featureclass LIKE '" + className + "';");
                    if (rs.size() > 0) {
                        tagID = Integer.parseInt(rs.get(0).get("id"));
                    } else {      //no existing tag, make a new record
                        updateDB("INSERT INTO feature_instances VALUES (NULL, '"
                                + tagName + "', '" + className + "');");
                        ResultSet rs2;
                        try {
                            rs2 = retrievalStatement.getGeneratedKeys();
                            if (rs2.next()) {
                                tagID = rs2.getInt(1);
                            } else {
                                Logger.getLogger(DBInterface.class.getName()).log(
                                        Level.SEVERE, null, "could not retrieve auto inc");
                                System.exit(1);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    return tagID;
                }
            };

    /** returns true if a static 'is-a' relationship was recorded in the
     * database between the two provided features
     */
    public boolean isa(Feature f1, Feature f2) {
        return Funcles.apply(isa, f1, f2);
    }
    
    private BinaryRelation<Feature> isa = new BinaryRelation<Feature>() {

        @Override
        public boolean apply(Pair<Feature> p) {
            return self.getQueryResults("SELECT * FROM feature_isa_relationships WHERE parent_id = "
                    + p.a1().getID() + " AND feature_id = " + p.a2().getID()).size() > 0;
        }
    };

    /** returns the number of graph structures in the database */
    public int getDatabaseSize() {
        return Integer.parseInt(
                getQueryResults("SELECT COUNT(*) FROM graph_instances").get(0).get("COUNT(*)"))
                - ignoreIDs.size();
    }
    
    /** returns the id of the next structure after the indicated structure id, or -1 if none exists */
    public int getNextLegalStructureID(int id, boolean index) {
        List<Map<String, String>> ret = getQueryResults("SELECT id FROM graph_instances WHERE id > " + id + 
                                                        " AND is_index = " +(index ? "TRUE" : "FALSE"));
        if(ret.size() == 0) return -1;
        return Integer.parseInt(ret.get(0).get("id"));
    }
    

    /** returns the number of graph structures in the database */
    public int getDatabaseSizeNoIndices() {
        return Integer.parseInt(
                getQueryResults("SELECT COUNT(*) FROM graph_instances WHERE is_index = FALSE").get(0).get("COUNT(*)"))
                - ignoreIDs.size();
    }
    
    
    /** returns the number of nodes which are part of non-index graphs in the database */
    public double getNodesInDatabase() {
        return Integer.parseInt(
                getQueryResults("SELECT COUNT(*) FROM graph_instances gi, node_instances ni WHERE "+ 
                  "gi.id = ni.graph_id AND gi.is_index = FALSE").get(0).get("COUNT(*)"))
                - ignoreIDs.size();
    }
    
    /** returns the number of nodes which are part of non-index graphs in the database */
    public double getEdgesInDatabase() {
        return Integer.parseInt(
                getQueryResults("SELECT COUNT(*) FROM graph_instances gi, edge_instances ni WHERE "+ 
                  "gi.id = ni.graph_id AND gi.is_index = FALSE").get(0).get("COUNT(*)"))
                - ignoreIDs.size();
    }
    
    
    /** returns all instances of a particular feature class */
    public Set<Feature> getAllFeatures(Class<? extends Feature> featureclass) {
        return getAllFeatures.apply(featureclass.getCanonicalName());
    }
    private Function<String, Set<Feature>> getAllFeatures =
            new Function<String, Set<Feature>>() {

                @Override
                public Set<Feature> apply(String featureclass) {
                    Set<Feature> tags = new HashSet<Feature>();
                    String sql = "SELECT id FROM feature_instances WHERE featureclass LIKE '" + featureclass + "';";
                    for (Map<String, String> m : getQueryResults(sql)) {
                        tags.add(loadFeature(Integer.parseInt(m.get("id"))));
                    }
                    return tags;
                }
            };

    /** returns all instances of every feature class */
    public Set<Feature> getAllFeatures() {
        if (getAllFeaturesCache != null) {
            return getAllFeaturesCache;
        }
        Set<Feature> tags = new HashSet<Feature>();
        String sql = "SELECT id FROM feature_instances;";
        for (Map<String, String> m : this.getQueryResults(sql)) {
            tags.add(loadFeature(Integer.parseInt(m.get("id"))));
        }
        getAllFeaturesCache = tags;
        return tags;
    }
    private Set<Feature> getAllFeaturesCache = null;

    public Set<Integer> getGraphIDsWithFeature(Feature f) {
        Set<Integer> s = new HashSet<Integer>();
        for (Map<String, String> m :
                getQueryResults("SELECT gi.id FROM graph_instances gi, graph_features fi WHERE " + 
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getID())) {
            if(ignoreIDs.contains(Integer.parseInt(m.get("id")))) continue;
           s.add(Integer.parseInt(m.get("id")));
        }
        for (Map<String, String> m :
                getQueryResults("SELECT gi.id FROM graph_instances gi, edge_features fi WHERE " + 
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getID())) {
            if(ignoreIDs.contains(Integer.parseInt(m.get("id")))) continue;
           s.add(Integer.parseInt(m.get("id")));
        }
        for (Map<String, String> m :
                getQueryResults("SELECT gi.id FROM graph_instances gi, node_features fi WHERE " + 
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getID())) {
            if(ignoreIDs.contains(Integer.parseInt(m.get("id")))) continue;
           s.add(Integer.parseInt(m.get("id")));
        }
        
        return s;
    }
    
    public Set<Graph> getGraphsWithFeature(Feature f) {
        Set<Graph> s = new HashSet<Graph>();
        for(int id : getGraphIDsWithFeature(f)) 
            s.add(this.loadStructureFromDatabase(id));
        return s;
    }
    
    /** returns the number of instances of the feature 't' in all stored structures */
    public int getNumberOfFeatureInstances(Feature t) {
        return getFeatureInstances.apply(t);
    }
    private Function<Feature,Integer> getFeatureInstances =
            new Function<Feature,Integer>() {

                @Override
                public Integer apply(Feature t) {
                    String sql = "SELECT COUNT(node_id) FROM node_features WHERE feature_id = " + t.getID();
                    List<Map<String, String>> ls = getQueryResults(sql);
                    if (ls.size() > 0) {
                        return Integer.parseInt(ls.get(0).get("COUNT(node_id)"));
                    }

                    return 0;
                }
            };

    /** returns a set of all Feature classes used in this database */
    public Set<Class> getAllFeatureTypes() {
        Set<Class> retVals = new HashSet<Class>();
        for (Map<String, String> m :
                getQueryResults("SELECT featureclass FROM feature_instances GROUP BY featureclass")) {
            try {
                retVals.add(Class.forName(m.get("featureclass")));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retVals;
    }

    public void setFeatureSetComparator(BinaryRelation<Set<? extends Feature>> fsc) {
        this.fsc = fsc;
    }

    public boolean featureSetsCompatible(
            Set<? extends Feature> t1s,
            Set<? extends Feature> t2s) {
        return Funcles.apply(fsc, t1s, t2s);
    }
    //----------------------- Indexing -------------------------------------
    private Set<IndexGenerator> indexers = new HashSet<IndexGenerator>();
    private Set<IndexRetriever> retrievers = new HashSet<IndexRetriever>();

    /** adds an indexer for this interface.  whenever 'indexGraph' is called on a
     * graph 'g', all indexers added through this method will have their 'indexGraph'
     * methods called on g, and the resulting indices will be saved to the database.
     *
     * @param ig
     */
    public void addIndexer(IndexGenerator ig) {
        indexers.add(ig);
    }

    /** saves 'i' to the database (if necessary) and associates i with g */
    public void addIndex(Graph g, Index i) {
        if (i.getID() < 1) {
            i.saveToDatabase();
        }
        addIndex(g.getID(), i.getID());
    }

    /** associates the graph with id 'graphID' with the index with id 'indexID'
     */
    public void addIndex(int graphID, int indexID) {
        if (graphID < 1 || indexID < 1) {
            throw new IllegalArgumentException("Cannot add indices for graphs not saved to the database");
        }
        if (getQueryResults("SELECT * FROM graph_instances WHERE id = " + indexID + " AND is_index = TRUE").size() == 0) {
            return; //make sure the index id is indeed an index
        }
        if (getQueryResults("SELECT * FROM graph_instances WHERE id = " + graphID).size() == 0) {
            return; //make sure the graph id exists
        }
        if (getQueryResults("SELECT * FROM graph_indices WHERE index_id = " + indexID +
                " AND graph_id = " + graphID).size() > 0) {
            return; //make sure it's not already indexed
        }
        updateDB("INSERT INTO graph_indices VALUES (" + indexID + ", " + graphID + ", NULL)");
    }

    /** Adds an index retriever to this database.  Whenever findIndices is
     * called on a graph 'g', each retriever added through this method will
     * have its 'retrieveIndices' method called on g, and the resulting
     * indices will be returned together by findIndices.
     *
     * @param r
     */
    public void addRetriever(IndexRetriever r) {
        retrievers.add(r);
    }

    /** uses registered Retrievers to find indices that should be related to
     * the query graph.  The query graph need not be saved into the database and
     * this method will not save the query graph to the database.  
     * @param g
     * @return
     */
    public Set<Index> findIndices(Graph g) {
        Set<Index> indices = new HashSet<Index>();
        for (IndexRetriever r : retrievers) {
            indices.addAll(r.retrieveIndices(g));
        }
        return indices;
    }

    /** retrieves indices associated with the stored graph g.
     *
     * @param g a graph which already exists in the database
     * @return
     */
    public Set<Index> getIndices(Graph g) {
        return getIndices(g.getID());
    }

    /** retieves indices associated with the graph with id 'id'.
     *
     * @param id
     * @return
     */
    public Set<Index> getIndices(int id) {
        Set<Index> ret = new HashSet<Index>();
        if (id < 1) {
            throw new IllegalArgumentException("this method only retrieves indices for stored graphs.  Use 'findIndices' for graphs not yet stored.");
        }

        String sql = "SELECT index_id FROM graph_indices WHERE graph_id = " + id;
        for (Map<String, String> row : getQueryResults(sql)) {
            ret.add(new Index(this, Integer.parseInt(row.get("index_id"))));
        }
        return ret;
    }

    /** uses registerd Indexers to generate or locate indices which should be
     * associated with stored graph g.  Generated indices are saved to the
     * database.  
     * @param g
     */
    public void indexGraph(Graph g) {
        if (g.getID() < 1) {
            return; //only index graphs saved to the DB
        }
        for (IndexGenerator ig : indexers) {
            for (Index i : ig.generateIndices(g)) {
                if (i.getID() < 1) {
                    i.saveToDatabase();
                }
                addIndex(g, i);
            }
        }
    }

    /** returns whether or not the specified stored index is associated with
     * the specified stored graph.  
     * @param g
     * @param i
     * @return
     */
    public boolean indexedBy(Graph g, Index i) {
        return getQueryResults("SELECT * FROM graph_indices WHERE index_id = " + i.getID()
                + " AND graph_id = " + g.getID()).size() > 0;
    }

    /** returns an iterator which iterates over every index stored in the database.
     * 
     * @return
     */
    public Iterator<Index> getIndexIterator() {
        return new Iterator<Index>() {

            private int id = 0;

            public boolean hasNext() {
                String sql = "SELECT id FROM graph_instances WHERE is_index = TRUE AND id > " + id + " ORDER BY id LIMIT 1";
                return getQueryResults(sql).size() > 0;
            }

            public Index next() {
                String sql = "SELECT id FROM graph_instances WHERE  is_index = TRUE AND id > " + id + " ORDER BY id LIMIT 1";
                List<Map<String, String>> ls =
                        getQueryResults(sql);
                if (ls.size() > 0) {
                    id = Integer.parseInt(ls.get(0).get("id"));
                    return (Index) loadStructureFromDatabase(id);
                }
                return null;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    public void viewAsUndirectedGraphs() { directed = false; }
    public void viewAsDirectedGraphs() { directed = true; }
    public boolean directedGraphs() { return directed; }
    
    
    

    public static String slurpStream(InputStream in) throws IOException {
      StringBuffer out = new StringBuffer();
      byte[] b = new byte[4096];
      for (int n; (n = in.read(b)) != -1;) {
          out.append(new String(b, 0, n));
      }
      return out.toString();
    }
}