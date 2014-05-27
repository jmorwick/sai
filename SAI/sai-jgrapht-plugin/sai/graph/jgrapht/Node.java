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

import java.util.HashSet;
import java.util.Set;

import db.mysql.MySQLDBInterface;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.base.Predicates.equalTo;


/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated
public class Node implements Comparable {
    private int id;
    private Set<Feature> features = new HashSet<Feature>();
    private Graph parent;
    private String alternateID;

    public Node(Graph parent,
                         MySQLDBInterface db,
                         Feature ... features) {
        this(parent, features);
    }

    public Node(Graph parent, Feature ... features) {
        this.parent = parent;
        id = parent.getUnusedNodeID();
        for(Feature t : features) this.features.add(t);
        parent.addVertex(this);
    }

    public Node(int id, Graph parent, Feature ... features) {
        this(parent, features);
        this.id = id;
    }

    public Node(int id,
                         Graph parent,
                         MySQLDBInterface db,
                         Feature ... features) {
        this(parent, db, features);
        this.id = id;
    }

   /** adds a Feature to this Node.  This results in the loss of the
    * graph's database ID.
    * @param tag
    */
    public void addFeature(Feature tag) {
        features.add(tag);
        for(Feature f : features) {
            if(!f.canAccompany(filter(features, equalTo(f))))
                throw new IllegalArgumentException("Added feature cannot accompany existing features");
        }
    }

   /** removes a Feature from this Node.  This results in the loss of the
    * graph's database ID.  
    * @param tag
    */
    public void removeFeature(Feature tag) {
        features.remove(tag);
    }

    /** returns the database ID associated with this Node.  This is unique
     * within the parent graph, but is not gobally unique.  
     * @return
     */
    public int getID() {
        return id;
    }

    /** returns all features associated with this node */
    public Set<Feature> getFeatures() {
        return newHashSet(features);
    }

    /** returns the feature of the given feature-class associated with this node.
     * If more than one such feature exists, an arbitrary selection is returned.
     * If no such feature exists, null is returned
     * @param <F>
     * @param featureClass
     * @return
     */
    public <F extends Feature> F getFeature(Class<F> featureClass) {
        for(Feature f : getFeatures()) {
            if(featureClass.isInstance(f))
                return (F)f;
        }
        return null;
    }


   /** sets an alternate ID by which this node can be referred to.  This ID is
    * not stored in the database, and exists only for this instance of the node.
    * @param s
    */
    protected void setAlternateID(String s) {
        alternateID = s;
    }

    /** returns the id set by 'setAlternateID' */
    public String getAlternateID() {
        return alternateID;
    }

    /** returns whether or not the parent DB interface believes that the
     * featureset held by this node is comptaible with the featureset held by 
     * node n2.
     * @param n2
     * @param featureTypes
     * @return
     */
    public boolean compatible(Node n2) {
        return parent.getDB().featureSetsCompatible(getFeatures(), n2.getFeatures());
    }

    @Override
    public String toString() {
        return "{N:"+getClass()+"("+id+")"+features+"}";
        //return ""+id;
    }

    public void freshenID() {
        id = parent.getUnusedNodeID();
    }


	@Override
	public int compareTo(Object o) {
		if(o instanceof Node) {
			return id - ((Node)o).id;
		}
		return 0;
	}
}