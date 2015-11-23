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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

class ApiServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ApiServlet.class);
    public static int ID_CLIENTE;
    public static int ID_SECTOR;
    public static int ENCONTRO_ANTERIOR = 0;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of the object.
     */
    public ApiServlet() {
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * @param request
     * @param response
     * 
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN
        
        ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
        ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());

        try {
            if (action.toLowerCase().equalsIgnoreCase("export")) {
                exportToExcel(request, response);
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * GET PERIODS FROM CS_PERIODOS TABLE
     * 
     * @return
     */
    public static String[] getPeriods() {
        int count = 0;
        String[] periodos = new String[100];

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        ResultSet rs = null;
        Statement stmt = null;

        String ultimo_periodo_f = "";
        String ultimo_periodo = "";

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT DISTINCT periodo, anio, mes FROM cs_periodos ORDER BY periodo DESC, anio DESC, mes DESC";
            rs = stmt.executeQuery(sql);

            if (rs.last()) {
                rs.beforeFirst();

                while (rs.next()) {
                    // long length = rs.getClob("periodo").length();

                    if (ultimo_periodo_f.equals("")) {
                        ultimo_periodo_f = rs.getString("periodo");
                    }

                    ultimo_periodo = rs.getString("Periodo");
                    periodos[count] = ultimo_periodo;
                    count++;
                }
            } else {
                periodos[0] = "2000-01";
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return periodos;
    }

    /**
     * VALIDATE USER SECURITY
     * 
     * @param session
     */
    public static int ValidateUserSecurity(HttpSession session) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        Statement stmtSeguridad = null;
        ResultSet rsSeguridad = null;
        String sql = "SELECT * FROM cs_perfiles_permisos WHERE id_modulo = 1 ORDER BY id_perfil ASC";
        int ewCurSec = 0; // initialise
        // int ID_Perfil = 0;

        try {
            stmtSeguridad = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rsSeguridad = stmtSeguridad.executeQuery(sql);
            int rowcountSeguridad = 0;

            ArrayList<Integer> ew_SecTableSeguridad = new ArrayList<Integer>();

            if (rsSeguridad.last()) {
                rowcountSeguridad = rsSeguridad.getRow();

                rsSeguridad.beforeFirst();

                while (rsSeguridad.next()) {
                    ew_SecTableSeguridad.add(rsSeguridad.getInt("permisos"));
                }
            }

            // get current table security

            if (session.getAttribute("Panel_" + cl.intelidata.util.Text.ProyectoID00 + "_status_UserLevel") != null) {
                int ewCurIdx = ((Integer) session.getAttribute("Panel_" + cl.intelidata.util.Text.ProyectoID00 + "_status_UserLevel")).intValue();
                // ID_Perfil = ewCurIdx;
                if (ewCurIdx == -1) { // system administrator
                    ewCurSec = 31;
                } else if (ewCurIdx > 0 && ewCurIdx <= rowcountSeguridad) {
                    // ewCurSec = ew_SecTable[ewCurIdx-1];
                    ewCurSec = (Integer) ew_SecTableSeguridad.get(ewCurIdx - 1);
                }
            }

        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            try {
                rsSeguridad.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                stmtSeguridad.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbtools.disconnectDB();
        }

        return ewCurSec;
    }

    /**
     * VALIDATE USER LOGIN
     * 
     * @param request
     * @param response
     * @param session
     * 
     * @return
     * 
     * @throws java.io.IOException
     */
    public static boolean ValidateUserLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

        String login = (String) session.getAttribute("Panel_" + cl.intelidata.util.Text.ProyectoID00 + "_status");

        String message = "";
        // String context = request.getContextPath();

        try {
            if (login == null) {
                logger.warn("LOGIN SESSION FAIL");

                message = "Session desconocida...";

                request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
                                + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + message + "</div>" + "</div>" + "</div>");

                request.getRequestDispatcher("login.jsp").forward(request, response);
                // response.sendRedirect(context + "/LoginServlet");
                response.flushBuffer();

                HttpSession newsession = request.getSession(false);
                if (newsession != null) {
                    newsession.invalidate();
                }
                return true;
            }

            if (!login.equals("login")) {

                logger.warn("LOGIN SESSION FAIL");

                message = "Sesion expirada!...";

                request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
                                + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + message + "</div>" + "</div>" + "</div>");

                request.getRequestDispatcher("login.jsp").forward(request, response);
                // response.sendRedirect(context + "/LoginServlet");
                response.flushBuffer();

                HttpSession newsession = request.getSession(false);
                if (newsession != null) {
                    newsession.invalidate();
                }
                return true;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        logger.warn("LOGIN SESSION OK");
        return false;
    }

    /**
     * GET PERIODS QUERY TO COMPLETE 'WHERE' QUERYS
     * 
     * @param periodo_0
     * @param periodo_1
     * @param periodo_2
     * @param periodo_3
     * 
     * @return
     */
    public static String getPeriodsQuery(String periodo_0, String periodo_1, String periodo_2, String periodo_3) {
        String temp = "";
        String temp2 = "";
        String sql = "";

        try {
            if (periodo_0 != null && periodo_1 != null) {
                if (periodo_0 != "" && periodo_1 != "") {
                    if (periodo_0.equals(periodo_1)) {
                        temp = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                    } else {
                        temp = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1
                                        + "' THEN 'ACTUAL' ";
                    }
                } else if (periodo_0 == "" && periodo_1 != "") {
                    temp = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                }
            }

                if (periodo_2 != null && periodo_3 != null) {
                if (periodo_2 != "" && periodo_3 != "") {
                    if (periodo_2.equals(periodo_3)) {
                        temp2 = " WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
                    } else {
                        temp2 = " WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3
                                        + "' THEN 'ANTERIOR' ";
                    }
                }
            }

            if (temp != "") {
                sql += temp;
            }
            if (temp2 != "")
                sql += " " + temp2 + " ";
            sql += " END AS CS_Tipo_Fecha, ";

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return sql;
    }

    /**
     * GET PERIODS QUERY TO COMPLETE 'WHERE' QUERYS
     * 
     * @param periodo_0
     * @param periodo_1
     * @param periodo_2
     * @param periodo_3
     * 
     * @return
     */
    public static String getPeriodsQueryNPS(String periodo_0, String periodo_1, String periodo_2, String periodo_3) {
        String temp = "";
        String temp2 = "";
        String sql = "";

        try {
            if (periodo_0 != null && periodo_1 != null) {
                if (periodo_0 != "" && periodo_1 != "") {
                    if (periodo_0.equals(periodo_1)) {
                        temp = " CASE WHEN DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                    } else {
                        temp = " CASE WHEN DATE_FORMAT(nps.fecha,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(nps.fecha,'%Y-%m') <= '" + periodo_1 + "' THEN 'ACTUAL' ";
                    }
                } else if (periodo_0 == "" && periodo_1 != "") {
                    temp = " CASE WHEN DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
                }
            }

            if (periodo_2 != null && periodo_3 != null) {
                if (periodo_2 != "" && periodo_3 != "") {
                    if (periodo_2.equals(periodo_3)) {
                        temp2 = " WHEN DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
                    } else {
                        temp2 = " WHEN DATE_FORMAT(nps.fecha,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(nps.fecha,'%Y-%m') <= '" + periodo_3 + "' THEN 'ANTERIOR' ";
                    }
                }
            }

            if (temp != "") {
                sql += temp;
            }
            if (temp2 != "")
                sql += " " + temp2 + " ";
            sql += " END AS CS_Tipo_Fecha, ";

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return sql;
    }

    /**
     * GET FILTERS TO COMPLETE QUERYS
     * 
     * @param periodo_0
     * @param periodo_1
     * @param periodo_2
     * @param periodo_3
     * 
     * @return
     */
    public static String getFiltersQuery(String periodo_0, String periodo_1, String periodo_2, String periodo_3) {
        String temp = "";
        String temp2 = "";
        String filter = "";

        try {
            if (periodo_0 != null && periodo_1 != null) {
                if (periodo_0 != "" && periodo_1 != "") {
                    if (periodo_0.equals(periodo_1)) {
                        temp = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
                    } else {
                        temp = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' ";
                    }
                } else if (periodo_0 == "" && periodo_1 != "") {
                    temp = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
                }
            }

            if (periodo_2 != null && periodo_3 != null) {
                if (periodo_2 != "" && periodo_3 != "") {
                    if (periodo_2.equals(periodo_3)) {
                        temp2 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' ";
                    } else {
                        temp2 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' ";
                    }
                }
            }

            filter += " ( ";
            if (temp != "") {
                filter += temp;
            }

            // if (temp2 != "") {
            if (temp2 != "" && temp != "") {
                filter += " OR " + temp2;
            }
            filter += " ) ";

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return filter;
    }

    /**
     * GET FILTERS TO COMPLETE QUERYS
     * 
     * @param periodo_0
     * @param periodo_1
     * @param periodo_2
     * @param periodo_3
     * 
     * @return
     */
    public static String getFiltersQueryNPS(String periodo_0, String periodo_1, String periodo_2, String periodo_3) {
        String temp = "";
        String temp2 = "";
        String filter = "";

        try {
            if (periodo_0 != null && periodo_1 != null) {
                if (periodo_0 != "" && periodo_1 != "") {
                    if (periodo_0.equals(periodo_1)) {
                        temp = " DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_1 + "' ";
                    } else {
                        temp = " DATE_FORMAT(nps.fecha,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(nps.fecha,'%Y-%m') <= '" + periodo_1 + "' ";
                    }
                } else if (periodo_0 == "" && periodo_1 != "") {
                    temp = " DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_1 + "' ";
                }
            }

            if (periodo_2 != null && periodo_3 != null) {
                if (periodo_2 != "" && periodo_3 != "") {
                    if (periodo_2.equals(periodo_3)) {
                        temp2 = " DATE_FORMAT(nps.fecha,'%Y-%m') = '" + periodo_3 + "' ";
                    } else {
                        temp2 = " DATE_FORMAT(nps.fecha,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(nps.fecha,'%Y-%m') <= '" + periodo_3 + "' ";
                    }
                }
            }

            filter += " ( ";
            if (temp != "") {
                filter += temp;
            }

            // if (temp2 != "") {
            if (temp2 != "" && temp != "") {
                filter += " OR " + temp2;
            }
            filter += " ) ";

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return filter;
    }

    /**
     * GENERATE PREGUNTAS QUERY FOR CREATE DATATABLE
     * 
     * @param Largo_Array
     * @param strSQL_Preguntas
     * 
     * @return
     */
    public static String[][] generateQuestionsQuery(String strSQL_Preguntas, int Largo_Array) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        ResultSet rs = null;
        Statement stmt = null;

        String[][] arrayPreguntas = new String[Largo_Array][7]; // null
        String ID_Pregunta_Str = "";

        try {
            for (int i = 0; i < Largo_Array; i++) {
                arrayPreguntas[i][0] = String.valueOf(i);
                arrayPreguntas[i][1] = "0.0";
                arrayPreguntas[i][2] = "0.0";
                arrayPreguntas[i][3] = "0.0";
                arrayPreguntas[i][4] = "0.0";
                arrayPreguntas[i][5] = "0.0";
                arrayPreguntas[i][6] = "0.0";
            }

            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_Preguntas);

            if (rs.last()) {
                // int rowcount = rs.getRow();
                rs.beforeFirst();
                while (rs.next()) {
                    for (int i = 1; i < Largo_Array; i++) {
                        ID_Pregunta_Str = rs.getString("numero_pregunta");

                        // COMPLETA EL ARRAY DE DATOS
                        if (ID_Pregunta_Str.equals(arrayPreguntas[i][0])) {
                            if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL")) {
                                arrayPreguntas[i][1] = rs.getString("NPS_7");
                                arrayPreguntas[i][2] = rs.getString("NPS_5");
                                arrayPreguntas[i][3] = rs.getString("NPS_4");
                            } else if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR")) {
                                arrayPreguntas[i][4] = rs.getString("NPS_7");
                                arrayPreguntas[i][5] = rs.getString("NPS_5");
                                arrayPreguntas[i][6] = rs.getString("NPS_4");
                            }
                            break;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return arrayPreguntas;
    }

    /**
     * FOUND OLD - METHOD TO VALIDATE IF STRSQL_PREGUNTAS HAVE 'ANTERIOR' VALUE
     * IN 'CS_TIPO_FECHA' FIELD
     * 
     * @param Largo_Array
     * @param strSQL_Preguntas
     * 
     * @return
     */
    public static int foundOld(String strSQL_Preguntas, int Largo_Array) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        ResultSet rs = null;
        Statement stmt = null;

        int found = 0;
        String[][] arrayPreguntas = new String[Largo_Array][7];
        String ID_Pregunta_Str = "";

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_Preguntas);

            if (rs.last()) {
                // int rowcount = rs.getRow();
                rs.beforeFirst();
                while (rs.next()) {
                    for (int i = 1; i < Largo_Array; i++) {
                        ID_Pregunta_Str = rs.getString("numero_pregunta");

                        // COMPLETA EL ARRAY DE DATOS
                        if (ID_Pregunta_Str.equals(arrayPreguntas[i][0])) {
                            if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL")) {
                            } else if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR")) {
                                found = 1;
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return found;
    }

    /**
     * GET ONE QUERY TO CREATE GRAPH OR TABLE
     * 
     * @param Largo_Array
     * @param arrayPreguntas
     * @param ENCONTRO_ANTERIOR
     * @param locale
     * 
     * @return
     */
    public static String genOne(int Largo_Array, String[][] arrayPreguntas, int ENCONTRO_ANTERIOR, Locale locale) {
        String html_preg_etiqueta = "";
        String html_preg = "";

        String etiqueta_porc_7 = "";
        String etiqueta_porc_4 = "";
        String etiqueta_porc_nps = "";

        Double nps_7 = 0.0;
        Double nps_5 = 0.0;
        Double nps_4 = 0.0;
        Double porc_7 = 0.0;
        Double porc_4 = 0.0;

        Double nps_7_2 = 0.0;
        Double nps_5_2 = 0.0;
        Double nps_4_2 = 0.0;
        Double porc_7_2 = 0.0;
        Double porc_4_2 = 0.0;

        try {
            for (int i = 1; i < Largo_Array; i++) {
                if (i == 1) {
                    html_preg_etiqueta = "Efectivo";
                } else if (i == 2) {
                    html_preg_etiqueta = "F&aacute;cil";
                } else if (i == 3) {
                    html_preg_etiqueta = "Agradable";
                }

                nps_7 = Double.valueOf(arrayPreguntas[i][1]);
                nps_5 = Double.valueOf(arrayPreguntas[i][2]);
                nps_4 = Double.valueOf(arrayPreguntas[i][3]);

                nps_7_2 = Double.valueOf(arrayPreguntas[i][4]);
                nps_5_2 = Double.valueOf(arrayPreguntas[i][5]);
                nps_4_2 = Double.valueOf(arrayPreguntas[i][6]);

                if ((nps_7 + nps_5 + nps_4) == 0.0) {
                    porc_7 = 0.0;
                    porc_4 = 0.0;
                } else {
                    porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
                    porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
                }

                if ((nps_7_2 + nps_5_2 + nps_4_2) == 0.0) {
                    porc_7_2 = 0.0;
                    porc_4_2 = 0.0;
                } else {
                    porc_7_2 = ((nps_7_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
                    porc_4_2 = ((nps_4_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
                }

                if (ENCONTRO_ANTERIOR == 1) {
                    if (porc_7 > porc_7_2) {
                        etiqueta_porc_7 = "&nbsp;<i title=\"Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-up\"></i>";
                    } else if (porc_7 < porc_7_2) {
                        etiqueta_porc_7 = "&nbsp;<i title=\"Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-down\"></i>";
                    } else if (porc_7.equals(porc_7_2)) {
                        etiqueta_porc_7 = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                    }

                    if (porc_4 > porc_4_2) {
                        etiqueta_porc_4 = "&nbsp;<i title=\"Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-up\"></i>";
                    } else if (porc_4 < porc_4_2) {
                        etiqueta_porc_4 = "&nbsp;<i title=\"Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-down\"></i>";
                    } else if (porc_4.equals(porc_4_2)) {
                        etiqueta_porc_4 = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                    }

                    if ((porc_7 - porc_4) > (porc_7_2 - porc_4_2)) {
                        etiqueta_porc_nps = "&nbsp;<i title=\"Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-up\"></i>";
                    } else if ((porc_7 - porc_4) < (porc_7_2 - porc_4_2)) {
                        etiqueta_porc_nps = "&nbsp;<i title=\"Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2), 0, 0, 0, 0, locale)
                                        + "%\" class=\"fa fa-long-arrow-down\"></i>";
                    } else if ((porc_7 - porc_4) == (porc_7_2 - porc_4_2)) {
                        etiqueta_porc_nps = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                    }
                }

                html_preg += "<tr>";
                html_preg += "<td class='text-left'>" + html_preg_etiqueta + "</td>";
                html_preg += "<td class='text-center'>" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7), 0, 0, 0, 0, locale) + "%" + etiqueta_porc_7 + "</td>";
                html_preg += "<td class='text-center'>" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4), 0, 0, 0, 0, locale) + "%" + etiqueta_porc_4 + "</td>";
                html_preg += "<td class='text-center'>" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7 - porc_4), 0, 0, 0, 0, locale) + "%" + etiqueta_porc_nps + "</td>";
                html_preg += "</tr>";
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return html_preg;
    }

    /**
     * GET NPS (?) VALUE
     * 
     * @param strSQL_NPS
     * @param mostrarComo
     * @param locale
     * 
     * @return
     */
    public static String genNPS(String strSQL_NPS, String mostrarComo, Locale locale) {
        Statement stmt = null;
        ResultSet rs = null;

        boolean ENCONTRO_ANTERIOR = false;

        String etiqueta_porc_7 = "";
        String etiqueta_porc_4 = "";
        String etiqueta_porc_nps = "";

        Double nps_7 = 0.0;
        Double nps_5 = 0.0;
        Double nps_4 = 0.0;
        Double porc_7 = 0.0;
        Double porc_4 = 0.0;

        Double nps_7_2 = 0.0;
        Double nps_5_2 = 0.0;
        Double nps_4_2 = 0.0;
        Double porc_7_2 = 0.0;
        Double porc_4_2 = 0.0;

        String porc_nps = "";
        // String porc_nps_2 = "";
        String porc_promotores = "";
        String porc_detractores = "";

        String out = "";

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        try {
            int promotor = 0;
            int neutro = 0;
            int detractor = 0;
            int total = 0;

            int promotor_2 = 0;
            // int neutro_2 = 0;
            int detractor_2 = 0;
            int total_2 = 0;

            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_NPS);

            while (rs.next()) {
                String CS_Tipo_Fecha = rs.getString("CS_Tipo_Fecha");

                if (CS_Tipo_Fecha.equals("ACTUAL")) {
                    if (rs.getDouble("promedio") >= 6.0) {
                        promotor++;
                    } else if (rs.getDouble("promedio") <= 4.0) {
                        detractor++;
                    } else {
                        neutro++;
                    }
                    total++;
                }

                if (CS_Tipo_Fecha.equals("ANTERIOR")) {
                    ENCONTRO_ANTERIOR = true;

                    if (rs.getDouble("promedio") >= 6.0) {
                        promotor_2++;
                    } else if (rs.getDouble("promedio") <= 4.0) {
                        detractor_2++;
                    } else {
                        // neutro_2++;
                    }
                    total_2++;
                }
            }

            nps_7 = (double) ((float) (promotor * 100) / total);
            nps_4 = (double) ((float) (detractor * 100) / total);
            nps_5 = (double) ((float) (nps_7 - nps_4));

            if ((promotor + detractor + neutro) == 0.0) {
                porc_7 = 0.0;
                porc_4 = 0.0;
                porc_nps = "0%";
                porc_promotores = "0%";
                porc_detractores = "0%";
            } else {
                porc_7 = (double) ((float) (promotor * 100) / total);
                porc_4 = (double) ((float) (detractor * 100) / total);
                porc_promotores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7), 0, 0, 0, 0, locale) + "%";
                porc_nps = jspmkrfn.EW_FormatNumber(String.valueOf(nps_5), 0, 0, 0, 0, locale) + "%";
                porc_detractores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_4), 0, 0, 0, 0, locale) + "%";
            }

            if (ENCONTRO_ANTERIOR) {
                nps_7_2 = (double) ((float) (promotor_2 * 100) / total_2);
                nps_4_2 = (double) ((float) (detractor_2 * 100) / total_2);
                nps_5_2 = (double) ((float) (nps_7_2 - nps_4_2));

                if ((nps_7_2 + nps_5_2 + nps_4_2) == 0.0) {
                    porc_7_2 = 0.0;
                    porc_4_2 = 0.0;
                } else {
                    porc_7_2 = ((nps_7_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
                    porc_4_2 = ((nps_4_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
                }

                // porc_nps_2 = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7_2
                // - nps_4_2), 0, 0, 0, 0, locale) + "%";
                // porc_nps_2 = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7_2
                // - nps_4_2), 0, 0, 0, 0, locale) + "%";

                if (porc_7 > porc_7_2) {
                    etiqueta_porc_7 = "&nbsp;<i title='Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class=\"fa fa-long-arrow-up\"></i>";
                } else if (porc_7 < porc_7_2) {
                    etiqueta_porc_7 = "&nbsp;<i title='Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class=\"fa fa-long-arrow-down\"></i>";
                } else if (porc_7.equals(porc_7_2)) {
                    etiqueta_porc_7 = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                }

                if (porc_4 > porc_4_2) {
                    etiqueta_porc_4 = "&nbsp;<i title='Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class=\"fa fa-long-arrow-up\"></i>";
                } else if (porc_4 < porc_4_2) {
                    etiqueta_porc_4 = "&nbsp;<i title='Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class=\"fa fa-long-arrow-down\"></i>";
                } else if (porc_4.equals(porc_4_2)) {
                    etiqueta_porc_4 = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                }

                if ((porc_7 - porc_4) > (porc_7_2 - porc_4_2)) {
                    etiqueta_porc_nps = "&nbsp;<i title='Mejor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class='icon icon-color icon-arrowthick-n'></span>";
                } else if ((porc_7 - porc_4) < (porc_7_2 - porc_4_2)) {
                    etiqueta_porc_nps = "&nbsp;<i title='Peor Porcentaje - Anterior " + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2), 0, 0, 0, 0, locale) + "%"
                                    + "' class='icon icon-red icon-arrowthick-s'></i>";
                } else if ((porc_7 - porc_4) == (porc_7_2 - porc_4_2)) {
                    etiqueta_porc_nps = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                }
            }
            rs.close();

            if (mostrarComo.equals("tabla")) {
                out += "<tr>";
                out += "<td class='text-left'><span title='NPS: &Iacute;ndice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></td>";
                out += "<td class='text-center'>" + porc_promotores + etiqueta_porc_7 + "</td>";
                out += "<td class='text-center'>" + porc_detractores + etiqueta_porc_4 + "</td>";
                out += "<td class='text-center'>" + porc_nps + etiqueta_porc_nps + "</td>";
                out += "</tr>";
            }

            if (mostrarComo.equals("grafico")) {
                String actual_superior2 = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7), 0, 0, 0, 0, locale);
                actual_superior2 = actual_superior2.replace(",", ".");

                String actual_inferior2 = jspmkrfn.EW_FormatNumber(String.valueOf(nps_4), 0, 0, 0, 0, locale);
                actual_inferior2 = "-" + actual_inferior2.replace(",", ".");

                String actual_diferencia2 = porc_nps;
                actual_diferencia2 = actual_diferencia2.replace(",", ".");
                actual_diferencia2 = actual_diferencia2.replace("%", "");

                if (ENCONTRO_ANTERIOR) {
                    String anterior_superior2 = jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2), 0, 0, 0, 0, locale);
                    anterior_superior2 = anterior_superior2.replace(",", ".");

                    String anterior_inferior2 = jspmkrfn.EW_FormatNumber(String.valueOf(porc_4_2), 0, 0, 0, 0, locale);
                    anterior_inferior2 = "-" + anterior_inferior2.replace(",", ".");

                    String anterior_diferencia2 = jspmkrfn.EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2), 0, 0, 0, 0, locale); // porc_nps_2;

                    anterior_diferencia2 = anterior_diferencia2.replace(",", ".");
                    anterior_diferencia2 = anterior_diferencia2.replace("%", "");

                    out += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0'>"
                                    + "\n";
                    out += "<categories >" + "\n";
                    out += "<category label='Comparar' />" + "\n";
                    out += "<category label='Inicial' />" + "\n";
                    out += "</categories>" + "\n";
                    out += "<dataset seriesName='Sat.' >" + "\n";
                    out += "<set value='" + anterior_superior2 + "' />" + "\n";
                    out += "<set value='" + actual_superior2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "<dataset seriesName='Insat.'>" + "\n";
                    out += "<set value='" + anterior_inferior2 + "' />" + "\n";
                    out += "<set value='" + actual_inferior2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "<dataset seriesName='Sat. Neta' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
                    out += "<set value='" + anterior_diferencia2 + "' />" + "\n";
                    out += "<set value='" + actual_diferencia2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "</chart>" + "\n";
                } else {
                    out += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0' yAxisMinValue='-100' yAxisMaxValue='100' showLimits='1' >"
                                    + "\n";
                    out += "<categories >" + "\n";
                    out += "<category label='Inicial' />" + "\n";
                    out += "</categories>" + "\n";
                    out += "<dataset seriesName='Prom.' >" + "\n";
                    out += "<set value='" + actual_superior2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "<dataset seriesName='Detr.'>" + "\n";
                    out += "<set value='" + actual_inferior2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "<dataset seriesName='Sat. Neta' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
                    out += "<set value='" + actual_diferencia2 + "' />" + "\n";
                    out += "</dataset>" + "\n";
                    out += "</chart>" + "\n";
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return out;
    }

    /**
     * GET NPS (?) VALUE
     * 
     * @param strSQL_NPS
     * @param mostrarComo
     * @param locale
     * 
     * @return
     */
    public static String genNPSAcumulado(String strSQL_NPS, Locale locale) {
        Statement stmt = null;
        ResultSet rs = null;

        // boolean ENCONTRO_ANTERIOR = false;

        // String etiqueta_porc_7 = "";
        // String etiqueta_porc_4 = "";
        // String etiqueta_porc_nps = "";

        Double nps_7 = 0.0;
        // Double nps_5 = 0.0;
        Double nps_4 = 0.0;
        // Double porc_7 = 0.0;
        // Double porc_4 = 0.0;

        // Double nps_7_2 = 0.0;
        // Double nps_5_2 = 0.0;
        // Double nps_4_2 = 0.0;
        // Double porc_7_2 = 0.0;
        // Double porc_4_2 = 0.0;

        // String porc_nps = "";
        // String porc_nps_2 = "";
        String porc_promotores = "";
        String porc_detractores = "";

        String output = "";

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        try {
            int promotor = 0;
            // int neutro = 0;
            int detractor = 0;
            int total = 0;

            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_NPS);

            while (rs.next()) {
                if (rs.getDouble("promedio") >= 6.0) {
                    promotor++;
                } else if (rs.getDouble("promedio") <= 4.0) {
                    detractor++;
                } else {
                    // neutro++;
                }
            }

            nps_7 = (double) ((float) (promotor * 100) / total);
            nps_4 = (double) ((float) (detractor * 100) / total);
            // nps_5 = (double) ((float) (nps_7 - nps_4));

            porc_promotores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7), 0, 0, 0, 0, locale) + "%";
            // String porc_nps = jspmkrfn.EW_FormatNumber(String.valueOf(nps_5),
            // 0, 0, 0, 0, locale) + "%";
            porc_detractores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_4), 0, 0, 0, 0, locale) + "%";

            output += "<tr>";
            output += "<td><i class='fa fa-thumbs-o-up fa-2x good'></td>";
            output += "<td>&nbsp;Promotores:</td>";
            output += "<td>&nbsp;" + porc_promotores + "</td>";
            output += "</tr>";
            output += "<tr>";
            output += "<td><i class='fa fa-thumbs-o-down fa-2x bad'></td>";
            output += "<td>&nbsp;Detractores:</td>";
            output += "<td>&nbsp;" + porc_detractores + "</td>";
            output += "</tr>";

        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return output;
    }

    /**
     * GET NUMBER OF QUESTIONS ON CUALIFICATION QUERY
     * 
     * @param strSQL_Cualificacion
     * 
     * @return
     */
    public static int getNumberOfQuestions(String strSQL_Cualificacion) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        ResultSet rs = null;
        Statement stmt = null;

        int cant_registro_rpta = 0;

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_Cualificacion);

            if (rs.last()) {
                rs.beforeFirst();
                rs.next();
                cant_registro_rpta = rs.getInt("Cs_Casos");
            }
            rs.close();
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return cant_registro_rpta;
    }

    /**
     * GET PERCENTAGE OF LOYALTY
     * 
     * @param strSQL_Lealtad
     * @param locale
     * 
     * @return
     */
    public static String getPercentageOfLoyalty(String strSQL_Lealtad, Locale locale) {
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        ResultSet rs = null;
        Statement stmt = null;

        String etiqueta_porc_7 = "";
        // String etiqueta_porc_4 = "";
        // String etiqueta_porc_nps = "";
        String porc_lealtad = "";

        Double cant_leal = 0.0;
        Double cant_leal_no = 0.0;
        Double porc_lealtad1 = 0.0;

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(strSQL_Lealtad);
            if (rs.last()) {
                int rowcount = rs.getRow();
                rs.beforeFirst();
                rs.next();

                if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL")) {
                    cant_leal = rs.getDouble("Leal_SI");
                    cant_leal_no = rs.getDouble("Leal_NO");
                    porc_lealtad1 = (cant_leal / (cant_leal + cant_leal_no)) * 100;
                    porc_lealtad = jspmkrfn.EW_FormatNumber(String.valueOf(porc_lealtad1), 0, 0, 0, 0, locale) + "%";

                    if (rowcount == 2) {
                        rs.next();
                    }
                }

                if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR")) {
                    Double cant_leal_2 = 0.0;
                    Double cant_leal_no_2 = 0.0;
                    String porc_lealtad_2 = "";
                    Double porc_lealtad1_2 = 0.0;
                    cant_leal_2 = rs.getDouble("Leal_SI");
                    cant_leal_no_2 = rs.getDouble("Leal_NO");
                    porc_lealtad1_2 = (cant_leal_2 / (cant_leal_2 + cant_leal_no_2)) * 100;
                    porc_lealtad_2 = jspmkrfn.EW_FormatNumber(String.valueOf(porc_lealtad1_2), 0, 0, 0, 0, locale) + "%";

                    if (porc_lealtad1 > porc_lealtad1_2) {
                        etiqueta_porc_7 = "&nbsp;<i title='Mejor Porcentaje - Anterior " + porc_lealtad_2 + "%" + "' class=\"fa fa-long-arrow-up\"></i>";
                    } else if (porc_lealtad1 < porc_lealtad1_2) {
                        etiqueta_porc_7 = "&nbsp;<i title='Peor Porcentaje - Anterior " + porc_lealtad_2 + "%" + "' class=\"fa fa-long-arrow-down\"></i>";
                    } else if (porc_lealtad1 == porc_lealtad1_2) {
                        etiqueta_porc_7 = "&nbsp;<i title='Porcentaje Iguales' class=\"fa fa-exchange\"></i>";
                    }
                    porc_lealtad = porc_lealtad + etiqueta_porc_7;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    System.out.println(ignore.toString());
                }
            }
            dbtools.disconnectDB();
        }

        return porc_lealtad;
    }

    /**
     * @param res
     * 
     * @return
     */
    public static int getRows(ResultSet res) {
        int totalRows = 0;
        try {
            res.last();
            totalRows = res.getRow();
            res.beforeFirst();
        } catch (Exception ex) {
            return 0;
        }
        return totalRows;
    }

    /**
     * @param request
     * @param response
     */
    public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {

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
        // String html = "";

        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        PrintWriter out = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            // PRINTER
            out = response.getWriter();

            // VALIDATE LOGIN
            if (ValidateUserLogin(request, response, session))
                return;

            // VALIDATE ROLES
            int ewCurSec = ValidateUserSecurity(session);
            int ewAllowList = (Integer) request.getAttribute("ewAllowList");
            int ewAllowAdd = (Integer) request.getAttribute("ewAllowAdd");

            if ((ewCurSec & ewAllowList) != ewAllowList) {
                response.sendRedirect("index.jsp");
                response.flushBuffer();
                return;
            }

            // GET PARAMETERS
            String currTime = request.getParameter("currTime");
            String periodo_0 = request.getParameter("periodo_0");
            String periodo_1 = request.getParameter("periodo_1");
            String periodo_2 = request.getParameter("periodo_2");
            String periodo_3 = request.getParameter("periodo_3");

            String filtro_sede = request.getParameter("sede");
            String filtro_facultad = request.getParameter("facultad");
            String filtro_campus = request.getParameter("campus");
            String filtros = "";

            if (filtro_sede != "") {
                filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.sede, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_sede + "' AND ";
            }

            if (filtro_facultad != "") {
                filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.facultad, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_facultad + "' AND ";
            }

            if (filtro_campus != "") {
                filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.campus, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_campus + "' AND ";
            }

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

            filtros = filtros + " ( " + strSQL_case011;

            if (strSQL_case02 != "")
                filtros = filtros + " or " + strSQL_case022;

            filtros = filtros + ") AND ";

            ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
            ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());

            String str_Exportar = "SELECT \n" + "\t" + strSQL_Cab + " \n" + "\tusuario.*,\n" + "\tcanal.descripcion_canal AS Canal_Ingreso,\n"
                            + "\tDATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') AS Periodo,\n" + "\tcliente_respuesta.ultima_respuesta AS Fecha_Encuesta,\n"
                            + "\testado.descripcion_estado AS Estado_Encuesta,\n" + "\tcliente_respuesta.id_cliente_respuesta,\n" + "\trespuesta.id_pregunta_cabecera,\n"
                            + "\tpregunta_cabecera.descripcion_1,\n" + "\trespuesta_detalle.valor1,\n"
                            + "\tCASE WHEN ISNULL(respuesta_detalle.valor2) THEN '---' ELSE respuesta_detalle.valor2 END AS valor2\n" + "FROM respuesta_detalle\n"
                            + "INNER JOIN  respuesta ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                            + "INNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n" + "LEFT JOIN usuario ON respuesta.id_usuario = usuario.id_usuario\n"
                            + "INNER JOIN canal ON respuesta.id_canal = canal.id_canal\n" + "INNER JOIN estado ON cliente_respuesta.id_estado = estado.id_estado\n"
                            + "INNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n" + "WHERE respuesta.id_cliente = " + ID_CLIENTE + "\n"
                            + "ORDER BY cliente_respuesta.id_cliente_respuesta, respuesta.id_pregunta_cabecera";

            if ((ewCurSec & ewAllowAdd) == ewAllowAdd) {

                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "inline; filename=CLIENTE_" + currTime + ".xls");

                // int rowcount = 0;
                stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery(str_Exportar);
                if (rs.last()) {
                    // rowcount = rs.getRow();

                    out.println("<table border='1'>");
                    ResultSetMetaData metaData = rs.getMetaData();

                    out.println("<tr>");
                    // int numberOfColumns = metaData.getColumnCount();
                    int column = 0;
                    for (column = 0; column < 19; column++) {
                        out.println("<td>" + metaData.getColumnLabel(column + 1) + "</td>");
                    }
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

                    String ultimo_id = "";
                    Double clasifica_alumno = 0.0;
                    rs.beforeFirst();
                    while (rs.next()) {
                        if (!rs.getObject(20).toString().equals(ultimo_id)) {
                            if (!ultimo_id.equals(""))
                                out.println("</tr>");

                            ultimo_id = rs.getObject(20).toString();

                            out.println("<tr>");
                            clasifica_alumno = 0.0;

                            for (column = 0; column < 19; column++) {
                                if (rs.getObject(column + 1) == null) {
                                    out.print("<td></td>");
                                } else {
                                    out.print("<td>" + rs.getObject(column + 1).toString() + "</td>");
                                }
                            }
                            out.print("<td>" + rs.getObject(23).toString() + "</td>");
                            out.print("<td>" + rs.getObject(24).toString() + "</td>");
                            clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getObject(23).toString());
                        } else {
                            if (rs.getObject(21).toString().equals("1") || rs.getObject(21).toString().equals("2") || rs.getObject(21).toString().equals("3")) {
                                out.print("<td>" + rs.getObject(23).toString() + "</td>");
                                out.print("<td>" + rs.getObject(24).toString() + "</td>");
                                clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getObject(23).toString());
                            } else if (rs.getObject(21).toString().equals("4")) {
                                if (rs.getObject(23).toString().equals("1"))
                                    out.print("<td>NO</td>");
                                else if (rs.getObject(23).toString().equals("2"))
                                    out.print("<td>SI</td>");

                                clasifica_alumno = clasifica_alumno / 3;
                                out.print("<td>" + jspmkrfn.EW_FormatNumber(String.valueOf(clasifica_alumno), 1, 0, 0, 0, locale) + "</td>");

                                if (clasifica_alumno >= 6.0) {
                                    out.println("<td>Promotor</td>");
                                } else if (clasifica_alumno <= 4.0) {
                                    out.println("<td>Detractor</td>");
                                } else {
                                    out.print("<td>Neutro</td>");
                                }
                            }
                        }
                        out.flush();
                    }
                    out.println("</tr>");
                    out.println("</table>");

                } else {
                    out.println("NO EXISTEN RESPUESTAS");
                }
                rs.close();
                // Close recordset and connection
                rs = null;
                stmt.close();
                stmt = null;
            } else {
                out.println("NO TIENE PERMISO PARA EXPORTAR DATOS");
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
    }

    /**
     * 
     * @param cant_registro_rpta
     * @param porc_lealtad
     * @param base
     * @return
     */
    public static String createDataTable(int cant_registro_rpta, String porc_lealtad, String base) {
        String html_preg = "";

        html_preg += "<p>N&deg; de Encuestas:&nbsp;" + cant_registro_rpta + "</p>";
        html_preg += "<table class='table table-condensed table-striped table-hover borderless2'>";
        html_preg += "<thead>";
        html_preg += "<tr>";
        html_preg += "<th class='text-left datatable-header'></th>";
        html_preg += "<th class='text-center datatable-header'><i class='fa fa-thumbs-o-up fa-2x good'></i></th>";
        html_preg += "<th class='text-center datatable-header'><i class='fa fa-thumbs-o-down fa-2x bad'></i></th>";
        html_preg += "<th class='text-center datatable-header'><span style='font-size: 14px;' title='' data-original-title='&Iacute;ndice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></th>";
        html_preg += "</tr>";
        html_preg += "</thead>";
        html_preg += "<tbody>";
        html_preg += base;
        html_preg += "</tbody>";
        html_preg += "</table>";
        html_preg += "<hr>";
        html_preg += "<p class='text-left'><strong>Lealtad&nbsp;<img src='img/corazon.gif' alt='lealtad' class='heart'>&nbsp;" + porc_lealtad + "</strong></p>";

        return html_preg;
    }

    /**
     * 
     * @param rs
     * @return
     */
    public static String validateRowsRS(ResultSet rs) {
        String message = "";
        int count = 0;

        try {
            count = getRows(rs);

            if (count == 0) {
                message = "<span title='no-data'>Aún no hay encuestas</span>";
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return message;
    }

    /**
     * 
     * @param message
     * @return
     */
    public static String bs_success(String message) {
        if (message != null && message.length() > 0) {
            return "<div id='alerta_panel' class='alert alert-success alert-dismissible' role='alert'>"
                            + "\t<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>\n"
                            + "\t<strong>OK!</strong>\n"
                            + "\t" + message + "\n"
                            + "</div>\n"
                            + "<script>\n"
                            + "\t$('#alerta_panel').delay(5000).fadeOut();\n"
                            + "</script>\n";
        }

        return null;
    }

    /**
     * 
     * @param message
     * @return
     */
    public static String bs_info(String message) {
        if (message != null && message.length() > 0) {
            return "<div id='alerta_panel' class='alert alert-info alert-dismissible' role='alert'>"
                            + "\t<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>\n"
                            + "\t<strong>Info: </strong>\n"
                            + "\t" + message + "\n"
                            + "</div>\n"
                            + "<script>\n"
                            + "\t$('#alerta_panel').delay(5000).fadeOut();\n"
                            + "</script>\n";
        }

        return null;
    }

    /**
     * 
     * @param message
     * @return
     */
    public static String bs_alert(String message) {
        if (message != null && message.length() > 0) {
            return "<div id='alerta_panel' class='alert alert-warning alert-dismissible' role='alert'>"
                            + "\t<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>\n"
                            + "\t<strong>Atenci&oacute;n!</strong> \n"
                            + "\t" + message + "\n"
                            + "</div>\n"
                            + "<script>\n"
                            + "\t$('#alerta_panel').delay(5000).fadeOut();\n"
                            + "</script>\n";
        }

        return null;
    }

    /**
     * 
     * @param message
     * @return
     */
    public static String bs_errors(String message) {
        if (message != null && message.length() > 0) {
            return "<div id='alerta_panel' class='alert alert-danger alert-dismissible' role='alert'>"
                            + "\t<button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>\n"
                            + "\t<strong>Error!</strong>\n "
                            + "\t" + message + "\n"
                            + "</div>\n"
                            + "<script>\n"
                            + "\t$('#alerta_panel').delay(5000).fadeOut();\n"
                            + "</script>\n";
        }

        return null;
    }

}