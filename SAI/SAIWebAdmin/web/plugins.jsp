<%-- 
    This page allows the user to register plugins, create new instances of 
    plugin classes and other resoruces, and manage existing plugins and 
    resources. 

    Document   : plugins
    Created on : Jul 8, 2014, 12:29:28 PM
    Author     : jmorwick
--%>

<%@page import="sai.visualization.MapVisualizer"%>
<%@page import="sai.visualization.GraphVisualizer"%>
<%@page import="sai.retrieval.GraphRetriever"%>
<%@page import="sai.retrieval.GraphIndexBasedRetriever"%>
<%@page import="sai.retrieval.FeatureIndexBasedRetriever"%>
<%@page import="sai.indexing.FeatureIndexGenerator"%>
<%@page import="sai.comparison.matching.MatchingGenerator"%>
<%@page import="sai.comparison.heuristics.GraphMatchingHeuristic"%>
<%@page import="sai.comparison.heuristics.FeatureSetMatchingHeuristic"%>
<%@page import="sai.comparison.heuristics.FeatureMatchingHeuristic"%>
<%@page import="sai.comparison.compatibility.FeatureSetCompatibilityChecker"%>
<%@page import="sai.comparison.compatibility.FeatureCompatibilityChecker"%>
<%@page import="info.kendall_morwick.funcles.Tuple"%>
<%@page import="com.google.common.collect.Sets"%>
<%@page import="info.kendall_morwick.funcles.T2"%>
<%@page import="java.util.Set"%>
<%@page import="sai.graph.GraphFactory"%>
<%@page import="sai.db.DBInterface"%>
<%@page import="java.util.Map"%>
<%@page import="sai.webadmin.Databases"%>
<%@page import="sai.webadmin.Graphs"%>
<%@page import="sai.webadmin.Plugins"%>
<%@page import="sai.webadmin.Resources"%>
<jsp:include page="header.jspf" />

<%
    Set<T2<String, ? extends Class<?>>> categories = Sets.newHashSet(
            Tuple.makeTuple("Database Interfaces", DBInterface.class),
            Tuple.makeTuple("Graph Factories", GraphFactory.class),
            Tuple.makeTuple("Feature Compatability Checkers", 
                    FeatureCompatibilityChecker.class),
            Tuple.makeTuple("Feature Set Compatability Checkers", 
                    FeatureSetCompatibilityChecker.class),
            Tuple.makeTuple("Feature Matching Heuristics", 
                    FeatureMatchingHeuristic.class),
            Tuple.makeTuple("Feature Set Matching Heuristics", 
                    FeatureSetMatchingHeuristic.class),
            Tuple.makeTuple("Graph Matching Heuristics", 
                    GraphMatchingHeuristic.class),
            Tuple.makeTuple("Feature-index Generators", 
                    FeatureIndexGenerator.class),
            Tuple.makeTuple("Feature-index Based Retrievers", 
                    FeatureIndexBasedRetriever.class),
            Tuple.makeTuple("Graph-index Generators", 
                    GraphIndexBasedRetriever.class),
            Tuple.makeTuple("Graph-index based Retrievers", 
                    GraphRetriever.class),
            Tuple.makeTuple("Graph Visualizers", GraphVisualizer.class),
            Tuple.makeTuple("Map Visualizers", MapVisualizer.class),
            Tuple.makeTuple("Matching Generators", MatchingGenerator.class)
    );
    Set<Class<?>> specializedPlugins = Sets.newHashSet();
    Set<String> liveCategories = Sets.newHashSet();
    for(T2<String, ? extends Class<?>> category : categories) { 
        for(Class c : Plugins.PLUGINS) {
            if(category.a2().isAssignableFrom(c)) {
                liveCategories.add(category.a1());
                specializedPlugins.add(c);
            }
            Resources.getInstantiationForm(request, out, "", "./Resources", "POST", c);
        }
    }
%>

<div class="plugininfo" ID="dbplugins">
    <% for(T2<String, ? extends Class<?>> category : categories) 
        if(liveCategories.contains(category.a1())) { %>
    <h1><%= category.a1() %></h1>
    <ul>
        <%
            for(Class c : Plugins.PLUGINS) {
                if(category.a2().isAssignableFrom(c)) {
                %>
                <li onClick='showCreationForm("", "<%= c.getCanonicalName() %>");'>
                    <%= c.getSimpleName() %>
                </li>
                <%
                }
            }
         %>
    </ul>
    <% } %>
    <h1>All other plugins / registered classes</h1>
    <ul>
        <%
            for(Class c : Sets.difference(Plugins.PLUGINS, specializedPlugins)) {
                %>
                <li onClick='showCreationForm("", "<%= c.getCanonicalName() %>");'>
                    <%= c.getSimpleName() %>
                </li>
                <%
            }
         %>
    </ul>
    <form action="Plugins" method="POST">
        <h1>Register class / plugin</h1>
        class name: <input type="text" name="plugin">
        <input type="submit" name="action" value="register plugin class">
    </form>
</div>

<h1>Manage resources</h1>
<form action="./Resources" method="POST">
<select name="resources" multiple>
    <% //TODO: make this a table with more info about each resource
    for(Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
        String name = e.getKey();
        Object resource = e.getValue();
    %><option><%= name %></option><%
    }
    %>
</select>
<input type="submit" name="action" value="remove"/>
</form>
<jsp:include page="footer.jspf" />