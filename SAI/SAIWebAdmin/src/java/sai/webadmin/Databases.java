/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sai.webadmin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sai.db.DBInterface;

/**
 *
 * @author jmorwick
 */
@WebServlet(name = "Databases", urlPatterns = {"/Databases"})
public class Databases extends HttpServlet {
    
    public static final Map<String,DBInterface> CONNECTIONS = 
            Maps.newConcurrentMap();
    public static final List<Class<? extends DBInterface>> INTERFACES = 
            Lists.newCopyOnWriteArrayList();


    private synchronized boolean addDatabase(String name, DBInterface db) {
        //make sure there isn't already a db with the same name
        if(CONNECTIONS.containsKey(name)) 
            if(CONNECTIONS.get(name).isConnected()) //...that is connected
                return false;
        
        CONNECTIONS.put(name, db);
        return true;
    }
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            //
            //validate the request
            //
            
            // make sure an action was set
            Map<String,String[]> params = request.getParameterMap();
            if(!params.containsKey("action") || params.get("action").length != 1
               || !Sets.newHashSet("connect", "disconnect", "plugin")
                       .contains(params.get("action")[0])) {
                response.sendError(400, "no proper action indicated");
                return;
            }
            
            
            
            //
            // request is valid, process it
            //
            boolean valid = true;
            String action = params.get("action")[0];
            if(action.equals("connect")) {
                //use given name if present
                String name = params.containsKey("name") && 
                        params.get("name").length > 0 ?
                        params.get("name")[0] : "db";
                int num = 1;
                //create the new DB connection
                Object newinstance = Resources.instantiate(params);
                 
                if(newinstance == null) response.sendError(400);
                name = Resources.addResource(newinstance, name);
                CONNECTIONS.put(name, (DBInterface)newinstance);
                
                response.sendRedirect(".");
            } else if(action.equals("disconnect")) {
                response.sendRedirect(".");
            } else if(action.equals("plugin") && params.containsKey("plugin")
                    && params.get("plugin").length == 1) {
                //TODO: remove this function
                String plugin = params.get("plugin")[0];
                try {
                    Class pclass = Class.forName(plugin);
                    if(DBInterface.class.isAssignableFrom(pclass) && 
                       pclass.getConstructors().length > 0) {
                        Plugins.PLUGINS.add(pclass);
                        INTERFACES.add(pclass);
                        response.sendRedirect("plugins.jsp");
                    } else valid = false;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Databases.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else valid = false;
                
            if(!valid) {
                response.sendError(400, "invalid arguments");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "manages SAI database connections";
    }// </editor-fold>

}
