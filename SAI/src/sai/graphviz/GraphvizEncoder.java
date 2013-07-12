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

import info.kendallmorwick.util.FileUtil;
import info.kendallmorwick.util.Map;
import info.kendallmorwick.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dataandsearch.sai.*;

/**
 * Produces textual and png image visualizations of graphs using the 
 * graphviz visualizer.
 * 
 * @version 0.2.0
 * @author Joseph Kendall-Morwick <jmorwick@indiana.edu>
 */
public class GraphvizEncoder {

    public static final String DOT_LOCATION = "dot";

    private static void appendAttributes(Set<Feature> features, StringBuffer out, String id, String prefix) {
        String label = "ID: "+prefix+id;
        for(Feature af : features) {
            if(!(af instanceof GraphvizProperty)) {
                String name = af.getClass().getSimpleName();
                label += "\\n"+name + ": " + af.getValue();
            } else {
                GraphvizProperty gp = (GraphvizProperty)(af);
                out.append(gp.toDot());
            }
        }
        out.append("[label=\""+label+"\"]");
    }

    private static String encodeNodes(Graph g, String prefix) {
        StringBuffer encoding = new StringBuffer();
        for(Node n : g.vertexSet()) {
            encoding.append("N"+prefix+n.getID()+" ");
            appendAttributes(n.getFeatures(),encoding, 
                    n.getID() + 
                    (n.getAlternateID() != null ? 
                        "(" + n.getAlternateID() + ")" : ""),
                    prefix);
            encoding.append(";\n");
        }
        return encoding.toString();
    }

    private static String encodeEdges(Graph g, String prefix) {
        StringBuffer encoding = new StringBuffer();
        String edgeop = g.getDB().directedGraphs() ? "->" : "--";
        for(Edge e : g.edgeSet()) {
            Node n1 = g.getEdgeSource(e);
            Node n2 = g.getEdgeTarget(e);
            encoding.append("N"+prefix+n1.getID()+" "+edgeop+" N"+prefix+n2.getID()+" ");
            appendAttributes(e.getFeatures(),encoding, e.getID()+"", prefix);
            encoding.append(";\n");
        }
        return encoding.toString();
    }

    public static String encodeAsDotText(Graph g) {
        StringBuffer encoding = new StringBuffer();
        String id = g.getID() > -1 ? g.getID()+"" :"";
        encoding.append((g.getDB().directedGraphs() ? "di" : ""));
        encoding.append("graph G"+id+" ");
        encoding.append("{\n");
        //appendAttributes(g.getFeatures(),encoding, g.getID());
        encoding.append("GTAGS ");
        appendAttributes(g.getFeatures(),encoding, g.getID()+"", "Graph");
        encoding.append(encodeNodes(g,""));
        encoding.append(encodeEdges(g,""));
        encoding.append("}");
        return encoding.toString();
    }
    
    public static void colorNode(Node n, String color, DBInterface db) {
        for(Feature f : n.getFeatures()) {
            if(f instanceof GraphvizProperty) {
                GraphvizProperty p = (GraphvizProperty)f;
                if(p.getGraphvizProperty().equals("color"))
                    n.removeFeature(f);
            }
        }
        n.addFeature(new GraphvizProperty("color", color, db));
    }

