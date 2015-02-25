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
package sai.db.mysql;

import info.kendall_morwick.funcles.BinaryRelation;
import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.Pair;
import info.kendall_morwick.funcles.T2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
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

import sai.comparison.compatibility.ManyTo1;
import sai.db.DBInterface;
import sai.graph.BasicGraphFactory;
import sai.graph.Feature;
import sai.graph.Graph;
import sai.graph.GraphFactory;
import sai.test.graph.Edge;
import sai.test.graph.Node;

/**
 * @version 2.0
 * @author Joseph Kendall-Morwick
 */
public class MySQLDBInterface implements DBInterface {

	
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
	

    

    public static String slurpStream(InputStream in) throws IOException {
      StringBuffer out = new StringBuffer();
      byte[] b = new byte[4096];
      for (int n; (n = in.read(b)) != -1;) {
          out.append(new String(b, 0, n));
      }
      return out.toString();
    }
    
    
	
    //-------------------------  Basic Initialization --------------------------
    private static boolean driverLoaded = false;
    private MySQLDBInterface self = this; //used inside closures
    private BinaryRelation<Set<? extends Feature>> fsc; //feature-set comparator
    boolean directed = true;

    public MySQLDBInterface(String DBHost,
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

    public MySQLDBInterface(String DBHost,
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
            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
            InputStream schemaIn = MySQLDBInterface.class.getResourceAsStream("resources/sai-base.sql");
            if(schemaIn == null) 
            	schemaIn = new FileInputStream("resources/sai-base.sql");
            
            schemaSQL = slurpStream(schemaIn);
            for (String statement : schemaSQL.split(";")) {
                if (!statement.trim().equals("")) {
                    updateDB(statement + ";");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
                    loadFeature = (Constructor<Feature>) Class.forName(featureclass).getConstructor(java.lang.Integer.TYPE, MySQLDBInterface.class);
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
                    Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
                                Logger.getLogger(MySQLDBInterface.class.getName()).log(
                                        Level.SEVERE, null, "could not retrieve auto inc");
                                System.exit(1);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
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
                    + p.a1().getSaiID() + " AND feature_id = " + p.a2().getSaiID()).size() > 0;
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
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getSaiID())) {
            if(ignoreIDs.contains(Integer.parseInt(m.get("id")))) continue;
           s.add(Integer.parseInt(m.get("id")));
        }
        for (Map<String, String> m :
                getQueryResults("SELECT gi.id FROM graph_instances gi, edge_features fi WHERE " + 
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getSaiID())) {
            if(ignoreIDs.contains(Integer.parseInt(m.get("id")))) continue;
           s.add(Integer.parseInt(m.get("id")));
        }
        for (Map<String, String> m :
                getQueryResults("SELECT gi.id FROM graph_instances gi, node_features fi WHERE " + 
                " fi.graph_id = gi.id AND fi.feature_id = " + f.getSaiID())) {
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
                    String sql = "SELECT COUNT(node_id) FROM node_features WHERE feature_id = " + t.getSaiID();
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
                Logger.getLogger(MySQLDBInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retVals;
    }

    
    //---------------------------------------------------------------------------
    

    /** associates the graph with id 'graphID' with the index with id 'indexID'
     */
    @Override
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
    
	@Override
	public void connect() throws AccessDeniedException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void disconnect() throws AccessDeniedException,
			FileNotFoundException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <G extends sai.graph.Graph> G retrieveGraph(int graphID,
			GraphFactory<G> f) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Iterator<Integer> getGraphIDIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Integer> getHiddenGraphs() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void hideGraph(int graphID) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void unhideGraph(int graphID) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteGraph(int graphID) {
		Graph s = retrieveGraph(graphID, new BasicGraphFactory());
        updateDB("DELETE FROM graph_instances WHERE id = " + graphID);
        updateDB("DELETE FROM node_instances WHERE graph_id = " + graphID);
        updateDB("DELETE FROM edge_instances WHERE graph_id = " + graphID);
        updateDB("DELETE FROM graph_features WHERE graph_id = " + graphID);
        updateDB("DELETE FROM graph_indices WHERE graph_id = " + graphID);
        updateDB("DELETE FROM graph_indices WHERE index_id = " + graphID);
        for (Node n : s.getNodeIDs()) {
            updateDB("DELETE FROM node_features WHERE graph_id = " + graphID + 
                    " AND node_id = " + n.getSaiID());
        }
        for (Edge e : s.getEdgeIDs()) {
            updateDB("DELETE FROM edge_features WHERE graph_id = " + graphID + 
                    " AND edge_id = " + e.getSaiID());
        }
		
	}
	@Override
	public Iterator<Integer> getIndexIDIterator() {
        return new Iterator<Integer>() {

            private int id = 0;

            public boolean hasNext() {
                String sql = "SELECT id FROM graph_instances WHERE is_index = TRUE AND id > " + id + " ORDER BY id LIMIT 1";
                return getQueryResults(sql).size() > 0;
            }

            public Integer next() {
                String sql = "SELECT id FROM graph_instances WHERE  is_index = TRUE AND id > " + id + " ORDER BY id LIMIT 1";
                List<Map<String, String>> ls =
                        getQueryResults(sql);
                if (ls.size() > 0) {
                    return Integer.parseInt(ls.get(0).get("id"));
                }
                return -1;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
	}
	
	@Override
	public Set<Integer> retrieveIndexIDs(int graphID) {
        Set<Integer> ret = new HashSet<Integer>();

        String sql = "SELECT index_id FROM graph_indices WHERE graph_id = " + id;
        for (Map<String, String> row : getQueryResults(sql)) {
            ret.add(retrieveGraph(id));
        }
        return ret;
	}
	
	
	
	
	
	/////////////////////// new stuff
	
	
	
	
	@Override
	public Set<Integer> retrieveIndexedGraphIDs(int indexID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public sai.graph.Feature getFeature(int featureID) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<String> getFeatureNames() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Integer> getFeatureIDs() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<Integer> getFeatureIDs(String featureClass) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCompatible(sai.graph.Feature fa, sai.graph.Feature fb) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setNotCompatible(sai.graph.Feature fa, sai.graph.Feature fb) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isCompatible(sai.graph.Feature fa, sai.graph.Feature fb) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getDatabaseSizeWithoutIndices() {
        return Integer.parseInt(
                getQueryResults("SELECT COUNT(*) FROM graph_instances").get(0).get("COUNT(*)"))
                - ignoreIDs.size();
	}
	@Override
	public Iterator<Integer> retrieveGraphsWithFeature(Feature f) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Iterator<Integer> retrieveGraphsWithFeatureName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int addGraph(Graph g) {
		// TODO Auto-generated method stub
		return 0;
	}
}