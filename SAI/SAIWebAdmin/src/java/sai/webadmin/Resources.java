/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sai.webadmin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

/** instantiates resource and plugin classes and manages instances
 *
 * TODO: only basic testing performed -- unit tests and more extensive testing needed
 * 
 * @author jmorwick
 */
@WebServlet(name = "Resources", urlPatterns = {"/Resources"})
public class Resources extends HttpServlet {

    public static final BiMap<String,Object> RESOURCES = 
           Maps.synchronizedBiMap(HashBiMap.create());
    
    /** stores and generates a name for the resource using preferredName if possible.
     * 
     * Resources for this application include database interfaces, graph factories, 
     * report generators, and other such tools, and any other objects which are 
     * needed to create these resources (such as File objects, etc..).
     * 
     * @param resource the instance being added to the list of resources
     * @param preferredName the name preferred for this resource
     * @return the generated name for the registered resource
     */
    public static synchronized String addResource(Object resource, String preferredName) {
        
        // first, make sure it isn't already present
        if(RESOURCES.containsValue(resource))
            return RESOURCES.inverse().get(resource);
        
        //use given name if present
        String name = preferredName;
        int num = 1;
        //make sure name is unique by adding a number to the end (if necessary)
        //default name will just be "db"
        //"null" is an illegal name for a resource.
        while(name.length() == 0 || RESOURCES.containsKey(name) || name.equals("null"))
            name = preferredName + num++;
        RESOURCES.put(name, resource);
        return name;
    }
    
    /** stores the resource and generates a name for it.
     * This method defers to its overloaded counterpart for the processing and
     * simply determines a default name for the resource based on its type.
     * 
     * @param resource the instance being added to the list of resources
     * @return the generated name for the registered resource
     */
    public static synchronized String addResource(Object resource) {
        return addResource(resource, 
                resource.getClass().getSimpleName());
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
            
            //
            //validate the request
            //
            
            // make sure an action was set
            Map<String,String[]> params = request.getParameterMap();
            if(!params.containsKey("action") || params.get("action").length != 1
               || !Sets.newHashSet("create", "remove")
                       .contains(params.get("action")[0])) {
                response.sendError(400, "no proper action indicated: " + params.get("action")[0]);
                return;
            }
            
            
            
            //
            // request is valid, process it
            //
            boolean valid = true;
            String action = params.get("action")[0];
            if(action.equals("create")) {
                //use given name if present
                
                int num = 1;
                //create the new DB connection
                Object newinstance = Resources.instantiate(params);
                 
                if(newinstance == null) {
                    response.sendError(400);
                    return;
                }
                
                
                String name = Resources.addResource(newinstance);
                
                response.sendRedirect("plugins.jsp");
                return;
            } else if(action.equals("remove")) {
                if(params.containsKey("resources")) {
                    for(String resourceID : params.get("resources")) {
                        System.out.println(resourceID);
                        System.out.println(RESOURCES.containsKey(resourceID));
                        System.out.println(RESOURCES.keySet());
                        RESOURCES.remove(resourceID);
                    }
                }
                response.sendRedirect("plugins.jsp");
                return;
            } 
                
            response.sendError(400, "invalid arguments");
            
        
    }
    
    
    // helper which determines if a parameter could possibly be satisified by 
    // arguments supplied to instantiate. 
    private static boolean canSatisfyParameter(Parameter p) {
        return Sets.newHashSet("int", "long", "boolean", "short",
                "byte", "char", "float", "double", "java.lang.String"
            ).contains(p.getType().getCanonicalName()) ||
            Plugins.PLUGINS.contains(p.getType());
    }
    
