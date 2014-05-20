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
package sai.graphviz;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

import sai.DBInterface;
import sai.DBInterfaceTest;
import sai.graph.jgrapht.Graph;
import sai.graph.jgrapht.Node;

/**
 *
 * @version 0.1.1
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class TestGraphviz {
    public static void main(String[] args) throws IOException {
        DBInterface db = DBInterfaceTest.getTestDBInterface();
        Graph g = DBInterfaceTest.getSmallGraph1(db);
        Graph g2 = DBInterfaceTest.getSmallGraph1(db);
        Map<Node,Node> m = Maps.newHashMap();
        for(Node n : g.vertexSet()) {
            m.put(n, g2.getNode(n.getID()));
        }

        System.out.println(GraphvizEncoder.encodeAsDotText(g));
        GraphvizEncoder.encodeAsPNG(g, "/tmp/a.png");

        System.out.println("\n\n\n");

        System.out.println(GraphvizEncoder.encodeAsDotText(g, g2, m));
        GraphvizEncoder.encodeAsPNG(g, g2, m, "/tmp/b.png");

        Runtime.getRuntime().exec("eog /tmp/a.png");
        Runtime.getRuntime().exec("eog /tmp/b.png");
    }
}