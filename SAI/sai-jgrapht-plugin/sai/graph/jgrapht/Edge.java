/* Copyright 2011 Joseph Kendall-Morwick

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

import java.util.Set;

import db.mysql.MySQLDBInterface;
import sai.SAIUtil;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.base.Predicates.equalTo;

/**
 * @version 2.0s.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated
public class Edge implements Comparable {
    private int id = -1;
    private Set<Feature> features = newHashSet();
    private Graph parent;

    public Edge(Graph parent, Feature ... tags) {
        id = parent.getUnusedEdgeID();
        this.parent = parent;
        for(Feature tag : tags) this.features.add(tag);
    }

    public Edge(Graph parent, MySQLDBInterface db, Feature ... tags) {
        this(parent,tags);
    }

    public Edge(int id, Graph parent, MySQLDBInterface db, Feature ... tags) {
        this(parent, db, tags);
        this.id = id;
    }

    public Edge(int id, Graph parent, Feature ... tags) {
        this(parent,tags);
        this.id = id;
    }

    public int getID() { return id; }

    public void addFeature(Feature t) {
        features.add(t);
        for(Feature f : features) {
            if(!f.canAccompany(filter(features, equalTo(f))))
                throw new IllegalArgumentException("Added feature cannot accompany existing features");
        }
    }

    public Set<Feature> getFeatures() {
        return newHashSet(features);
    }

    public Graph getParent() {
        return parent;
    }

    /** returns whether or not the parent DB Interface believes this edge's
     * featureset is subsumed by e2's featureset. 
     * @param e2
     * @param featureTypes
     * @return
     */
    public boolean subsumes(Edge e2) {
        return parent.getDB().featureSetsCompatible(
                SAIUtil.retainOnly(getFeatures()),
                SAIUtil.retainOnly(e2.getFeatures()));
    }


    public String toString() {
        return "{E:"+this.getClass()+"("+id+")"+"["+
                parent.getEdgeSource(this).getID() +
                ","+
                parent.getEdgeTarget(this).getID()+
                "]"+features+"}";

    }

	@Override
	public int compareTo(Object o) {
		if(o instanceof Edge) {
			return id - ((Edge)o).id;
		}
		return 0;
	}
}