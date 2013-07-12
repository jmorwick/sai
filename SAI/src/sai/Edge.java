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

package sai;

import sai.comparison.Util;

/**
 * @version 2.0s.0
 * @author Joseph Kendall-Morwick
 */
public class Edge {
    private int id = -1;
    private Set<Feature> features = new Set<Feature>();
    private Graph parent;

    public Edge(Graph parent, Feature ... tags) {
        id = parent.getUnusedEdgeID();
        this.parent = parent;
        for(Feature tag : tags) this.features.add(tag);
    }

    public Edge(Graph parent, DBInterface db, Feature ... tags) {
        this(parent,tags);
    }

    public Edge(int id, Graph parent, DBInterface db, Feature ... tags) {
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
            if(!f.canAccompany(features.removeC(f)))
                throw new IllegalArgumentException("Added feature cannot accompany existing features");
        }
    }

    public Set<Feature> getFeatures() {
        return features.copy();
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
    public boolean subsumes(Edge e2, Class<? extends Feature> ... featureTypes) {
        return parent.getDB().FeatureSetsCompatible(
                Util.retainOnly(getFeatures(), featureTypes),
                Util.retainOnly(e2.getFeatures(), featureTypes),
                featureTypes);
    }


    public String toString() {
        return "{E:"+this.getClass()+"("+id+")"+"["+
                parent.getEdgeSource(this).getID() +
                ","+
                parent.getEdgeTarget(this).getID()+
                "]"+features+"}";

    }
}