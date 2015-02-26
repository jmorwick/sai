<%-- 
    Document   : plugins
    Created on : Jul 8, 2014, 12:29:28 PM
    Author     : jmorwick
--%>

<%@page import="sai.graph.GraphFactory"%>
<%@page import="sai.db.DBInterface"%>
<%@page import="java.util.Map"%>
<%@page import="sai.webadmin.Databases"%>
<%@page import="sai.webadmin.Graphs"%>
<%@page import="sai.webadmin.Plugins"%>
<%@page import="sai.webadmin.Resources"%>
<jsp:include page="header.jspf" />
<div class="plugininfo" ID="dbplugins">
    <h1>Database interface plugins</h1>
    <select name="dbplugin" multiple>
        <%
            for(Class c : Databases.INTERFACES) {
                %>
                <option disabled><%= c.getSimpleName() %></option>
                <%
            }
         %>
    </select>
    <h1>Graph factory plugins</h1>
    <select name="dbplugin" multiple>
        <%
            for(Class c : Graphs.INTERFACES) {
                %>
                <option disabled><%= c.getSimpleName() %></option>
                <%
            }
         %>
    </select>
    <h1>All other plugins / registered classes</h1>
    <select name="dbplugin" multiple>
        <%
            for(Class c : Plugins.PLUGINS) {
                if(!DBInterface.class.isAssignableFrom(c) && 
                   !GraphFactory.class.isAssignableFrom(c)) {
                %>
                <option disabled><%= c.getSimpleName() %></option>
                <%
                }
            }
         %>
    </select>
    <h1>Instantiate a new resource</h1>
<%
    Resources.getMultiform(request, out, "create", 
            "Resources", "POST", Plugins.PLUGINS);
%>
    <form action="Plugins" method="POST">
        <h1>Register class / plugin</h1>
        class name: <input type="text" name="plugin">
        <input type="submit" name="action" value="register plugin class">
    </form>
</div>

<h1>Manage resources</h1>
<select name="resources" multiple>
    <%
    for(Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
        String name = e.getKey();
        Object resource = e.getValue();
    %><option><%= name %> (<%=resource.getClass().getSimpleName()%> - <%=resource.toString()%>)</option><%
    }
    %>
</select>
<jsp:include page="footer.jspf" />