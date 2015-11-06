package cl.intelidata.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.Text;

public class LoginServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginServlet.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public LoginServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setDateHeader("Expires", 0); // date in the past
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache"); // HTTP/1.0

        Locale locale = Locale.getDefault();
        response.setLocale(locale);

//        HttpSession session = request.getSession();
//        session.setAttribute("ewAllowAdd", 1);
//        session.setAttribute("ewAllowDelete", 2);
//        session.setAttribute("ewAllowEdit", 4);
//        session.setAttribute("ewAllowView", 8);
//        session.setAttribute("ewAllowList", 8);
//        session.setAttribute("ewAllowSearch", 8);
//        session.setAttribute("ewAllowAdmin", 16);
        
//        for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); ) {     
//            String attribName = (String) e.nextElement();
//            Object attribValue = session.getAttribute(attribName);
//            System.out.println(attribName + "=" + attribValue);
//        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        boolean validpwd = false;

        String sql = "";
        String escapeString = "\\\\'";
        String ClaveDura = "5a1ed03731bbdf75efe8b9e8516ad815"; // CustomeR

        HttpSession session = request.getSession();
        String userid = request.getParameter("username");
        String passwd = request.getParameter("password");
        String passwd2 = passwd;

        if (userid != null && userid.length() > 0 && passwd != null && passwd.length() > 0) {

            StringBuffer hashedpasswd = hashedPassword(passwd2);
            DatabaseTools dbtools = new DatabaseTools();

            if (!dbtools.connectDB()) {
                request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
                                + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + "Error al conectar con la BBDD!.." + "</div>" + "</div>" + "</div>");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            Statement stmt = null;
            ResultSet rs = null;

            try {
                stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                if (("admin".toUpperCase().equals(userid.toUpperCase())) && (ClaveDura.equals(hashedpasswd.toString()))) {
                    session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_User", "admin");
                    session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserID", 0);
                    session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserLevel", new Integer(-1)); // System

                    // administrator
                    sql = "INSERT INTO cs_usuarios_log (idusuario) VALUES (0)";
                    stmt.executeUpdate(sql);
                    validpwd = true;
                    System.out.println("Administrator Sign In");
                }

                if (!validpwd) {

                    System.out.println("Serching user");
                    sql = "SELECT \n" + "cs_usuarios.*,\n" + "cliente.nombre_cliente,\n" + "cliente.id_sector,\n" + "cliente.id_plan,\n" + "cliente.id_encuesta\n" + "FROM cs_usuarios \n"
                                    + "INNER JOIN cliente\n" + "ON cliente.id_cliente = cs_usuarios.id_cliente\n" + "WHERE 1 = 1\n" + "AND usuario = '" + userid.replaceAll("'", escapeString) + "' \n"
                                    + "AND activo = 1;";
                    rs = stmt.executeQuery(sql);

                    if (rs.next()) {
                        if (rs.getString("pwdusuario").equals(hashedpasswd.toString())) {
                            System.out.println("User found: " + rs.getString("usuario"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_User", rs.getString("usuario"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente", rs.getString("id_cliente"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_NombreCliente", rs.getString("nombre_cliente"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector", rs.getString("id_sector"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_IdPlan", rs.getString("id_plan"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_IdEncuesta", rs.getString("id_encuesta"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserID", rs.getString("id_usuario"));
                            session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserLevel", new Integer(rs.getInt("id_perfil")));

                            sql = "INSERT INTO cs_usuarios_log (idusuario) VALUES (" + rs.getString("id_usuario") + ")";
                            stmt.executeUpdate(sql);

                            rs.close();

                            sql = "SELECT Max(cliente_respuesta.id_cliente_respuesta) as Max_Reg_Rpta FROM cliente_respuesta";
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                System.out.println("Max answers: " + rs.getString("Max_Reg_Rpta"));
                                session.setAttribute("Panel_" + Text.ProyectoID00 + "_Max_Reg_Rpta", rs.getString("Max_Reg_Rpta"));
                            }

                            validpwd = true;
                        }
                    }

                }

                if (validpwd) {
                    request.setAttribute("validpwd", validpwd);
                    if (request.getParameter("remember") != null && ((String) request.getParameter("remember")).length() > 0) {
                        Cookie cookie = new Cookie("Panel_" + Text.ProyectoID00 + "_userid", new String(userid));

                        cookie.setMaxAge(365 * 24 * 60 * 60);

                        response.addCookie(cookie);
                    }
                    session.setAttribute("Panel_" + Text.ProyectoID00 + "_status", "login");
                    request.setAttribute("status_User", session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_User"));

                    String context = request.getContextPath();
                    response.sendRedirect(context + "/PanelServlet");
                } else {
                    // System.out.println("User not found.");
                    request.setAttribute("validpwd", validpwd);
                    request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
                                    + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + "Acceso denegado!.." + "</div>" + "</div>" + "</div>");

                    request.getRequestDispatcher("login.jsp").forward(request, response);

                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
            } finally {                
                session.setAttribute("ewAllowAdd", 1);
                session.setAttribute("ewAllowDelete", 2);
                session.setAttribute("ewAllowEdit", 4);
                session.setAttribute("ewAllowView", 8);
                session.setAttribute("ewAllowList", 8);
                session.setAttribute("ewAllowSearch", 8);
                session.setAttribute("ewAllowAdmin", 16);
                
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ignore) {
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ignore) {
                    }
                }
                dbtools.disconnectDB();
            }
        } else {
            validpwd = false;

            request.setAttribute("validpwd", false);
            request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
                            + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + "Ingrese su usuario y clave." + "</div>" + "</div>" + "</div>");

            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * @param password
     * 
     * @return
     * 
     * @throws NoSuchAlgorithmException
     */
    private StringBuffer hashedPassword(String password) {
        try {
            MessageDigest alg = MessageDigest.getInstance("MD5");

            alg.reset();
            alg.update(password.getBytes());

            byte[] digest = alg.digest();

            StringBuffer hashedpasswd = new StringBuffer();
            String hx;
            for (int i = 0; i < digest.length; i++) {
                hx = Integer.toHexString(0xFF & digest[i]);
                if (hx.length() == 1) {
                    hx = "0" + hx;
                }
                hashedpasswd.append(hx);
            }
            return hashedpasswd;

        } catch (NoSuchAlgorithmException nse) {
            logger.error(nse.getMessage());
        }

        return null;

    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

}
