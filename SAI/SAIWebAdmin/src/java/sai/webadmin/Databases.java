/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sai.webadmin;

import com.google.common.collect.Sets;
import info.kendall_morwick.funcles.T2;
import info.kendall_morwick.funcles.Tuple;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sai.db.DBInterface;

/** manages database connection instances and interfaces.
 *
 * @author jmorwick
 */
@WebServlet(name = "Databases", urlPatterns = {"/Databases"})
public class Databases extends HttpServlet {
   
    public static Set<T2<String, DBInterface>> getDBInterfaces() {
        Set<T2<String, DBInterface>> acc = Sets.newHashSet();
        for(Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
            if(e.getValue() instanceof DBInterface)
                acc.add(Tuple.makeTuple(e.getKey(), 
                        (DBInterface)e.getValue()));
        }
        return acc;
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
                
                response.sendRedirect(".");
            } else if(action.equals("disconnect")) {
                response.sendRedirect(".");
            } 
            
            response.sendError(400, "invalid arguments");
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
