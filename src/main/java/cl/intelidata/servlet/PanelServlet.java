package cl.intelidata.servlet;

import cl.intelidata.conf.Text;
import cl.intelidata.tools.DatabaseTools;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@WebServlet("/PanelServlet")
public class PanelServlet extends HttpServlet {

    /**
     * Constructor of the object.
     */
    public PanelServlet() {
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
     * The doDelete method of the servlet. <br>
     * <p>
     * This method is called when a HTTP delete request is received.
     * <p>
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to get.
     * <p>
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("INIT PANEL");

        HttpSession session = request.getSession();

        String login = (String) session.getAttribute("Panel_" + Text.ProyectoID00 + "_status");

        System.out.println(login);

        if (login == null || !login.equals("login")) {
            System.out.println("Session.......Failed");
            String context = request.getContextPath();
            response.sendRedirect(context + "/LoginServlet");
            response.flushBuffer();

            HttpSession newsession = request.getSession(false);
            if (newsession != null) {
                newsession.invalidate();
            }
            return;
        }
        System.out.println("Session.......OK");

        String sql = "";

        String fileName = "C:\\Users\\Maze\\Google Drive\\Intelidata\\Gemini's\\2015\\1231\\panelExperiencia.properties";

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB(fileName);

        // BEGIN SECURITY ------------------
        sql = "SELECT * from cs_perfiles_permisos where id_modulo = 1 order by id_perfil ASC";
        validateSecurity(session, sql, dbtools);
        // END SECURITY ------------------

        /**
         *
         */
        String[] Periodos = new String[100];
        String[] Periodos2 = new String[100];
        String[] Array_Facultades = new String[200];
        String[] Array_Campus = new String[200];
        String ultimo_periodo = "";
        String ultimo_periodo_f = "";
        int Fila_Facultad = 0;
        int Fila_Campus = 0;

        ResultSet rs = null;
        Statement stmt = null;

        int rowcount = 0;
        int Fila = 0;

        try {
            // ************************

            try {
                System.out.println("LOAD PERIODOS");
                stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                sql = "SELECT DISTINCT Periodo, anio, mes FROM cs_periodos order by Periodo DESC, anio DESC, mes DESC";
                rs = stmt.executeQuery(sql);

                if (rs.last()) {
                    rowcount = rs.getRow();
                    rs.beforeFirst();

                    while (rs.next()) {
                        long length = rs.getClob("Periodo").length();

                        if (ultimo_periodo_f.equals("")) {
                            ultimo_periodo_f = rs.getString("Periodo");
                        }

                        ultimo_periodo = rs.getString("Periodo");
                        Periodos[Fila] = ultimo_periodo;
                        Periodos2[Fila] = ultimo_periodo;
                        Fila = Fila + 1;
                    }
                } else {
                    Periodos[0] = "2000-01";
                    Periodos2[0] = "2000-1";
                }
                rs.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }

//          // ************************
            try {
                System.out.println("LOAD MOMENTOS");

                sql = "SELECT descripcion_momento FROM momento GROUP BY descripcion_momento order by id_momento";
                rs = stmt.executeQuery(sql);

                Fila_Facultad = 0;

                if (rs.last()) {
                    rowcount = rs.getRow();

                    rs.beforeFirst();

                    while (rs.next()) {
                        Array_Facultades[Fila_Facultad] = rs.getString("descripcion_momento");
                        Fila_Facultad = Fila_Facultad + 1;
                    }
                } else {
                    Array_Facultades[0] = "NN";
                }

                rs.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }

            // // ************************
            // try {
            // sql =
            // "SELECT bdd_umayor.campus FROM bdd_umayor GROUP BY bdd_umayor.campus order by bdd_umayor.campus";
            // rs = dbtools.exeQuery(sql);
            //
            // Fila_Campus = 0;
            //
            // if (rs.last()) {
            // rowcount = rs.getRow();
            //
            // rs.beforeFirst();
            //
            // while (rs.next()) {
            // Array_Campus[Fila_Campus] = rs.getString("campus");
            // Fila_Campus = Fila_Campus + 1;
            // }
            // } else {
            // Array_Campus[0] = "NN";
            // }
            // rs.close();
            //
            // rs = null;
            //
            // } catch (SQLException ex) {
            // System.out.println(ex.toString());
            // }
            request.setAttribute("Periodos", Periodos);
            request.setAttribute("Periodos2", Periodos2);
            request.setAttribute("Array_Facultades", Array_Facultades);
            request.setAttribute("Array_Campus", Array_Campus);
            request.setAttribute("ultimo_periodo", ultimo_periodo);
            request.setAttribute("ultimo_periodo_f", ultimo_periodo_f);
            request.setAttribute("Fila_Facultad", Fila_Facultad);
            request.setAttribute("Fila_Campus", Fila_Campus);

            request.getRequestDispatcher("panel.jsp").forward(request, response);
            //
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                }
            }
            if (dbtools.getConexion() != null) {
                try {
                    dbtools.getConexion().close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    /**
     * @param session
     * @param sql
     * @param dbtools
     * @throws SQLException
     */
    @SuppressWarnings("unused")
    private void validateSecurity(HttpSession session, String sql, DatabaseTools dbtools) {
        try {
            Statement stmtSeguridad = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rsSeguridad = null;
            rsSeguridad = stmtSeguridad.executeQuery("SELECT * from cs_perfiles_permisos where id_modulo = 1 order by id_perfil ASC");
            int rowcountSeguridad = 0;

            ArrayList ew_SecTableSeguridad = new ArrayList();

            if (rsSeguridad.last()) {
                rowcountSeguridad = rsSeguridad.getRow();

                rsSeguridad.beforeFirst();
                while (rsSeguridad.next()) {
                    ew_SecTableSeguridad.add(rsSeguridad.getInt("permisos"));
                }
            }

            rsSeguridad.close();
            rsSeguridad = null;
            stmtSeguridad.close();
            stmtSeguridad = null;

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    /**
     * The doPost method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to
     * post.
     * <p>
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("    This is ");
        out.print(this.getClass());
        out.println(", using the POST method");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    /**
     * The doPut method of the servlet. <br>
     * <p>
     * This method is called when a HTTP put request is received.
     * <p>
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Put your code here
    }

    public void doSome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * Returns information about the servlet, such as author, version, and
     * copyright.
     * <p>
     * @return String information about this servlet
     */
    public String getServletInfo() {
        return "This is my default servlet created by Eclipse";
    }

    /**
     * Initialization of the servlet. <br>
     * <p>
     * @throws ServletException if an error occurs
     */
    public void init() throws ServletException {
        // Put your code here
    }

}
