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

import info.km.funcles.Funcles;
import info.km.funcles.T2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sai.indexing.Index;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
public class Graph
        extends DirectedMultigraph<Node, Edge> {

    private int DBID = -1;
    private Multiset<Index> indices = HashMultiset.<Index>create();
    final private DBInterface db;
    private Set<Feature> features = new HashSet<Feature>();

    private final Graph self = this;

    private Map<String, Node> alternateIDs = new HashMap<String, Node>();
    
    public  Graph(DBInterface db,
                  Feature ... tags) {
        super(Edge.class);
        this.db = db;
        for(Feature f : tags) this.features.add(f);
    }

    public Graph(DBInterface db, int id) {
        super(Edge.class);
            this.DBID = id;
            this.db = db;

            //insure graph exists in the database
            List<Map<String,String>> rs = db.getQueryResults("SELECT * FROM graph_instances WHERE id = "+id);
            if(rs.size() == 0) throw new IllegalArgumentException("No such graph with id="+id);

            boolean index = rs.get(0).get("is_index").equals("1");


            //load graph tags
            rs = db.getQueryResults("SELECT * FROM graph_features WHERE " +
                                   "graph_id = "+id);
            for(Map<String,String> rm : rs) {
                try {
                    addFeature(db.loadFeature(Integer.parseInt(rm.get("feature_id"))));
                } catch(NumberFormatException e) {
                    throw new IllegalStateException("corrupt data for graph #"+id + " data: " + rm);
                }
            }


            //load nodes
            rs = db.getQueryResults("SELECT * FROM node_instances WHERE " +
                                   "graph_id = "+id);

            for(Map<String,String> rm : rs) {
                int nid = Integer.parseInt(rm.get("id"));
                List<Map<String,String>> tagrs =
                        db.getQueryResults("SELECT * FROM node_features "+
                           "WHERE node_id = "+
                           nid + " AND " +
                           "graph_id = " + id);

                Node n = new Node(nid, this);

                for(Map<String,String> tagrm : tagrs)
                    n.addFeature(db.loadFeature(Integer.parseInt(tagrm.get("feature_id"))));

                addVertex(n);
            }

            //load edges
            rs = db.getQueryResults("SELECT * FROM edge_instances WHERE " +
                                   "graph_id = "+id);
            for(Map<String,String> rm : rs) {
                int eid = Integer.parseInt(rm.get("id"));
                List<Map<String,String>> tagrs =
                        db.getQueryResults("SELECT * FROM edge_features "+
                           "WHERE edge_id = "+eid + " AND " +
                           "graph_id = " + id);
                Set<Integer> tagIDs = new HashSet<Integer>();
                Edge e = new Edge(eid, this);

                for(Map<String,String> tagrm : tagrs)
                    e.addFeature(db.loadFeature(Integer.parseInt(tagrm.get("feature_id"))));

                addEdge(getNode(Integer.parseInt(rm.get("from_node_id"))),
                          getNode(Integer.parseInt(rm.get("to_node_id"))),
                          e);
            }

            //load indices
            rs = db.getQueryResults("SELECT * FROM  graph_indices WHERE graph_id = "+id);
            for(Map<String,String> rm : rs) {

            }
    }
    

    @Override
    public Edge addEdge(Node n1, Node n2) {
        return addEdge(n1, n2, new Feature[0]);
    }


    public Edge addEdge(Node n1, Node n2, Feature ... features) {
        Edge e = new Edge(this, db, features);
        addEdge(n1, n2, e);
        return e;
    }

    /** creates a new graph instance in the database for this graph, and sets
     * the ID to that of the newly created instance.  This is perforemd
     * <b>even if this graph already has a database ID</b>.
     */
    public void saveToDatabase() {
        synchronized(db) {
        db.updateDB("INSERT INTO graph_instances VALUES (NULL, "+
                vertexSet().size() + ", "+ edgeSet().size() + ", " +
                getFeatures().size() + ", " + 
                (this instanceof Index ? "TRUE" : "FALSE")+", FALSE);");
        DBID = db.getLastAutoIncrement();

        for(Feature t : getFeatures()) {
            db.updateDB("INSERT INTO graph_features VALUES (" +
                getID() + ", " + t.getID()+");");
        }
        for(Node n : vertexSet()) {

            db.updateDB("INSERT INTO node_instances VALUES (" +
                               n.getID() + ", " + getID()+", " + n.getFeatures().size()+");");
            for(Feature t : n.getFeatures()) {
                db.updateDB("INSERT INTO node_features VALUES (" +
                               getID() + ", " +
                               n.getID() + ", " +
                               t.getID()+");");

            }
        }
        for(Edge e : edgeSet()) {
            Node source = getEdgeSource(e);
            Node target = getEdgeTarget(e);
            db.updateDB("INSERT INTO edge_instances VALUES (" +
                               e.getID() + ", " +
                               getID() + ", "+
                               source.getID() + ", " +
                               target.getID() + ", " +
                               e.getFeatures().size()+");");
            for(Feature t : e.getFeatures()) {
                db.updateDB("INSERT INTO edge_features VALUES (" +
                               getID() + ", " +
                               e.getID() + ", " +
                               t.getID()+");");

            }
        }

        db.resetCache("getAllFeatures");
        }
    }

    public Set<Feature> getFeatures() {
        return Sets.newHashSet(features);
    }
    
/** sets a temporary ID for the specified Node.  This ID is not saved to the
 * database and setting this ID will not alter this Object's relationship
 * with a stored graph in the database.
 * 
 * @param n
 * @param id
 */
    public void setAlternateID(Node n, String id) {
        alternateIDs.put(id, n);
        n.setAlternateID(id);
    }

    public int getID() { return DBID; }

    public Node getNode(int id) {
        for(Node n : vertexSet()) {
            if(n.getID() == id) return n;
        }
        
        return null;
    }

    public Node getNode(String id) {
        return alternateIDs.get(id);
    }

    /** associate feature t with this graph.  t must not conflict with existing
     * features.  If t, or an equivalent feature, is already associated, no action is taken.
     * Otherwise, the feature is added and the ID associating this object with
     * a stored graph is cleared.
     * @param t
     */
    public void addFeature(Feature t) {
        int size = features.size();
        features.add(t);
        
        for(Feature f : features) {
        	
        	
            if(!f.canAccompany(Collections2.filter(features, Predicates.equalTo(f)))) {
                features.remove(t);
                throw new IllegalArgumentException("Added feature cannot accompany existing features");
            }
        }
    }

    /** all nodes with an edge leading from n to them
     * 
     * @param n
     * @return
     */
    public Set<Node> getLinkedFromNodes(Node n) {
        Set<Node> nodes = new HashSet<Node>();
        for(Edge e : edgeSet()) {
            if(getEdgeTarget(e) == n) {
                nodes.add(getEdgeSource(e));
            }
        }
        return nodes;
    }

    /** all nodes with an edge leading to this node
     * 
     * @param n
     * @return
     */
    public Set<Node> getLinkedToNodes(Node n) {
        Set<Node> nodes = new HashSet<Node>();
        for(Edge e : edgeSet()) {
            if(getEdgeSource(e) == n) {
                nodes.add(getEdgeTarget(e));
            }
        }
        return nodes;
    }

    /** returns all nodes connected to the given nodes, but not contained within
     * the given nodes. 
     * @param subStructure
     * @return
     */
    public Set<Node> getFringe(Set<Node> subStructure) {
        Set<Node> extended = new HashSet<Node>();
        for(Node n : subStructure) {
            extended.addAll(getLinkedFromNodes(n));
            extended.addAll(getLinkedToNodes(n));
        }
        return Sets.difference(extended, subStructure);
    }

    /** returns true if an edge links 'source' to 'target' */
    public boolean linkedTo(Node source, Node target) {
        for(Edge e : edgeSet()) {
            if(getEdgeSource(e) == source && getEdgeTarget(e) == target) {
                return true;
            }
        }
        return false;
    }


    /** returns true if an edge links 'n1' to 'n2' or vice versa.
     * 
     * @param n1
     * @param n2
     * @return
     */
    public boolean linkBetween(Node n1, Node n2) {
        return linkedTo(n1, n2) || linkedTo(n2, n1);
    }

    /** returns an edge ID which is not used in this graph
     *
     * @return
     */
    public int getUnusedEdgeID() {
        int id = 1;
        for(Edge edge : this.edgeSet()) {
            if(id <= edge.getID())
                id = edge.getID() + 1;
        }
        return id;
    }

    /** returns a node ID which is not used in this graph
     * 
     * @return
     */
    public int getUnusedNodeID() {
        int id = 1;
        for(Node node : this.vertexSet()) {
            if(id <= node.getID())
                id = node.getID() + 1;
        }
        return id;
    }


    @Override
    public Set<Node> vertexSet() { return new HashSet<Node>(super.vertexSet()); }

    @Override
    public Set<Edge> edgeSet() { return new HashSet<Edge>(super.edgeSet()); }

/** creates a copy of this graph.
 * 
 * @return
 */
    public Graph copy() {
        Graph t = new Graph(db);
        for(Node n : vertexSet()) {
            Node nn = new Node(n.getID(), t, db);
            for(Feature f : n.getFeatures())
                nn.addFeature(f);
            t.addVertex(nn);
        }
        for(Edge e : edgeSet()) {
            Edge ne = new Edge(e.getID(), t, db);
            for(Feature f : e.getFeatures())
                ne.addFeature(f);
            t.addEdge(t.getNode(getEdgeSource(e).getID()),
                      t.getNode(getEdgeTarget(e).getID()), ne);
        }
        for(Feature tag : getFeatures()) {
            t.addFeature(tag);
        }
        return t;
    }

    /** creates a copy of this graph without the specified node, or any edges
     * associated with the node
     *
     * @param node
     * @return
     */
    public Graph copyWithoutNode(Node node) {
        if(!vertexSet().contains(node))
            throw new IllegalArgumentException("Node " + node.getID() + " not in graph " + getID());
        Graph t = copy();
        t.removeVertex(t.getNode(node.getID()));
        return t;
    }

    /** returns a copy of this graph without the specified edge
     * 
     * @param edge
     * @return
     */
    public Graph copyWithoutEdge(Edge edge) {
        if(!edgeSet().contains(edge))
            throw new IllegalArgumentException("Edge " + edge.getID() + " not in graph " + getID());
        Graph t = copy();
        Edge ne = null;
        for(Edge e2 : t.getAllEdges(t.getNode(getEdgeSource(edge).getID()), 
                                    t.getNode(getEdgeTarget(edge).getID()))) {
            if(e2.getFeatures().equals(edge.getFeatures()))
                ne = e2;
        }
        if(ne == null) throw new IllegalStateException("ERROR: could not determine an equivalent feature set for deleted edge");
        t.removeEdge(ne);
        return t;
    }



    /** implements the Floyd-Warshall algorithm for finding shortest paths between all pairs of nodes.
        all distances between nodes conntected by an edge is assumed to be 1.0
     */
    public Map<T2<Node,Node>, Double> allPairsShortestPaths() {
        return allPairsShortestPaths(new Function<Edge,Double>() {

            @Override
            public Double apply(Edge args) {
                return 1.0;
            }
        });
    }

    /** implements the Floyd-Warshall algorithm for finding shortest paths between all pairs of nodes */
    public Map<T2<Node,Node>, Double> allPairsShortestPaths(Function<Edge,Double> edgeCost) {
        int maxid=0;
        for(Node n : vertexSet()) {
            maxid = Math.max(maxid, n.getID());
        }
        double[][] distance = new double[maxid+1][maxid+1];
        for(int i=0; i<=maxid; i++) {
            for(int j=0; j<=maxid; j++) {
                distance[i][j] = Double.POSITIVE_INFINITY;
                Node n1 = this.getNode(i);
                Node n2 = this.getNode(j);
                if(n1 == null || n2 == null) continue;
                if(this.linkedTo(n1, n2)) {
                    distance[i][j] = edgeCost.apply(
                    		Funcles.argmaxCollection(edgeCost, getAllEdges(n1, n2)));
                }
            }
        }

        for(int k=0; k<=maxid; k++) {
            for(int i=0; i<=maxid; i++) {
                for(int j=0; j<=maxid; j++) {
                    distance[i][j] = Math.min(distance[i][j],
                                              distance[i][k]+distance[i][j]);
                }
            }
        }
        Map<T2<Node,Node>, Double> m = new HashMap<T2<Node,Node>, Double>();
        for(Node n1 : Sets.newHashSet(vertexSet())) {
            for(Node n2: vertexSet()) {
                if(n1 == n2) m.put(T2.makeTuple(n1, n2), 0.0);
                else if(distance[n1.getID()][n2.getID()] < Double.POSITIVE_INFINITY)
                    m.put(T2.makeTuple(n1, n2), distance[n1.getID()][n2.getID()]);
            }
        }
        return m;
    }



    @Override
    public String toString() {
        String ret = "{S: "+features+"\n";
        ret += "  nodes:\n";
        for(Node n : vertexSet()) ret += "    "+n+"\n";
        ret += "  edges:\n";
        for(Edge e : edgeSet()) ret += "    "+e+"\n";
        return ret+"\n}";
    }

    /** returns whether or not this graph and the specified object are
     * both graphs associated with the same stored graph ID.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Graph) {
            return getID() > 0 && ((Graph)o).getID() == getID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.DBID;
        return hash;
    }

    public DBInterface getDB() {
        return db;
    }

    /** returns the feature of the given feature-class associated with this node.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, an IllegalArgumentException is thrown.
     * @param <F>
     * @param featureClass
     * @return
     */
    public <F extends Feature> F getFeature(Class<F> featureClass) {
        for(Feature f : getFeatures()) {
            if(featureClass.isInstance(f))
                return (F)f;
        }
        throw new IllegalArgumentException(
                "No feature with class "+featureClass+" in this node("+
                toString()+")");
    }
    
    public <F extends Feature> boolean containsFeatureType(Class<F> featureClass) {
        for(Feature f : getFeatures()) {
            if(featureClass.isInstance(f))
                return true;
        }
        return false;
    }
}