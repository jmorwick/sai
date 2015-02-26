<%-- 
    Document   : index
    Created on : Jul 6, 2014, 3:55:04 PM
    Author     : Joey Kendall-Morwick <jkendallmorwick@capital.edu>
--%>
<%@page import="sai.webadmin.Databases"%>
<%@page import="sai.webadmin.Resources"%>
<%@page import="sai.db.DBInterface"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<h1>Query a database</h1>
<select name="db">
    <%
    for(String dbName : Databases.CONNECTIONS.keySet()) {
    %><option><%= dbName %></option><%
    }
    %>
</select>
<jsp:include page="footer.jspf" />