    public static String encodeAsDotText(Graph g1, Graph g2, Map<Node,Node> m) {
        StringBuilder encoding = new StringBuilder();
        g1 = g1.copy();
        g2 = g2.copy();
        Set<String> colors = new Set<String>();
        colors.addAll(Arrays.asList(ARROW_COLORS));
        Map<Node, String> nodeColors = new Map<Node, String>();
        for(Node n : g1.vertexSet()) colorNode(n, "gray", g1.getDB());
        for(Node n : g2.vertexSet()) colorNode(n, "gray", g2.getDB());
        for(Map.Entry<Node,Node> e : m.entrySet()) {
            String arrowColor = colors.getRandomElement();
            colors.remove(arrowColor);
            Node n1 = g1.getNode(e.getKey().getID());
            Node n2 = g2.getNode(e.getValue().getID());
            nodeColors.put(n1, arrowColor);
            colorNode(n1, arrowColor, g1.getDB());
            colorNode(n2, arrowColor, g2.getDB());
        }
        
        
        encoding.append("digraph M ");
        //appendAttributes(g.getFeatures(),encoding, g.getID());
        encoding.append("{\n");
        encoding.append("subgraph cluster_A {");
        encoding.append(encodeNodes(g1,"A"));
        encoding.append(encodeEdges(g1,"A"));
        encoding.append("}");
        encoding.append("subgraph cluster_B {");
        encoding.append(encodeNodes(g2,"B"));
        encoding.append(encodeEdges(g2,"B"));
        encoding.append("}");
        
        for(Map.Entry<Node,Node> e : m.entrySet()) {
            Node n1 = g1.getNode(e.getKey().getID());
            Node n2 = g2.getNode(e.getValue().getID());
            String arrowColor = nodeColors.get(n1);
            encoding.append("NA"+n1.getID()+" -> NB"+n2.getID()+" ");
            encoding.append("[dir=both,arrowtail=crow,arrowhead=crow,color="+arrowColor+"]");
            encoding.append(";\n");
        }
        encoding.append("}");
        return encoding.toString();
    }


    private static void encodeAsPNG(String dotText, File f) throws IOException {
        OutputStream out = new FileOutputStream(f);
        Process p = Runtime.getRuntime().exec(DOT_LOCATION+" -Tpng");
        p.getOutputStream().write(dotText.getBytes());
        p.getOutputStream().flush();
        p.getOutputStream().close();
        while(true) {
            int b = p.getInputStream().read();
            if(b == -1) break;
            out.write(b);
        }
        out.flush();
        out.close();
    }

    public static void encodeAsPNG(Graph g, String filename) throws IOException {
        encodeAsPNG(g, new File(filename));
    }

    public static void encodeAsPNG(Graph g, File f) throws IOException {
        encodeAsPNG(encodeAsDotText(g), f);
    }

    public static void encodeAsPNG(Graph g1, Graph g2, Map<Node,Node> m,
            String filename) throws IOException {
        encodeAsPNG(g1, g2, m, new File(filename));
    }

    public static void encodeAsPNG(Graph g1, Graph g2, Map<Node,Node> m,
            File f) throws IOException {
        encodeAsPNG(encodeAsDotText(g1, g2, m), f);
    }