    // helper which generates a portion of the form for selecting an argument
    // value
    private static String getParameterInput(Parameter p) {
        String acc =  "<li>"+
                "<input type='hidden' name='types' value='"+
                p.getType().getCanonicalName()+"'>" + 
                p.getName()+" ("+p.getType().getTypeName()+
                "): ";
        // check if string input is allowed
        if(Sets.newHashSet("int", "long", "boolean", "short",
                "byte", "char", "float", "double", "java.lang.String"
            ).contains(p.getType().getCanonicalName()))
            acc += "<input name='arguments' type='text'>";
        else if(Plugins.PLUGINS.contains(p.getType())) { 
        // locate any applicable instances for a select and include null
            acc += "<select name='arguments'>";
            acc += "  <option value='null'>null</option>";
            for(Map.Entry<String, Object> e : Resources.RESOURCES.entrySet()) {
                if(p.getType().isAssignableFrom(e.getValue().getClass())) {
                    
                    acc += "  <option value='"+e.getKey()+
                            "'>"+e.getKey()+"</option>";
                }
            }
            acc += "</select>";
        } else throw new IllegalArgumentException("no way to satisfy parameter");
        acc += "</li>";
        return acc;
    }
    /** creates an instance of the class specified by the arguments.
     * 
     * The arguments should include, at the least, a "class" argument, which 
     * holds the name of the class to be instantiated. If arguments must be 
     * passed to the constructor, there must also be an array of parameter 
     * types in "types" and an array of arguments for those parameters in 
     * "arguments". Currently, arrays, varargs, and generic types aren't 
     * supported. 
     * 
     * Arguments provided must be a standard java literal in the case of a 
     * java primitive type, any arbitrary string in the case of a String, and 
     * the name of an already resident resource or "null" in the case of 
     * any other type. 
     * 
     * If bad data is provided for arguments or a bad class name for the class
     * to be instantiated or for a parameter for a constructor, null will be
     * returned, indicating that the user made an error in the data they provided.
     * 
     * @param arguments GET/POST arguments described above
     * @return the instance that was created or null if it couldn't be created
     */
    public static Object instantiate(Map<String,String[]> arguments) {
        try {
            // get a class object for the class being instantiated
            Class typeToInstantiate = Class.forName(arguments.get("class")[0]);
            
            // determine the names of each of the parameter types for the constructor
            String[] typeNames = arguments.containsKey("types") ? 
                    arguments.get("types") : new String[0];
            
            // now determine appropriate reflection type objects for each of them
            Class[] parameterTypes = new Class[typeNames.length];
            for(int i=0; i<parameterTypes.length; i++) {
                String typeName = typeNames[i];
                // check to see if it's a primitive type, and get the appropriate
                // type instance
                if(typeName.equals("int"))
                    parameterTypes[i] = Integer.TYPE;
                else if(typeName.equals("long"))
                    parameterTypes[i] = Long.TYPE;
                else if(typeName.equals("float"))
                    parameterTypes[i] = Float.TYPE;
                else if(typeName.equals("double"))
                    parameterTypes[i] = Double.TYPE;
                else if(typeName.equals("boolean"))
                    parameterTypes[i] = Boolean.TYPE;
                else if(typeName.equals("short"))
                    parameterTypes[i] = Short.TYPE;
                else if(typeName.equals("byte"))
                    parameterTypes[i] = Byte.TYPE;
                else if(typeName.equals("char"))
                    parameterTypes[i] = Character.TYPE;
                // if it's not a primitive, it must be a fully qualified class name
                else parameterTypes[i] = Class.forName(typeName);
            }
            
            // Now parse each argument from the provided strings
            Object[] constructorArguments = new Object[typeNames.length];
            for(int i=0; i<parameterTypes.length; i++) {
                String typeName = typeNames[i];
                String arg = arguments.get("arguments")[i];
                
                // if the type is primitive, use standard java literal syntax:
                if(typeName.equals("int"))
                    constructorArguments[i] = Integer.parseInt(arg);
                else if(typeName.equals("long"))
                    constructorArguments[i] = Long.parseLong(arg);
                else if(typeName.equals("float"))
                    constructorArguments[i] = Float.parseFloat(arg);
                else if(typeName.equals("double"))
                    constructorArguments[i] = Double.parseDouble(arg);
                else if(typeName.equals("boolean"))
                    constructorArguments[i] = Boolean.parseBoolean(arg);
                else if(typeName.equals("short"))
                    constructorArguments[i] = Short.parseShort(arg);
                else if(typeName.equals("byte"))
                    constructorArguments[i] = Byte.parseByte(arg);
                else if(typeName.equals("char"))
                    constructorArguments[i] = arg.charAt(0);
                else if(typeName.equals("java.lang.String"))
                    constructorArguments[i] = arg; // if it's a string, just use the argument
                else if(arg.equals("null")) // it's not primitive, but it might be null
                    constructorArguments[i] = null;
                // otherwise, it must be an instance already resident in the 
                // resources collection. The string is the name of that 
                // instance in the collection. 
                else if(Class.forName(typeName).isAssignableFrom(
                        Resources.RESOURCES.get(arg).getClass()))
                    constructorArguments[i] = Resources.RESOURCES.get(arg);
                else return null; //no match for type
            }
            
            //instantiate the object and return it
            return typeToInstantiate.getConstructor(parameterTypes)
                    .newInstance(constructorArguments);
        } catch (ClassNotFoundException | NoSuchMethodException | 
                SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException ex) {
            return null;
        }
    }
    /** creates webform for instantiating a class via this web service
     * 
     * TODO: allow the user to specify a preferred name for the new instance
     * 
     * @param request the current servlet request
     * @param out output stream to print the HTML form to
     * @param formName base name applied to each form
     * @param action URL of this web service
     * @param method method of transferring arguments -- should be POST
     * @param c class to be instantiated
     * @throws IOException 
     */
    public static void getInstantiationForm(HttpServletRequest request, 
            JspWriter out, String formName, String action, 
            String method, Class c) throws IOException {
        out.println("  <div class='createobject' ID='create_"+
                formName+"_"+c.getCanonicalName()+"' style='display: none'>");
        out.println("    <h1> Create a " + c.getCanonicalName() + "...</h1>");
        
        boolean firstForm = true;
        int n = 1;
        //create a form for each constructor
        for(Constructor cn : c.getConstructors()) { 
            
            // determine if it's possible to provide arguments for each of
            // the parameters for this constructor...
            boolean possible = true;
            for(Parameter p : cn.getParameters()) 
                if(!canSatisfyParameter(p))
                    possible = false;
            if(!possible) { // if it's not, skip this constructor
                continue;
            }
            
            // if another form has already been output, separate it
            if (!firstForm) {
                out.println("    <hr/>");
            }
            
            // print the content of this form
            out.println("    <form action = '" + action + "' method = '" + method +
                    "'>");
            out.println("      <h2>Constructor #" + n++ + "</h2>");
            for(Parameter p : cn.getParameters()) {
                out.println("      " + getParameterInput(p));
            }
            
            out.println("      <input type='submit' name='action' value='create'/>");
            out.println("      <input type='hidden' name='class' value='"+
                    c.getCanonicalName()+"'/>");
            out.println("    </form>");
            firstForm = false;
        }
            out.println("    <input type='submit' value='cancel' "+
                    "onClick='hideCreationForm(\""+
                    formName+"\", \""+c.getCanonicalName()+"\")'>");
        out.println("  </div>");
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
