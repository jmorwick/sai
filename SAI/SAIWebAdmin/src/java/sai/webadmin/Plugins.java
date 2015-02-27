/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sai.webadmin;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** manages all plugin classes.
 * 
 * TODO: basic testing performed -- more extensive testing and unit tests needed.
 * @author jmorwick
 */
@WebServlet(name = "Plugins", urlPatterns = {"/Plugins"})
public class Plugins extends HttpServlet {

    
      
    /** plugin classes may only be registered (and thus instantiated) if they
     * are or extend one of these types
     */
    public static final Class<?>[] ALLOWED_SUPERCLASSES = 
            new Class<?>[] {
                    sai.graph.GraphFactory.class,
                    sai.db.DBInterface.class,
                    java.io.File.class,
                    java.lang.Integer.class
            };
    
    
    /** collection of all classes which can be instantiated as plugins.
     * This may include classes which are not technically "plugins", such
     * as java.util.File, but are used as resources for instantiating 
     * plugins. Classes may only be instantiated by this webapp if they are 
     * registered in this collection. 
     */
    public static final Set<Class<?>> PLUGINS = 
            Sets.newCopyOnWriteArraySet(); //thread safe
    
    
    /**
     * Processes POST requests for registering plugin classes.
     * 
     * the "plugin" parameter must be specified and must be a valid 
     * name of a java class which is loadable in the server's context. 
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            //
            //validate the request
            //
            
            // make sure the proper POST params are set
            Map<String,String[]> args = request.getParameterMap();
            if(!args.containsKey("plugin") || 
                    args.get("plugin").length != 1) {
                response.sendError(400, "no plugin indicated");
                return;
            }
            
            // validate the class name provided
            String classname = args.get("plugin")[0];
            Class<?> plugin = null;
            try {
                plugin = Class.forName(classname); // load the class
            } catch(ClassNotFoundException e) {
                response.sendError(400, "improper or nonexistant plugin");
                return;
            }
            
            // ensure the plugin class is concrete
            if(Modifier.isAbstract(plugin.getModifiers())) {
                response.sendError(400, "plugin class must be instantiable");
                return;
            }
            
            // ensure we're alloed to use this plugin 
            boolean allowed = false;
            for(Class c : ALLOWED_SUPERCLASSES) {
                if(c.isAssignableFrom(plugin)) {
                    allowed = true;
                }
            }
            if(!allowed) {
                response.sendRedirect("plugins.jsp");
                return;
            }
            
            //
            // request is valid, process it by adding to the appropriate collections
            //
            
            PLUGINS.add(plugin);
            
            response.sendRedirect("plugins.jsp");
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
        return "Short description";
    }// </editor-fold>

}