    public static final String[] ARROW_COLORS = new String[] {  "yellow",
  "yellow1",
  "yellow2",
  "yellow3",
  "yellow4",
  "yellowgreen",
  "turquoise",
  "turquoise1",
  "turquoise2",
  "turquoise3",
  "turquoise4",
  "violet",
  "violetred",
  "violetred1",
  "violetred2",
  "violetred3",
  "violetred4",
  "wheat",
  "wheat1",
  "wheat2",
  "wheat3",
  "wheat4",
  "snow4",
  "springgreen",
  "springgreen1",
  "springgreen2",
  "springgreen3",
  "springgreen4",
  "steelblue",
  "steelblue1",
  "steelblue2",
  "steelblue3",
  "steelblue4",
  "tan",
  "tan1",
  "tan2",
  "tan3",
  "tan4",
  "thistle",
  "thistle1",
  "thistle2",
  "thistle3",
  "thistle4",
  "tomato",
  "tomato1",
  "tomato2",
  "tomato3",
  "tomato4",
  "seashell3",
  "seashell4",
  "sienna",
  "sienna1",
  "sienna2",
  "sienna3",
  "sienna4",
  "skyblue",
  "skyblue1",
  "skyblue2",
  "skyblue3",
  "skyblue4",
  "slateblue",
  "slateblue1",
  "slateblue2",
  "slateblue3",
  "slateblue4",
  "slategray",
  "slategray1",
  "slategray2",
  "slategray3",
  "slategray4",
  "slategrey",
  "olivedrab",
  "olivedrab1",
  "olivedrab2",
  "olivedrab3",
  "olivedrab4",
  "orange",
  "orange1",
  "orange2",
  "orange3",
  "orange4",
  "orangered",
  "orangered1",
  "orangered2",
  "orangered3",
  "orangered4",
  "orchid",
  "orchid1",
  "orchid2",
  "orchid3",
  "orchid4",
  "palegoldenrod",
  "palegreen",
  "palegreen1",
  "palegreen2",
  "palegreen3",
  "palegreen4",
  "paleturquoise",
  "paleturquoise1",
  "paleturquoise2",
  "paleturquoise3",
  "paleturquoise4",
  "palevioletred",
  "palevioletred1",
  "palevioletred2",
  "palevioletred3",
  "palevioletred4",
  "papayawhip",
  "peachpuff",
  "peachpuff1",
  "peachpuff2",
  "peachpuff3",
  "peachpuff4",
  "peru",
  "pink",
  "pink1",
  "pink2",
  "pink3",
  "pink4",
  "plum",
  "plum1",
  "plum2",
  "plum3",
  "plum4",
  "powderblue",
  "purple",
  "purple1",
  "purple2",
  "purple3",
  "purple4",
  "red",
  "red1",
  "red2",
  "red3",
  "red4",
  "rosybrown",
  "rosybrown1",
  "rosybrown2",
  "rosybrown3",
  "rosybrown4",
  "royalblue",
  "royalblue1",
  "royalblue2",
  "royalblue3",
  "royalblue4",
  "saddlebrown",
  "salmon",
  "salmon1",
  "salmon2",
  "salmon3",
  "salmon4",
  "sandybrown",
  "seagreen",
  "seagreen1",
  "seagreen2",
  "seagreen3",
  "seagreen4",
  "mistyrose",
  "mistyrose1",
  "mistyrose2",
  "mistyrose3",
  "mistyrose4",
  "moccasin",
  "navajowhite",
  "navajowhite1",
  "navajowhite2",
  "navajowhite3",
  "navajowhite4",
  "navy",
  "navyblue",
  "ivory3",
  "ivory4",
  "khaki",
  "khaki1",
  "khaki2",
  "khaki3",
  "khaki4",
  "lavender",
  "lavenderblush",
  "lavenderblush1",
  "lavenderblush2",
  "lavenderblush3",
  "lavenderblush4",
  "lawngreen",
  "lemonchiffon",
  "lemonchiffon1",
  "lemonchiffon2",
  "lemonchiffon3",
  "lemonchiffon4",
  "lightblue",
  "lightblue1",
  "lightblue2",
  "lightblue3",
  "lightblue4",
  "lightcoral",
  "lightcyan",
  "lightcyan1",
  "lightcyan2",
  "lightcyan3",
  "lightcyan4",
  "lightgoldenrod",
  "lightgoldenrod1",
  "lightgoldenrod2",
  "lightgoldenrod3",
  "lightgoldenrod4",
  "lightgoldenrodyellow",
  "lightgray",
  "lightgrey",
  "lightpink",
  "lightpink1",
  "lightpink2",
  "lightpink3",
  "lightpink4",
  "lightsalmon",
  "lightsalmon1",
  "lightsalmon2",
  "lightsalmon3",
  "lightsalmon4",
  "lightseagreen",
  "lightskyblue",
  "lightskyblue1",
  "lightskyblue2",
  "lightskyblue3",
  "lightskyblue4",
  "lightslateblue",
  "lightslategray",
  "lightslategrey",
  "lightsteelblue",
  "lightsteelblue1",
  "lightsteelblue2",
  "lightsteelblue3",
  "lightsteelblue4",
  "lightyellow",
  "lightyellow1",
  "lightyellow2",
  "lightyellow3",
  "lightyellow4",
  "limegreen",
  "linen",
  "magenta",
  "magenta1",
  "magenta2",
  "magenta3",
  "magenta4",
  "maroon",
  "maroon1",
  "maroon2",
  "maroon3",
  "maroon4",
  "mediumaquamarine",
  "mediumblue",
  "mediumorchid",
  "mediumorchid1",
  "mediumorchid2",
  "mediumorchid3",
  "mediumorchid4",
  "mediumpurple",
  "mediumpurple1",
  "mediumpurple2",
  "mediumpurple3",
  "mediumpurple4",
  "mediumseagreen",
  "mediumslateblue",
  "mediumspringgreen",
  "mediumturquoise",
  "mediumvioletred",
  "midnightblue",
  "mintcream",
  "hotpink",
  "hotpink1",
  "hotpink2",
  "hotpink3",
  "hotpink4",
  "indianred",
  "indianred1",
  "indianred2",
  "indianred3",
  "indianred4",
  "indigo",
  "green",
  "green1",
  "green2",
  "green3",
  "green4",
  "greenyellow",
  "gold",
  "gold1",
  "gold2",
  "gold3",
  "gold4",
  "goldenrod",
  "goldenrod1",
  "goldenrod2",
  "goldenrod3",
  "goldenrod4",
  "forestgreen",
  "crimson",
  "cyan",
  "cyan1",
  "cyan2",
  "cyan3",
  "cyan4",
  "darkgoldenrod",
  "darkgoldenrod1",
  "darkgoldenrod2",
  "darkgoldenrod3",
  "darkgoldenrod4",
  "darkgreen",
  "darkkhaki",
  "darkolivegreen",
  "darkolivegreen1",
  "darkolivegreen2",
  "darkolivegreen3",
  "darkolivegreen4",
  "darkorange",
  "darkorange1",
  "darkorange2",
  "darkorange3",
  "darkorange4",
  "darkorchid",
  "darkorchid1",
  "darkorchid2",
  "darkorchid3",
  "darkorchid4",
  "darksalmon",
  "darkseagreen",
  "darkseagreen1",
  "darkseagreen2",
  "darkseagreen3",
  "darkseagreen4",
  "darkslateblue",
  "darkslategray",
  "darkslategray1",
  "darkslategray2",
  "darkslategray3",
  "darkslategray4",
  "darkslategrey",
  "darkturquoise",
  "darkviolet",
  "deeppink",
  "deeppink1",
  "deeppink2",
  "deeppink3",
  "deeppink4",
  "deepskyblue",
  "deepskyblue1",
  "deepskyblue2",
  "deepskyblue3",
  "deepskyblue4",
  "dimgray",
  "dimgrey",
  "dodgerblue",
  "dodgerblue1",
  "dodgerblue2",
  "dodgerblue3",
  "dodgerblue4",
  "firebrick",
  "firebrick1",
  "firebrick2",
  "firebrick3",
  "firebrick4",
  "blue",
  "blue1",
  "blue2",
  "blue3",
  "blue4",
  "blueviolet",
  "brown",
  "brown1",
  "brown2",
  "brown3",
  "brown4",
  "burlywood",
  "burlywood1",
  "burlywood2",
  "burlywood3",
  "burlywood4",
  "cadetblue",
  "cadetblue1",
  "cadetblue2",
  "cadetblue3",
  "cadetblue4",
  "chartreuse",
  "chartreuse1",
  "chartreuse2",
  "chartreuse3",
  "chartreuse4",
  "chocolate",
  "chocolate1",
  "chocolate2",
  "chocolate3",
  "chocolate4",
  "coral",
  "coral1",
  "coral2",
  "coral3",
  "coral4",
  "cornflowerblue",
  "aquamarine",
  "aquamarine1",
  "aquamarine2",
  "aquamarine3",
  "aquamarine4"};

}