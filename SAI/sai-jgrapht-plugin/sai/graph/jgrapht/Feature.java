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

import java.util.Collection;
import java.util.Set;

import db.mysql.MySQLDBInterface;

/**
 * @version 2.0.0
 * @author Joseph Kendall-Morwick
 */
@Deprecated
public class Feature {
    private int id = -1;
    private String value;
    private MySQLDBInterface db;


/** 'value' is a unique String for this feature
 * 
 * @param value
 * @param db
 */
    public Feature(String value, MySQLDBInterface db) {
        this.db = db;
        db.registerFeatureClass(this);
        this.value =  value;
        this.id = db.getFeatureID(getClass().getName(),  value);
    }

    /** id is the id of an existing Feature in the database
     * 
     * @param id
     * @param db
     */
    protected Feature(int id, MySQLDBInterface db) {
        this.db = db;
        db.registerFeatureClass(this);
        this.value = db.getFeatureName(getClass().getName(), id);
        this.id = id;
    }

    /** returns a string unique to this feature instance */
    public String getValue() { return value; }

    /** returns true iff o is a feature and this feature is the same object as
     * o, contains the exact same content.  It does not check for bi-directional
     * 'is-a' equivalency.  
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if(o.getClass() != getClass()) return false;
        Feature t = (Feature)o;
        if(this == t)
            return true; //they are the same object in memory
       if(t.getValue().equals(getValue()) &&
                t.getClass().getName().equals(getClass().getName()))
           return true; //they are exactly the same i ncontent
        return false;  //they are neither the same nor equivalent
    }


    @Override
    public int hashCode() { return toString().hashCode(); }

    @Override
    public String toString() {
        return getClass().getName()+":"+getValue();
    }


    public int getID() {
        return id;
    }

    /** Table definition and other statements required to be executed on a
     * SQL database before this class can be used.
     * 
     * @return
     */
    public String getInitSQL() {
        return "";
    }

    public MySQLDBInterface getDB() {
        return db;
    }


    /** determines whether or not this feature is compatible with feature f2.
     * This method is not necessarily symmetric.
     *
     * @param f2 the proposed super-feature
     * @return whether or not this feature is a sub-feature of feature f2
     */
    public boolean compatible(Feature f2) {
        if(equals(f2)) return true;
        return db.isa(this, f2);
    }

    /** returns whether or not this feature 'is-a' f2 and f2 'is-a' this
     * feature.
     * 
     * @param f2
     * @return
     */
    public final boolean equivalent(Feature f2) {
        return f2.compatible(this) && this.compatible(f2);
    }

    /** determines whether or not this feature can accompany the indicated
     * features within a single entity (graph, node, or edge)
     * @param features
     * @return
     */
    public boolean canAccompany(Collection<Feature> features) {
        return true;
    }

    /** determines the degree of relatedness between this feature and
     * f2, where 0 indicates no relationship, and 1 indicates this feature
     * 'is-a' f2.  This function is not necessarily symmetric.  
     * @param f2
     * @return
     */
    public double relatedness(Feature f2) {
        return this.compatible(f2) ? 1.0 : 0.0;
    }

}