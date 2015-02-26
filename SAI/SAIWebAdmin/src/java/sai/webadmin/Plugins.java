/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sai.webadmin;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sai.db.DBInterface;
import sai.graph.GraphFactory;

/** manages all plugin classes
 *
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
    
    
    public static final List<Class<?>> PLUGINS = 
            Lists.newCopyOnWriteArrayList(); //thread safe
    
    
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
            //
            //validate the request
            //
            
            // make sure an action was set
            Map<String,String[]> params = request.getParameterMap();
            if(!params.containsKey("plugin") || 
                    params.get("plugin").length != 1) {
                response.sendError(400, "no plugin indicated");
                return;
            }
            String classname = params.get("plugin")[0];
            Class<?> plugin = null;
            try {
                plugin = Class.forName(classname); // load the class
            } catch(ClassNotFoundException e) {
                response.sendError(400, "improper or nonexistant plugin");
                return;
            }
            
            // TODO: ensure the plugin class is concrete
            if(Modifier.isAbstract(plugin.getModifiers())) {
                response.sendError(400, "plugin class must be instantiable");
                return;
            }
            
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
            // request is valid, process it
            //
            
            PLUGINS.add(plugin);
            if(DBInterface.class.isAssignableFrom(plugin)) {
                Databases.INTERFACES.add((Class<? extends DBInterface>)plugin);
            } else if(GraphFactory.class.isAssignableFrom(plugin)) {
                Graphs.INTERFACES.add((Class<? extends GraphFactory>)plugin);
            }
            
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
