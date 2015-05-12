<%-- 
    This page lists available database interfaces and allows the user to 
    query these databases.

    // TODO: create query form in index.jsp
    // TODO: create form processor in Databases creating session variable with iterator
    // TODO: also initialize a shared counter for how many id's have been read 
    // TODO: also initialize a shared map of result #'s to graph id's
    // TODO: also initialize a cache for retrieved graphs in session variable
    // TODO: have this script check get params for page and limit to determine
             how many graphs to retrieve and how to update shared vars
    // TODO: render table with results and links for graph visualization (allow choice of existing visualizers)
    // TODO: create a script which accesses graph visualizer
    // TODO: validate state and get params
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