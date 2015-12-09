package cl.intelidata.servlet;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.Text;
import cl.intelidata.util.jspmkrfn;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class ExportServlet extends HttpServlet {
    private static final Logger logger           = Logger.getLogger(ExportServlet.class);
    public static int           ID_CLIENTE;
    public static int           ID_SECTOR;
    public static String        NOMBRE_CLIENTE;

    private static final long   serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public ExportServlet() {
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
     * @param request
     * @param response
     * 
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HELLO WORLD!...</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    /**
     * @param request
     * @param response
     * 
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String action = request.getParameter("action");
            HttpSession session = request.getSession();

            // BEGIN VALIDATE USER LOGIN
            if (ApiServlet.ValidateUserLogin(request, response, session)) {
                return;
            }
            // END VALIDATE USER LOGIN

            // ID_CLIENTE = 1;
            ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
            ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());
            NOMBRE_CLIENTE = (String) session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_NombreCliente");

            if (action.equalsIgnoreCase("excel")) {

            }

            // String[] Periodos = (String[]) request.getAttribute("Periodos");
            // String[] Periodos2 = (String[])
            // request.getAttribute("Periodos2");
            // //
            // System.out.println(Periodos.length);
            // System.out.println(Periodos2.length);

            exportToExcel(request, response);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // HEADERS
        // contentType="text/html; charset=UTF-8"
        response.setDateHeader("Expires", 0); // date in the past
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache"); // HTTP/1.0

        // LOCALE
        Locale locale = Locale.getDefault();
        response.setLocale(locale);

        // SESSION
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(30 * 60);

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        PrintWriter out = null;
        ResultSet rs = null;
        Statement stmt = null;

        String NOMBRE_MOMENTO = "";

        // try {
        // PRINTER
        out = response.getWriter();

        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        // for (Enumeration<String> e = session.getAttributeNames();
        // e.hasMoreElements(); ) {
        // String attribName = (String) e.nextElement();
        // Object attribValue = session.getAttribute(attribName);
        // System.out.println(attribName + "=" + attribValue);
        // }

        // VALIDATE ROLES
        int ewCurSec = ApiServlet.ValidateUserSecurity(session);
        int ewAllowList = (Integer) session.getAttribute("ewAllowList");
        int ewAllowAdd = (Integer) session.getAttribute("ewAllowAdd");

        if ((ewCurSec & ewAllowList) != ewAllowList) {
            response.sendRedirect("index.jsp");
            response.flushBuffer();
            return;
        }

        if ((ewCurSec & ewAllowAdd) == ewAllowAdd) {

            // GET PARAMETERS
            String currTime = request.getParameter("currTime");
            String periodo_0 = request.getParameter("periodo_0");
            String periodo_1 = request.getParameter("periodo_1");
            String periodo_2 = request.getParameter("periodo_2");
            String periodo_3 = request.getParameter("periodo_3");

            // MOMENTO
            String filterMoment = "";
            try {
                String moment = request.getParameter("moment");

                if (moment == null || moment == "") {
                    filterMoment = "";
                } else {
                    NOMBRE_MOMENTO = getMomentName(request, moment);
                    filterMoment = " AND respuesta.id_momento = " + moment;
                }
            } catch (Exception ex) {
                logger.error(ex.toString());
            }
            String filtros = "";

            String strSQL_case01 = "";
            String strSQL_case011 = "";
            String strSQL_case02 = "";
            String strSQL_case022 = "";

            if (periodo_0 != "" && periodo_1 != "") {
                if (periodo_0.equals(periodo_1)) {
                    strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                    strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
                } else {
                    strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '"
                            + periodo_1 + "' THEN 'ACTUAL' ";
                    strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1
                            + "' ";
                }
            } else if (periodo_0 == "" && periodo_1 != "") {
                strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
            }

            if (periodo_2 != "" && periodo_3 != "") {
                if (periodo_2.equals(periodo_3)) {
                    strSQL_case02 = " WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
                    strSQL_case022 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' ";
                } else {
                    strSQL_case02 = " WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3
                            + "' THEN 'ANTERIOR' ";
                    strSQL_case022 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3
                            + "' ";
                }

            }

            String strSQL_Cab = strSQL_case01;
            if (strSQL_case02 != "")
                strSQL_Cab = strSQL_Cab + " " + strSQL_case02 + " ";
            strSQL_Cab = strSQL_Cab + " END AS CS_Tipo_Fecha, ";

            // if (filtros != "") filtros = filtros + " AND ";
            filtros = filtros + " ( " + strSQL_case011;

            if (strSQL_case02 != "")
                filtros = filtros + " or " + strSQL_case022;

            filtros = filtros + ") AND ";

            String str_Exportar = "SELECT \n"
                    + "\tCASE WHEN ISNULL(usuario.nombre_usuario) THEN '' ELSE usuario.nombre_usuario END AS Nombre_Usuario, \n"
                    + "\tCASE WHEN ISNULL(usuario.rut_usuario) THEN '' ELSE usuario.rut_usuario END AS RUT_Usuario, \n"
                    + "\tCASE WHEN ISNULL(usuario.edad_usuario) THEN '' ELSE usuario.edad_usuario END AS Edad_Usuario, \n"
                    + "\tCASE WHEN ISNULL(usuario.genero_usuario) THEN '' ELSE usuario.genero_usuario END AS Genero_Usuario, \n"
                    + "\tCASE WHEN ISNULL(usuario.correo_usuario) THEN '' ELSE usuario.correo_usuario END AS Correo_Usuario, \n"
                    + "\tCASE WHEN ISNULL(usuario.desea_correo_usuario) THEN '' ELSE CASE WHEN usuario.desea_correo_usuario = 'NO' THEN 'DESINSCRITO' ELSE 'INSCRITO' END END AS Desinscrito_Usuario, \n"
                    + "\tCASE WHEN ISNULL(momento_encuesta.descripcion_momento) THEN '' ELSE momento_encuesta.descripcion_momento END AS Momento, \n"
                    + "\tCASE WHEN ISNULL(canal.descripcion_canal) THEN '' ELSE canal.descripcion_canal END AS Canal_Ingreso,\n"
                    + "\tCASE WHEN ISNULL(DATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m')) THEN '' ELSE DATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') END AS Periodo, \n"
                    + "\tCASE WHEN ISNULL(cliente_respuesta.ultima_respuesta) THEN '' ELSE cliente_respuesta.ultima_respuesta END AS Fecha_Encuesta, \n"
                    + "\tCASE WHEN ISNULL(estado.descripcion_estado) THEN '' ELSE estado.descripcion_estado END AS Estado_Encuesta, \n"
                    + "\tCASE WHEN ISNULL(cliente_respuesta.id_cliente_respuesta) THEN '' ELSE cliente_respuesta.id_cliente_respuesta END AS id_cliente_respuesta, \n"
                    + "\tCASE WHEN ISNULL(pregunta_cabecera.numero_pregunta) THEN '' ELSE pregunta_cabecera.numero_pregunta END AS numero_pregunta, \n"
                    + "\t-- CASE WHEN ISNULL(pregunta_cabecera.descripcion_1) THEN '' ELSE pregunta_cabecera.descripcion_1 END AS descripcion_1, \n"
                    + "\tCASE WHEN ISNULL(respuesta_detalle.valor1) THEN '' ELSE respuesta_detalle.valor1 END AS valor1, \n"
                    + "\tCASE WHEN ISNULL(respuesta_detalle.valor2) THEN '' ELSE respuesta_detalle.valor2 END AS valor2 \n" 
                    + "FROM respuesta_detalle \n"
                    + "INNER JOIN  respuesta ON respuesta.id_respuesta = respuesta_detalle.id_respuesta \n"
                    + "INNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta \n" 
                    + "LEFT JOIN usuario ON respuesta.id_usuario = usuario.id_usuario \n"
                    + "INNER JOIN canal ON respuesta.id_canal = canal.id_canal \n" 
                    + "INNER JOIN estado ON cliente_respuesta.id_estado = estado.id_estado \n"
                    + "INNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera \n"
                    + "INNER JOIN momento_encuesta ON momento_encuesta.id_cliente = respuesta.id_cliente \n" 
                    + " AND momento_encuesta.id_momento = respuesta.id_momento \n" 
                    + "WHERE " + filtros
                    + "respuesta.id_cliente = " + ID_CLIENTE + " \n" 
                    + "AND momento_encuesta.id_cliente = respuesta.id_cliente \n"
                    + filterMoment + " \n"
                    + " -- AND pregunta_cabecera.id_encuesta = momento_encuesta.id_encuesta \n" 
                    + "ORDER BY cliente_respuesta.id_cliente_respuesta, respuesta.id_pregunta_cabecera  \n";

            if (NOMBRE_MOMENTO != "") {
                NOMBRE_MOMENTO = "_" + NOMBRE_MOMENTO;
            }
            // Salidas de archivo
            // 1.- HTML
            // response.setContentType("text/html");
            // 2.- CSV
            // response.setContentType("text/csv");
            // response.setHeader("Content-Disposition", "inline; filename=" + NOMBRE_CLIENTE + NOMBRE_MOMENTO + "_" + currTime + ".csv");
            // 3.- XSLX
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "inline; filename=" + NOMBRE_CLIENTE + NOMBRE_MOMENTO + "_" + currTime + ".xls");
            // 4.- TXT
            // response.setContentType("text/csv");
            // response.setHeader("Content-Disposition", "inline; filename=" + NOMBRE_CLIENTE + NOMBRE_MOMENTO + "_" + currTime + ".csv");
            
            try {
                stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery(str_Exportar);

                if (rs.last()) {

                    out.println("<table border='1'>");

                    // BEGIN CABECERA TABLA
                    out.println("<thead>");
                    out.println("<tr>");
                    out.print("<td>Nombre_Usuario</td>");
                    out.print("<td>RUT_Usuario</td>");
                    out.print("<td>Edad_Usuario</td>");
                    out.print("<td>Genero_Usuario</td>");
                    out.print("<td>Correo_Usuario</td>");
                    out.print("<td>Desinscrito_Usuario</td>");
                    out.print("<td>Momento</td>");
                    out.print("<td>Canal_Ingreso</td>");
                    out.print("<td>Periodo</td>");
                    out.print("<td>Fecha_Encuesta</td>");
                    out.print("<td>Estado_Encuesta</td>");
                    out.println("<td>Pregunta 01 Nota</td>");
                    out.println("<td>Pregunta 01 Motivo</td>");
                    out.println("<td>Pregunta 02 Nota</td>");
                    out.println("<td>Pregunta 02 Motivo</td>");
                    out.println("<td>Pregunta 03 Nota</td>");
                    out.println("<td>Pregunta 03 Motivo</td>");
                    out.println("<td>Pregunta 04 Recomienda</td>");
                    out.println("<td>Clasificacion Cliente Nota</td>");
                    out.println("<td>Clasificacion Cliente Estado</td>");
                    out.println("</tr>");
                    out.println("</thead>");
                    // END CABECERA TABLA

                    // BEGIN DATOS
                    out.println("<tbody>");

                    Double clasifica_alumno = 0.0;
                    rs.beforeFirst();

                    while (rs.next()) { // 15
                        if (rs.getString("numero_pregunta").equalsIgnoreCase("1")) {
                            out.println("<tr>");
                            clasifica_alumno = 0.0;

                            out.print("<td>" + rs.getString("Nombre_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("RUT_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("Edad_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("Genero_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("Correo_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("Desinscrito_Usuario") + "</td>");
                            out.print("<td>" + rs.getString("Momento") + "</td>");
                            out.print("<td>" + rs.getString("Canal_Ingreso") + "</td>");
                            out.print("<td>" + rs.getString("Periodo") + "</td>");
                            out.print("<td>" + rs.getString("Fecha_Encuesta") + "</td>");
                            out.print("<td>" + rs.getString("Estado_Encuesta") + "</td>");
                            out.print("<td>" + Double.valueOf(rs.getString("valor1")) + "</td>");
                            out.print("<td>" + rs.getString("valor2") + "</td>");
                            clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getString("valor1"));

                        } else {
                            if (rs.getString("numero_pregunta").equalsIgnoreCase("2") || rs.getString("numero_pregunta").equalsIgnoreCase("3")) {
                                out.print("<td>" + Double.valueOf(rs.getString("valor1")) + "</td>");
                                out.print("<td>" + rs.getString("valor2") + "</td>");
                                clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getString("valor1"));
                            }
                            if (rs.getString("numero_pregunta").equalsIgnoreCase("4")) {
                                out.print("<td>" + rs.getString("valor2") + "</td>");

                                clasifica_alumno = clasifica_alumno / 3;
                                out.print("<td>" + jspmkrfn.EW_FormatNumber(String.valueOf(clasifica_alumno), 1, 0, 0, 0, locale) + "</td>");

                                if (clasifica_alumno >= 6.0) {
                                    out.println("<td>Promotor</td>");
                                } else if (clasifica_alumno <= 4.0) {
                                    out.println("<td>Detractor</td>");
                                } else {
                                    out.print("<td>Neutro</td>");
                                }
                                out.println("</tr>");
                            }
                        }
                    }
                    out.println("</tr>");
                    out.println("</tbody>");
                    out.println("</table>");

                } else {
                    // String msg =
                    // ApiServlet.bs_alert("No existen respuestas");
                    out.println("NO EXISTEN RESPUESTAS");
                }

            } catch (Exception ex) {
                logger.error(ex.toString());
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error(e.toString());
                }

                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error(e.toString());
                }
                dbtools.disconnectDB();
            }

            out.close();
        } else {
            String msg = ApiServlet.bs_errors("No tiene permisos para exportar datos");
            request.getSession().setAttribute("messagesPanel", msg);
            response.sendRedirect(request.getContextPath() + "/PanelServlet");
        }
        // out.close();
    }

    /**
     * 
     * @param request
     * @param ID_MOMENTO
     * @return
     */
    public static String getMomentName(HttpServletRequest request, String ID_MOMENTO) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        String strSQL_moments = "";

        ResultSet rs = null;
        Statement stmt = null;

        String name = "";

        try {
            HttpSession session = request.getSession();

            ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());

            // SELECT ALL MOMENTS BY CLIENT
            strSQL_moments = "SELECT \n" 
                    + "momento_encuesta.descripcion_momento \n" 
                    + "FROM momento_encuesta \n"                    
                    + "WHERE momento_encuesta.id_cliente = " + ID_CLIENTE + " \n"
                    + "AND momento_encuesta.id_momento = " + ID_MOMENTO + " \n"
                    + "GROUP BY momento_encuesta.id_momento \n" 
                    + "ORDER BY momento_encuesta.id_momento ASC";

            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_moments);

            while (rs.next()) {
                name = rs.getString("descripcion_momento");
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbtools.disconnectDB();
        }

        return name;
    }
}
