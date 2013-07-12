/* Copyright 2011 Joseph Kendall-Morwick

     This file is part of SAI: The Structure Access Interface.

    jmorwick-javalib is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jmorwick-javalib is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with jmorwick-javalib.  If not, see <http://www.gnu.org/licenses/>.

 */
package org.dataandsearch.sai.graphviz;

import info.kendallmorwick.util.Set;
import org.dataandsearch.sai.DBInterface;
import org.dataandsearch.sai.Feature;

/**
 * A feature which signals the graphviz encoder to use special formatting --
 * these features are not intended to be used for reasoning.
 *
 * @version 0.2.0
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class GraphvizProperty extends Feature {
    private final String property;
    private final String propertyValue;

    public static final Set<String> legalProperties =
            Set.makeSet("Damping", "K", "URL", "area");  ///TODO: finish list

    public GraphvizProperty(int id, DBInterface db) {
        super(id, db);
        int cloc = this.getValue().indexOf(":");
        int eqloc = this.getValue().indexOf("=");
        this.property = getValue().substring(cloc+1, eqloc);
        this.propertyValue = getValue().substring(eqloc+1);
    }

    public GraphvizProperty(String property, String value, DBInterface db) {
        super("graphviz:"+property+"="+value, db);

        //TODO: validate
        this.property = property;
        this.propertyValue = value;
    }

    public String getGraphvizProperty() { return property; }
    public String getGraphvizPropertyValue() { return propertyValue; }

    public String toDot() {
        return "["+property+"=\""+propertyValue+"\"]";
    }
}