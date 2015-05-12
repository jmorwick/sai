<%-- 
    This page lists available database interfaces and allows the user to 
    query these databases.

// TODO: add option to query "all graphs" from a DBInterface
// TODO: add option to use graph retriever and query it
// TODO: add option to retrieve a single graph w/ factory of choice and 
         inspect w/ visualizer of choice
// TODO: add option to compare two graphs with map generator of choice and 
         inspect mapping w/ visualizer

    Document   : index
    Created on : Jul 6, 2014, 3:55:04 PM
    Author     : Joey Kendall-Morwick <jkendallmorwick@capital.edu>
--%>
<%@page import="info.kendall_morwick.funcles.T2"%>
<%@page import="sai.webadmin.Databases"%>
<%@page import="sai.webadmin.Resources"%>
<%@page import="sai.db.DBInterface"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="header.jspf" />
<h1>Query a database</h1>
<select name="db">
    <%
    for(T2<String, DBInterface> t : Databases.getDBInterfaces()) {
    %><option><%= t.a1() %></option><%
    }
    %>
</select>
<jsp:include page="footer.jspf" />