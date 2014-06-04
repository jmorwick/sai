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
package sai.graph.jgrapht;

import info.kendall_morwick.funcles.Funcles;
import info.kendall_morwick.funcles.T2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import db.mysql.MySQLDBInterface;

/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated
public class Graph
        extends DirectedMultigraph<Node, Edge> {

    private int DBID = -1;
    private Multiset<Index> indices = HashMultiset.<Index>create();
    final private MySQLDBInterface db;
    private Set<Feature> features = new HashSet<Feature>();

    private final Graph self = this;

    private Map<String, Node> alternateIDs = new HashMap<String, Node>();
    
    public  Graph(MySQLDBInterface db,
                  Feature ... tags) {
        super(Edge.class);
        this.db = db;
        for(Feature f : tags) this.features.add(f);
    }

    public Graph(MySQLDBInterface db, int id) {
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
                getSaiID() + ", " + t.getSaiID()+");");
        }
        for(Node n : vertexSet()) {

            db.updateDB("INSERT INTO node_instances VALUES (" +
                               n.getSaiID() + ", " + getSaiID()+", " + n.getFeatures().size()+");");
            for(Feature t : n.getFeatures()) {
                db.updateDB("INSERT INTO node_features VALUES (" +
                               getSaiID() + ", " +
                               n.getSaiID() + ", " +
                               t.getSaiID()+");");

            }
        }
        for(Edge e : edgeSet()) {
            Node source = getEdgeSourceNodeID(e);
            Node target = getEdgeTargetNodeID(e);
            db.updateDB("INSERT INTO edge_instances VALUES (" +
                               e.getSaiID() + ", " +
                               getSaiID() + ", "+
                               source.getSaiID() + ", " +
                               target.getSaiID() + ", " +
                               e.getFeatures().size()+");");
            for(Feature t : e.getFeatures()) {
                db.updateDB("INSERT INTO edge_features VALUES (" +
                               getSaiID() + ", " +
                               e.getSaiID() + ", " +
                               t.getSaiID()+");");

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


 
}