package cl.intelidata.servlet;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.EntityDecoder;
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

public class ChartServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ChartServlet.class);
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
    public ChartServlet() {
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
     * DO POST
     * 
     * @param request
     * @param response
     * 
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chart = request.getParameter("chart");
        HttpSession session = request.getSession();
        
        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
        ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());
        
        try {
            if (chart.toLowerCase().equalsIgnoreCase("00_simplesearch")) {
                // Thread.sleep(10000);
                doChart_00_simplesearch(request, response);
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * DO GET
     * 
     * @param request
     * @param response
     * 
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String chart = request.getParameter("chart");
        HttpSession session = request.getSession();
     
        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
        ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());
        
        try {
            if (chart.toLowerCase().equalsIgnoreCase("00_muestra")) {
                doChart_00_muestra(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("00_comentarios")) {
                doChart_00_comentarios(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("00_simplesearch")) {
                doChart_00_simplesearch(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("01_acumulado")) {
                doChart_01_acumulado(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("01_acumulado_tendencia")) {
                // doChart_01_acumulado_tendencia(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("02_periodo_indicadores")) {
                doChart_02_periodo_indicadores(request, response);
            } else if (chart.toLowerCase().equalsIgnoreCase("03_benchmark")) {
                doChart_03_benchmark(request, response);
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @throws Exception
     */
    private void doChart_00_simplesearch(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // PRINTER
        PrintWriter out = response.getWriter();

        // HEADERS
        response.setDateHeader("Expires", 0); // date in the past
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache"); // HTTP/1.0

        Locale locale = Locale.getDefault();
        response.setLocale(locale);

        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(30 * 60);

        // out.println(request.getParameter("wordSearch"));
        // out.println(request.getParameter("fromDate"));
        // out.println(request.getParameter("toDate"));

        // INSTANCE BBDD
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        // PARAMETERS
        String wordfind = request.getParameter("wordSearch");
        String fromdate = request.getParameter("fromDate");
        String todate = request.getParameter("toDate");

        String filtros = "";

        String strSQL_case01 = "";
        String strSQL_case011 = "";
        String strSQL_case02 = "";
        String strSQL_case022 = "";

        if (fromdate != "" && todate != "") {
            if (fromdate.equals(todate)) {
                strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + fromdate + "' THEN 'ACTUAL' ";
                strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + todate + "' ";
            } else {
                strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + fromdate + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + todate
                                + "' THEN 'ACTUAL' ";
                strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + fromdate + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + todate + "' ";
            }
        } else if (fromdate == "" && todate != "") {
            strSQL_case01 = " CASE WHEN DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + todate + "' THEN 'ACTUAL' ";
            strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + todate + "' ";
        }

        String strSQL_Cab = strSQL_case01;
        if (strSQL_case02 != "") {
            strSQL_Cab = strSQL_Cab + " " + strSQL_case02 + " ";
        }

        strSQL_Cab = strSQL_Cab + " END AS CS_Tipo_Fecha, ";

        filtros = filtros + " ( " + strSQL_case011;

        if (strSQL_case02 != "") {
            filtros = filtros + " or " + strSQL_case022;
        }

        filtros = filtros + ") AND ";

        // SQL QUERY
        String str_BusquedaSimple = "SELECT \n" + "\t" + strSQL_Cab + " \n" + "\tusuario.*, \n" + "\tcanal.descripcion_canal AS Canal_Ingreso, \n"
                        + "\tDATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') AS Periodo, \n" + "\tcliente_respuesta.ultima_respuesta AS Fecha_Encuesta, \n"
                        + "\testado.descripcion_estado AS Estado_Encuesta, \n" + "\tcliente_respuesta.id_cliente_respuesta, \n" + "\tpregunta_cabecera.numero_pregunta, \n"
                        + "\trespuesta.id_pregunta_cabecera, \n" + "\tcategoria.descripcion_categoria AS categoria, \n" + "\tpregunta_cabecera.descripcion_1, \n" + "\trespuesta_detalle.valor1, \n"
                        + "\tCASE WHEN ISNULL(respuesta_detalle.valor2) THEN '---' ELSE respuesta_detalle.valor2 END AS valor2 \n" + "FROM respuesta_detalle \n"
                        + "INNER JOIN  respuesta ON respuesta.id_respuesta = respuesta_detalle.id_respuesta \n"
                        + "INNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta \n" + "LEFT JOIN usuario ON respuesta.id_usuario = usuario.id_usuario \n"
                        + "INNER JOIN canal ON respuesta.id_canal = canal.id_canal \n" + "INNER JOIN estado ON cliente_respuesta.id_estado = estado.id_estado \n"
                        + "INNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera \n"
                        + "INNER JOIN categoria ON pregunta_cabecera.id_categoria = categoria.id_categoria \n" + "WHERE " + filtros + "\n" + "respuesta.id_cliente = " + ID_CLIENTE + " \n"
                        + "AND respuesta_detalle.valor2 LIKE '%" + wordfind + "%' \n"
                        // + filterMoment + " \n"
                        + "ORDER BY cliente_respuesta.id_cliente_respuesta, respuesta.id_pregunta_cabecera";

        Statement stmt = null;
        ResultSet rs = null;
        String html = "";

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = null;
            rs = stmt.executeQuery(str_BusquedaSimple);
            int count = 0;

            if (rs.last()) {
                // int cantRows = rs.getRow();
                rs.beforeFirst();
                String idtemp = "";

                while (rs.next()) {
                    String id = rs.getString("id_usuario");
                    String nombre = rs.getString("nombre_usuario");
                    String rut = rs.getString("rut_usuario");
                    String sexo = rs.getString("genero_usuario");
                    String edad = rs.getString("edad_usuario");
                    String icon = "";
                    String categoria = rs.getString("categoria");
                    String pregunta = rs.getString("numero_pregunta");
                    String comentario = rs.getString("valor2");

                    String once = rs.getString(11);

                    if (id == null) {
                        id = "";
                    }
                    if (nombre == null) {
                        nombre = "";
                    }
                    if (edad == null) {
                        edad = "";
                    }
                    if (rut == null) {
                        rut = "";
                    }

                    if (sexo == null) {
                        sexo = "";
                    }
                    if (sexo.equalsIgnoreCase("M")) {
                        sexo = "Masculino";
                        icon = "\t\t\t\t<span class=\"fa-stack fa-2x\">\n" + "\t\t\t\t\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n"
                                        + "\t\t\t\t\t<i class=\"fa fa-male fa-stack-1x fa-inverse\"></i>\n" + "\t\t\t\t</span>\n";
                    } else if (sexo.equalsIgnoreCase("F")) {
                        sexo = "Femenino";
                        icon = "\t\t\t\t<span class=\"fa-stack fa-2x\">\n" + "\t\t\t\t\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n"
                                        + "\t\t\t\t\t<i class=\"fa fa-female fa-stack-1x fa-inverse\"></i>\n" + "\t\t\t\t</span>";
                    } else {
                        icon = "\t\t\t\t<span class=\"fa-stack fa-2x\">\n" + "\t\t\t\t\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n"
                                        + "\t\t\t\t\t<i class=\"fa fa-question fa-stack-1x fa-inverse\"></i>\n" + "\t\t\t\t</span>";
                    }
                    if (categoria == null) {
                        categoria = "";
                    }
                    if (comentario == null) {
                        comentario = "";
                    }
                    if (pregunta == null) {
                        pregunta = "";
                    }
                    if (once == null) {
                        once = "";
                    }

                    if (count > 0 && !idtemp.equalsIgnoreCase(id)) {
                        html += "\t\t</ul>\n";
                        html += "\t</div>\n";
                        html += "</div>\n";
                        html += "<hr>\n";
                        count = 0;
                    }

                    if (count == 0 && !idtemp.equalsIgnoreCase(id)) {
                        html += "<div class=\"row\" style=\"margin-bottom: 5px;\">\n";
                        html += "\t<div class=\"col-md-3\">\n";
                        html += "\t\t<ul class=\"list-unstyled\">\n";
                        html += "\t\t\t<li>\n" + icon + "</li>\n";
                        html += "\t\t\t<li><strong>Nombre:&nbsp;</strong>" + nombre + "</li>\n";
                        html += "\t\t\t<li><strong>Edad:&nbsp;</strong>" + edad + "</li>\n";
                        html += "\t\t\t<li><strong>RUT:&nbsp;</strong>" + rut + "</li>\n";
                        html += "\t\t\t<li><strong>Sexo:&nbsp;</strong>" + sexo + "</li>\n";
                        html += "\t\t\t<li><strong>Fecha:&nbsp;</strong>" + jspmkrfn.EW_FormatDateTime(rs.getTimestamp("Fecha_Encuesta"), 8, locale) + "</li>\n";
                        html += "\t\t</ul>\n";
                        html += "\t</div>\n";
                        html += "\t<div class=\"col-md-9\">\n";
                        html += "\t\t<ul class=\"list-unstyled\">\n ";
                        idtemp = id;
                        count++;
                    }

                    html += "\t\t\t<li>\n" + "<strong>" + EntityDecoder.entityToHtml(categoria) + "</strong>\n" + "<br>\n" + EntityDecoder.entityToHtml(comentario) + "\n</li>\n";
                }

                if(!html.endsWith("<hr>\n")){
                    html += "\t\t</ul>\n";
                    html += "\t</div>\n";
                    html += "</div>\n";
                    count = 0;
                }

                out.print(html);
            } else {
                String msg = ApiServlet.bs_alert("No existen comentarios");
                out.print(msg);
            }
        } catch (SQLException ex) {
            logger.error(ex.toString());
        } finally {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }

                if (!stmt.isClosed()) {
                    stmt.close();
                }
                dbtools.disconnectDB();
            } catch (SQLException sqle) {
                System.out.println(sqle.toString());
            }
        }

        out.close();
    }

    /**
     * FIRST ROW - THIRD BOX DOCHART_01_ACUMULADOS ---------------------- FALTA
     * REVISAR EL TEMA QUE DATOS VA A MOSTRAR YA QUE SOLO SE TIENE UN SOLO
     * CLIENTE
     *
     * @param request
     * @param response
     */
    public void doChart_01_acumulado(HttpServletRequest request, HttpServletResponse response) {
        ResultSet rs = null;
        Statement stmt = null;

        // INSTANCE BBDD
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        // PRINTER
        PrintWriter out = null;
        try {

            out = response.getWriter();

            // HEADERS
            response.setContentType("text/html;charset=UTF-8");
            response.setDateHeader("Expires", 0); // date in the past
            response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.addHeader("Pragma", "no-cache"); // HTTP/1.0

            // LOCALE
            Locale locale = Locale.getDefault();
            response.setLocale(locale);

            // SESSION
            javax.servlet.http.HttpSession session = request.getSession();
            session.setMaxInactiveInterval(30 * 60);

            // BEGIN VALIDATE USER LOGIN
            if (ApiServlet.ValidateUserLogin(request, response, session)) {
                return;
            }
            // END VALIDATE USER LOGIN

            // SQL QUERY'S ------------------ ACUMULADO
            String strSQL_Cualificacion = "SELECT  \n" + "'Encuesta' AS CS_Tipo,\n" + "CONVERT(count(*)/ 4,UNSIGNED INTEGER) As CS_Casos,\n" + "'' AS Ultima\n" + "FROM cliente_respuesta \n"
                            + "INNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n" + "WHERE\n" + "\t\tcliente.id_cliente = " + ID_CLIENTE + " \n"
                            + "\t\tAND cliente.id_sector = " + ID_SECTOR + " \n";

            String strSQL_NPS = "SELECT \n" + " nps.promedio \n" + "FROM nps \n" + "WHERE nps.id_cliente = " + ID_CLIENTE + " \n";

            String strSQL_Lealtad = "SELECT \n" + "\tSUM(CASE WHEN valor2 = 'NO' THEN 1 ELSE 0 END) AS Leal_NO, \n" + "\tSUM(CASE WHEN valor2 = 'SI' THEN 1 END) AS Leal_SI \n" + "FROM ( \n"
                            + "SELECT respuesta.id_cliente, respuesta_detalle.valor2 \n" + "FROM cliente \n" + "INNER JOIN cliente_respuesta ON cliente.id_cliente = cliente_respuesta.id_cliente \n"
                            + "INNER JOIN respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta \n"
                            + "INNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta \n" + "INNER JOIN pregunta_cabecera \n"
                            + "ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera \n" + "\tINNER JOIN ( \n" + "\t\tSELECT cliente_respuesta.id_cliente, \n"
                            + "\t\tMAX(cliente_respuesta.id_cliente_respuesta) AS id_ultima_rpta \n" + "\t\tFROM cliente_respuesta \n"
                            + "\t\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente \n" + "\t\tGROUP BY id_cliente \n" + "\t) AS Ultima_rpta_x_usuario \n"
                            + "\tON cliente_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente\n" + "WHERE (pregunta_cabecera.numero_pregunta = 4)" + "AND cliente.id_cliente = " + ID_CLIENTE
                            + " \n" + ") AS Datos ";

            String strSQL_Preguntas = "SELECT \n" + "\tnumero_pregunta, \n" + "\tSUM(NPS_7) AS NPS_7, \n" + "\tSUM(NPS_5) AS NPS_5, \n" + "\tSUM(NPS_4) AS NPS_4 \n" + "FROM (\n" + "\tSELECT \n"
                            + "\t\tpregunta_cabecera.numero_pregunta, \n" + "\t\t\tCASE WHEN respuesta_detalle.valor1 >= 6 THEN 1 ELSE 0 end AS NPS_7, \n"
                            + "\t\t\tCASE WHEN respuesta_detalle.valor1 < 6 AND respuesta_detalle.valor1 > 4 THEN 1 ELSE 0 end AS NPS_5, \n"
                            + "\t\t\tCASE WHEN respuesta_detalle.valor1 <= 4 then 1 ELSE 0 end AS NPS_4 \n" + "\tFROM respuesta \n"
                            + "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta \n"
                            + "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta \n"
                            + "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente \n"
                            + "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n" + "\tINNER JOIN (\n"
                            + "\t\tSELECT cliente_respuesta.id_cliente,\n" + "\t\tMAX(cliente_respuesta.id_cliente_respuesta) AS id_ultima_rpta \n" + "\t\tFROM cliente_respuesta \n"
                            + "\t\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente \n" + "\t\tGROUP BY id_cliente \n" + "\t) AS Ultima_rpta_x_usuario \n"
                            + "\tON cliente_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente\n"
                            + "\tWHERE (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n" + "\tAND cliente.id_cliente = "
                            + ID_CLIENTE + " \n" + "\tGROUP BY respuesta.id_respuesta \n" + ") AS Datos_Tmp \n" + "GROUP BY numero_pregunta \n" + "ORDER BY numero_pregunta";

            // Double cant_registro_total = 0.0;
            // Double cant_registro_rpta = 0.0;

            Double nps_7 = 0.0;
            Double nps_5 = 0.0;
            Double nps_4 = 0.0;
            Double porc_7 = 0.0;
            Double porc_4 = 0.0;

            // Double cant_promotores = 0.0;
            // Double cant_detractores = 0.0;

            Double cant_leal = 0.0;
            Double cant_leal_no = 0.0;

            // String porc_cualificacion = "";
            String porc_nps = "";
            String porc_promotores = "";
            String porc_detractores = "";

            String porc_lealtad = "";

            String html_preg_etiqueta = "";
            String html_preg = "";

            // BEGIN TRY
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            int cant_total_rpta = ApiServlet.getNumberOfQuestions(strSQL_Cualificacion);

            if (cant_total_rpta > 0) {

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
                    total++;
                }

                nps_7 = (double) ((float) (promotor * 100) / total);
                nps_4 = (double) ((float) (detractor * 100) / total);
                nps_5 = (double) ((float) (nps_7 - nps_4));

                porc_promotores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_7), 0, 0, 0, 0, locale) + "%";
                porc_nps = jspmkrfn.EW_FormatNumber(String.valueOf(nps_5), 0, 0, 0, 0, locale) + "%";
                porc_detractores = jspmkrfn.EW_FormatNumber(String.valueOf(nps_4), 0, 0, 0, 0, locale) + "%";

                rs.close();

                rs = stmt.executeQuery(strSQL_Lealtad);
                if (rs.last()) {
                    rs.beforeFirst();
                    rs.next();
                    cant_leal = rs.getDouble("Leal_SI");
                    cant_leal_no = rs.getDouble("Leal_NO");
                    porc_lealtad = jspmkrfn.EW_FormatNumber(String.valueOf((cant_leal / (cant_leal + cant_leal_no)) * 100), 0, 0, 0, 0, locale) + "%";
                }
                rs.close();

                rs = stmt.executeQuery(strSQL_Preguntas);
                if (rs.last()) {
                    rs.beforeFirst();
                    while (rs.next()) {
                        nps_7 = rs.getDouble("NPS_7");
                        nps_5 = rs.getDouble("NPS_5");
                        nps_4 = rs.getDouble("NPS_4");
                        porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
                        porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);

                        if (rs.getInt("numero_pregunta") == 1) {
                            html_preg_etiqueta = "Efectivo";
                        } else if (rs.getInt("numero_pregunta") == 2) {
                            html_preg_etiqueta = "F&aacute;cil";
                        } else if (rs.getInt("numero_pregunta") == 3) {
                            html_preg_etiqueta = "Agradable";
                        }

                        html_preg = html_preg + "<tr>";
                        html_preg = html_preg + "<td class='text-left'>&nbsp;" + html_preg_etiqueta + "</td>";
                        html_preg = html_preg + "<td class='text-center'>&nbsp;" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7), 0, 0, 0, 0, locale) + "%</td>";
                        html_preg = html_preg + "<td class='text-center'>&nbsp;" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_4), 0, 0, 0, 0, locale) + "%</td>";
                        html_preg = html_preg + "<td class='text-center'>&nbsp;" + jspmkrfn.EW_FormatNumber(String.valueOf(porc_7 - porc_4), 0, 0, 0, 0, locale) + "%</td>";
                        html_preg = html_preg + "</tr>";
                    }
                }
                rs.close();
            } else {
                porc_nps = "-";
                porc_promotores = "-";
                porc_detractores = "-";
                porc_lealtad = "-";
            }

            out.println("<table class='table table-condensed table-striped table-hover borderless2'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th colspan='3'>N&deg; de Encuestas:&nbsp" + cant_total_rpta + "</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            out.println("<tr>");
            out.println("<td><i class='fa fa-thumbs-o-up fa-2x good'></td>");
            out.println("<td>&nbsp;Promotores:</td>");
            out.println("<td>&nbsp;" + porc_promotores + "</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td><i class='fa fa-thumbs-o-down fa-2x bad'></td>");
            out.println("<td>&nbsp;Detractores:</td>");
            out.println("<td>&nbsp;" + porc_detractores + "</td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("</table>");

            out.println("<table class='table table-condensed table-striped table-hover borderless2'>");
            out.println("<tbody>");
            out.println("<tr>");
            out.println("<td>&nbsp;<span title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span>:</td>");
            out.println("<td>&nbsp;" + porc_nps + "</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>&nbsp;Recomendaci&oacute;n:</td>");
            out.println("<td>&nbsp;" + porc_lealtad + "&nbsp;<img src='img/corazon.gif' alt='lealtad' class='heart'></td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("</table>");

            out.println("<table class='table table-condensed table-striped table-hover borderless2'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th class='text-left datatable-header'></th>");
            out.println("<th class='text-center datatable-header'><i class='fa fa-thumbs-o-up fa-2x good'></th>");
            out.println("<th class='text-center datatable-header'><i class='fa fa-thumbs-o-down fa-2x bad'></th>");
            out.println("<th class='text-center datatable-header'><span style='font-size: 14px' title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            out.println(html_preg);
            out.println("</tbody>");
            out.println("</table>");

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
            out.close();
        }
    }

    /**
     * DOCHART_02_PERIODO_INDICADORES ----------------------------- OK
     *
     * @param request
     * @param response
     */
    public void doChart_02_periodo_indicadores(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            // PRINTER
            out = response.getWriter();

            // HEADERS
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

            // BEGIN VALIDATE USER LOGIN
            if (ApiServlet.ValidateUserLogin(request, response, session)) {
                return;
            }
            // END VALIDATE USER LOGIN

            // BEGIN SECURITY ------------------
            ApiServlet.ValidateUserSecurity(session);
            // END SECURITY ------------------

            // PERIODOS
            String periodo_0 = request.getParameter("periodo_0");
            String periodo_1 = request.getParameter("periodo_1");
            String periodo_2 = request.getParameter("periodo_2");
            String periodo_3 = request.getParameter("periodo_3");
            String mostrar_como = request.getParameter("mostrar_como");

            // FILTROS
            String strSQL_Cab = ApiServlet.getPeriodsQuery(periodo_0, periodo_1, periodo_2, periodo_3);
            String filtros = ApiServlet.getFiltersQuery(periodo_0, periodo_1, periodo_2, periodo_3);
            String strSQL_CabNPS = ApiServlet.getPeriodsQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);
            String filtrosNPS = ApiServlet.getFiltersQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);

            // SQL QUERY'S ------------------ PERIODO
            String strSQL_Cualificacion = "SELECT  \n" + "'Encuesta' AS CS_Tipo,\n" + "CONVERT(count(*)/ 4,UNSIGNED INTEGER) As CS_Casos,\n" + "'' AS Ultima\n" + "FROM cliente_respuesta \n"
                            + "INNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n" + "WHERE\n" + "\t\t" + filtros + " \n" + "\t\tAND cliente.id_cliente = " + ID_CLIENTE + " \n"
                            + "\t\tAND cliente.id_sector = " + ID_SECTOR + " \n";

            String strSQL_NPS = "SELECT \n" + strSQL_CabNPS + " \n" + " nps.promedio \n" + "FROM nps \n" + "WHERE nps.id_cliente = " + ID_CLIENTE + " \n" + "AND " + filtrosNPS;

            String strSQL_Lealtad = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN valor2 = 'NO' THEN 1 ELSE 0 END) AS Leal_NO,\n" + "\tSUM(CASE WHEN valor2 = 'SI' THEN 1 END) AS Leal_SI\n"
                            + "FROM (\n" + "\tSELECT \n" + "\t\t" + strSQL_Cab + "\n" + "\t\t'' CS_Rpta_Periodo,\n" + "\t\trespuesta.id_cliente,\n" + "\t\trespuesta_detalle.valor2\n"
                            + "\tFROM respuesta\n" + "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                            + "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
                            + "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
                            + "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n" + "\tWHERE cliente.id_cliente = " + ID_CLIENTE + "\n"
                            + "\tAND respuesta.id_encuesta = cliente.id_encuesta\n" + "\tAND (pregunta_cabecera.numero_pregunta = 4)" + "\tAND (" + filtros + ") \n" + ") AS Datos\n"
                            + "GROUP BY CS_Tipo_Fecha\n" + "ORDER BY CS_Tipo_Fecha\n";

            String strSQL_Preguntas = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tnumero_pregunta,\n" + "\tSUM(NPS_7) AS NPS_7,\n" + "\tSUM(NPS_5) AS NPS_5,\n" + "\tSUM(NPS_4) AS NPS_4\n" + "FROM ( \n"
                            + "\tSELECT \n" + "\t\t"
                            + strSQL_Cab
                            + "\n"
                            + "\t\tDATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') AS CS_Rpta_Periodo,\n"
                            + "\t\tpregunta_cabecera.numero_pregunta AS numero_pregunta,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 >= 6 THEN 1 ELSE 0 END AS NPS_7,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 < 6 AND respuesta_detalle.valor1 > 4 THEN 1 ELSE 0 END AS NPS_5,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 <= 4 THEN 1 ELSE 0 END AS NPS_4\n"
                            + "\tFROM respuesta\n"
                            + "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                            + "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
                            + "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
                            + "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
                            + "\tWHERE cliente.id_cliente = "
                            + ID_CLIENTE
                            + "\n"
                            + "\tAND respuesta.id_encuesta = cliente.id_encuesta\n"
                            + "\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
                            + "\tAND ("
                            + filtros
                            + ") \n"
                            + "\tGROUP BY respuesta.id_respuesta\n" + ") AS Datos_Tmp\n" + "GROUP BY CS_Tipo_Fecha , numero_pregunta\n" + "ORDER BY CS_Tipo_Fecha , numero_pregunta;";

            int cant_registro_rpta = 0;
            String porc_lealtad = "";
            String html_preg = "";
            String strXML = "";
            String base = "";

            ENCONTRO_ANTERIOR = 0;
            cant_registro_rpta = ApiServlet.getNumberOfQuestions(strSQL_Cualificacion);

            // SI ENCUENTRA REGISTROS EN LA CONSULTA DE CUALIFICACION
            if (cant_registro_rpta > 0) {

                // BEGIN GENERATE NPS
                String outputNPS = ApiServlet.genNPS(strSQL_NPS, mostrar_como, locale);
                // END GENERATE NPS

                if (mostrar_como.equals("tabla")) {
                    // BEGIN PREGUNTAS
                    int Largo_Array = 4;
                    String[][] arrayPreguntas = new String[Largo_Array][7]; // null
                    arrayPreguntas = ApiServlet.generateQuestionsQuery(strSQL_Preguntas, Largo_Array);
                    ENCONTRO_ANTERIOR = ApiServlet.foundOld(strSQL_Preguntas, Largo_Array);
                    base += ApiServlet.genOne(Largo_Array, arrayPreguntas, ENCONTRO_ANTERIOR, locale);
                    // END PREGUNTAS

                    // BEGIN NPS
                    base += outputNPS;
                    // END NPS

                    // BEGIN GENERATE LEALTAD
                    porc_lealtad = ApiServlet.getPercentageOfLoyalty(strSQL_Lealtad, locale);
                    // END GENERATE LEALTAD
                } else {
                    strXML += outputNPS;
                }
            } else {
                response.setContentType("text/html;charset=UTF-8");
                html_preg = "<span>No existen encuestas a&uacute;n.</span>";
                out.println(html_preg);
                out.close();
            }

            if (mostrar_como.equals("tabla")) {
                html_preg = ApiServlet.createDataTable(cant_registro_rpta, porc_lealtad, base);
                response.setContentType("text/html;charset=UTF-8");
                out.println(html_preg);
            }

            if (mostrar_como.equals("grafico")) {
                response.setContentType("text/xml");
                out.println(strXML);
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
        }

        out.close();
    }

    /**
     * COMPETENCIA DATOS DOCHART_03_BENCHMARK
     *
     * @param request
     * @param response
     *
     * @throws java.io.IOException
     */
    public void doChart_03_benchmark(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        PrintWriter out = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            // PRINTER
            out = response.getWriter();

            // HEADERS
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

            // BEGIN VALIDATE USER LOGIN
            if (ApiServlet.ValidateUserLogin(request, response, session)) {
                return;
            }
            // END VALIDATE USER LOGIN

            // BEGIN SECURITY ------------------
            ApiServlet.ValidateUserSecurity(session);
            // END SECURITY ------------------

            // PERIODOS
            String periodo_0 = request.getParameter("periodo_0");
            String periodo_1 = request.getParameter("periodo_1");
            String periodo_2 = request.getParameter("periodo_2");
            String periodo_3 = request.getParameter("periodo_3");
            String mostrar_como = request.getParameter("mostrar_como");

            // FILTROS
            String strSQL_Cab = ApiServlet.getPeriodsQuery(periodo_0, periodo_1, periodo_2, periodo_3);
            String filtros = ApiServlet.getFiltersQuery(periodo_0, periodo_1, periodo_2, periodo_3);
            String strSQL_CabNPS = ApiServlet.getPeriodsQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);
            String filtrosNPS = ApiServlet.getFiltersQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);

            // SQL QUERY'S ------------------ COMPETENCIA
            String strSQL_Cualificacion = "SELECT  \n" + "'Encuesta' AS CS_Tipo,\n" + "CONVERT(count(*)/ 4,UNSIGNED INTEGER) As CS_Casos,\n" + "'' AS Ultima\n" + "FROM cliente_respuesta \n"
                            + "INNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n" + "WHERE\n" + "\t\t" + filtros + " \n" + "\t\tAND cliente.id_cliente != " + ID_CLIENTE
                            + " \n" + "\t\tAND cliente.id_sector = " + ID_SECTOR + " \n";

            String strSQL_NPS = "SELECT \n" + strSQL_CabNPS + " \n" + " nps.promedio \n" + "FROM nps \n" + "INNER JOIN cliente ON nps.id_cliente = cliente.id_cliente \n" + "WHERE nps.id_cliente != "
                            + ID_CLIENTE + " \n" + "AND cliente.id_sector = " + ID_SECTOR + " \n" + "AND " + filtrosNPS;

            String strSQL_Lealtad = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN valor2 = 'NO' THEN 1 ELSE 0 END) AS Leal_NO,\n" + "\tSUM(CASE WHEN valor2 = 'SI' THEN 1 END) AS Leal_SI\n"
                            + "FROM (\n" + "\tSELECT \n" + "\t\t"
                            + strSQL_Cab
                            + "\n"
                            + "\t\t'' CS_Rpta_Periodo,\n"
                            + "\t\trespuesta.id_cliente,\n"
                            + "\t\trespuesta_detalle.valor2\n"
                            + "\tFROM respuesta\n"
                            + "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                            + "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
                            + "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
                            + "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
                            + "\tWHERE cliente.id_cliente != "
                            + ID_CLIENTE
                            + "\n"
                            + "\t\tAND cliente.id_sector = "
                            + ID_SECTOR
                            + " \n"
                            + "\tAND respuesta.id_encuesta = cliente.id_encuesta\n"
                            + "\tAND (pregunta_cabecera.numero_pregunta = 4)"
                            + "\tAND ("
                            + filtros + ") \n" + ") AS Datos\n" + "GROUP BY CS_Tipo_Fecha\n" + "ORDER BY CS_Tipo_Fecha\n";

            String strSQL_Preguntas = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tnumero_pregunta,\n" + "\tSUM(NPS_7) AS NPS_7,\n" + "\tSUM(NPS_5) AS NPS_5,\n" + "\tSUM(NPS_4) AS NPS_4\n" + "FROM ( \n"
                            + "\tSELECT \n" + "\t\t"
                            + strSQL_Cab
                            + "\n"
                            + "\t\tDATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') AS CS_Rpta_Periodo,\n"
                            + "\t\tpregunta_cabecera.numero_pregunta AS numero_pregunta,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 >= 6 THEN 1 ELSE 0 END AS NPS_7,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 < 6 AND respuesta_detalle.valor1 > 4 THEN 1 ELSE 0 END AS NPS_5,\n"
                            + "\t\tCASE WHEN respuesta_detalle.valor1 <= 4 THEN 1 ELSE 0 END AS NPS_4\n"
                            + "\tFROM respuesta\n"
                            + "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                            + "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
                            + "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
                            + "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
                            + "\tWHERE cliente.id_cliente != "
                            + ID_CLIENTE
                            + "\n"
                            + "\t\tAND cliente.id_sector = "
                            + ID_SECTOR
                            + " \n"
                            + "\tAND respuesta.id_encuesta = cliente.id_encuesta\n"
                            + "\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
                            + "\tAND ("
                            + filtros
                            + ") \n"
                            + "\tGROUP BY respuesta.id_respuesta\n" + ") AS Datos_Tmp\n" + "GROUP BY CS_Tipo_Fecha , numero_pregunta\n" + "ORDER BY CS_Tipo_Fecha , numero_pregunta;";

            int cant_registro_rpta = 0;
            String porc_lealtad = "";
            String html_preg = "";
            String strXML = "";
            String base = "";

            ENCONTRO_ANTERIOR = 0;
            cant_registro_rpta = ApiServlet.getNumberOfQuestions(strSQL_Cualificacion);

            // SI ENCUENTRA REGISTROS EN LA CONSULTA DE CUALIFICACION
            if (cant_registro_rpta > 0) {
                // BEGIN GENERATE NPS
                String outputNPS = ApiServlet.genNPS(strSQL_NPS, mostrar_como, locale);
                // END GENERATE NPS

                if (mostrar_como.equals("tabla")) {
                    // BEGIN PREGUNTAS
                    int Largo_Array = 4;
                    String[][] arrayPreguntas = new String[Largo_Array][7]; // null
                    arrayPreguntas = ApiServlet.generateQuestionsQuery(strSQL_Preguntas, Largo_Array);
                    ENCONTRO_ANTERIOR = ApiServlet.foundOld(strSQL_Preguntas, Largo_Array);
                    base += ApiServlet.genOne(Largo_Array, arrayPreguntas, ENCONTRO_ANTERIOR, locale);
                    // END PREGUNTAS

                    // BEGIN NPS
                    base += outputNPS;
                    // END NPS

                    // BEGIN GENERATE LEALTAD
                    porc_lealtad = ApiServlet.getPercentageOfLoyalty(strSQL_Lealtad, locale);
                    // END GENERATE LEALTAD
                } else {
                    strXML += outputNPS;
                }
            } else {
                response.setContentType("text/html;charset=UTF-8");
                html_preg = "<span>No existen encuestas a&uacute;n.</span>";
                out.println(html_preg);
                out.close();
            }

            if (mostrar_como.equals("tabla")) {
                html_preg = ApiServlet.createDataTable(cant_registro_rpta, porc_lealtad, base);
                response.setContentType("text/html;charset=UTF-8");
                out.println(html_preg);
            }

            if (mostrar_como.equals("grafico")) {
                response.setContentType("text/xml");
                out.println(strXML);
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
        }

        out.close();
    }

    /**
     * DOCHART_00_MUESTRA ----------------------------- OK
     *
     * @param request
     * @param response
     *
     * @throws ServletException
     * @throws IOException
     * @throws SQLException
     */
    public void doChart_00_muestra(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        // PRINTER
        PrintWriter out = response.getWriter();

        // HEADERS
        response.setContentType("text/html;charset=UTF-8");
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

        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        // INSTANCE BBDD
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        // SET VARS
        String periodo_0 = request.getParameter("periodo_0");
        if (periodo_0 == null) {
            periodo_0 = "";
        }
        String periodo_1 = request.getParameter("periodo_1");
        if (periodo_1 == null) {
            periodo_1 = "";
        }
        String periodo_2 = request.getParameter("periodo_2");
        if (periodo_2 == null) {
            periodo_2 = "";
        }
        String periodo_3 = request.getParameter("periodo_3");
        if (periodo_3 == null) {
            periodo_3 = "";
        }
        String tipoD = request.getParameter("tipoD");
        String tituloX = request.getParameter("Titulo");

        String strXML = "";
        Statement stmt = null;
        ResultSet rs = null;
        Object x_FECHA = null;

        String strSQL_Periodo = "";
        // String strSQL_Cab = ApiServlet.getPeriodsQuery(periodo_0, periodo_1,
        // periodo_2, periodo_3);
        String filtros = ApiServlet.getFiltersQuery(periodo_0, periodo_1, periodo_2, periodo_3);

        if (periodo_0 != "" && periodo_1 != "") {
            if (periodo_0.equals(periodo_1)) {
                strSQL_Periodo = " Periodo = '" + periodo_1 + "' ";
            } else {
                strSQL_Periodo = " Periodo >= '" + periodo_0 + "' AND Periodo <= '" + periodo_1 + "' ";
            }
        } else if (periodo_0 == "" && periodo_1 != "") {
            strSQL_Periodo = " Periodo = '" + periodo_1 + "' ";
        }

        if (periodo_2 != "" && periodo_3 != "") {
            if (periodo_2.equals(periodo_3)) {
                strSQL_Periodo = " Periodo = '" + periodo_3 + "' ";
            } else {
                strSQL_Periodo = " Periodo >= '" + periodo_2 + "' AND Periodo <= '" + periodo_3 + "' ";
            }
        }

        try {
            if (periodo_0 == "" && periodo_1 != "") {
                int año = Integer.parseInt(periodo_1.split("-")[0]);
                int mes = Integer.parseInt(periodo_1.split("-")[1]);
                mes = mes - 1;
                if (mes < 10) {
                    periodo_0 = año + "-" + "0" + mes;
                } else {
                    periodo_0 = año + "-" + mes;
                }
            }

            // CONSULTING DATABASE
            String strSQL = "SELECT " + "\t'Muestra' AS CS_Tipo," + "\tSUM(cs_periodos.meta) AS CS_Casos," + "\t'' AS Ultima " + "FROM cs_periodos " + "WHERE Periodo BETWEEN ('" + periodo_0
                            + "') AND ('" + periodo_1 + "') " + "AND id_cliente = " + ID_CLIENTE + "\n" + "AND " + strSQL_Periodo + "\n" + "UNION " + "SELECT " + "\t'Encuesta' AS CS_Tipo,"
                            + "\tCONVERT(COUNT(*)/4, UNSIGNED INTEGER) AS CS_Casos," + "\tMAX(ultima_respuesta) AS Ultima " + "FROM cliente_respuesta " + "WHERE id_cliente = " + ID_CLIENTE + "\n"
                            + "AND DATE_FORMAT(ultima_respuesta, '%Y-%m') BETWEEN '" + periodo_0 + "' AND '" + periodo_1 + "'\n" + "AND " + filtros + " \n";

            stmt = dbtools.getConexion().createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);

            rs = stmt.executeQuery(strSQL);
            if (rs.last()) {
                rs.beforeFirst();
                rs.next();
                Long objetivo = rs.getLong("CS_Casos");
                rs.next();
                Long llevamos = rs.getLong("CS_Casos");

                if (rs.getTimestamp("Ultima") != null) {
                    x_FECHA = rs.getTimestamp("Ultima");
                } else {
                    x_FECHA = "";
                }

                rs.next();

                Double avance = ((double) llevamos / (double) objetivo) * 100;
                String avance2 = jspmkrfn.EW_FormatNumber(String.valueOf(avance), 0, 0, 0, 0, locale);
                avance2 = avance2.replace(",", ".");

                // GENERATE CHART
                if (tipoD.equals("1")) {
                    strXML = getShowProgressChart(avance, avance2, objetivo);
                    response.setContentType("text/xml");
                } else {
                    strXML = "<div class='text-center'>" + "<span class='label3 label-info2'>Total Muestra " + tituloX + "</span><br>" + "<span class='label3 label-info2'>Objetivo " + objetivo
                                    + " encuestas</span><br>" + "<span class='label3 label-info2'>Llevamos " + llevamos + " realizadas</span><br>"
                                    + "<span class='label3 label-info2'>&Uacute;ltima actualizaci&oacute;n:<br>" + jspmkrfn.EW_FormatDateTime(x_FECHA, 8, locale) + "</span>" + "<div>";
                }
            } else {
                if (tipoD.equals("1")) {
                    strXML = getDefaultShowProgressChart();
                    response.setContentType("text/xml");
                } else {
                    strXML = "<div class='text-center'>" + "<span class='label3 label-info2'>Total Muestras2</span><br>" + "<span class='label3 label-info2'>Objetivo -- encuestas</span><br>"
                                    + "<span class='label3 label-info2'>Llevamos -- realizadas</span><br>" + "<span class='label3 label-info2'>&Uacute;ltima actualizaci&oacute;n:<br>--</span>"
                                    + "<div>";
                }
            }

            out.println(strXML);
        } catch (Exception ex) {
            logger.error(ex.toString());
        } finally {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                    rs = null;
                }
                if (!stmt.isClosed()) {
                    stmt.close();
                    stmt = null;
                }
                dbtools.disconnectDB();
            } catch (SQLException sqle) {
                out.println(sqle.toString());
            }

            out.close();
        }
    }

    /**
     * MODAL COMMENT DOCHART_00_COMENTARIOS
     *
     * @param request
     * @param response
     *
     * @throws ServletException
     * @throws IOException
     * @throws SQLException
     */
    public void doChart_00_comentarios(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {

        // PRINTER
        PrintWriter out = response.getWriter();

        // HEADERS
        response.setDateHeader("Expires", 0); // date in the past
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache"); // HTTP/1.0

        Locale locale = Locale.getDefault();
        response.setLocale(locale);

        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(30 * 60);

        // INSTANCE BBDD
        DatabaseTools dbtools = new DatabaseTools();
        dbtools.connectDB();

        // BEGIN VALIDATE USER LOGIN
        if (ApiServlet.ValidateUserLogin(request, response, session)) {
            return;
        }
        // END VALIDATE USER LOGIN

        // String currTime = request.getParameter("currTime");
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
                strSQL_case011 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' ";
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
                strSQL_case022 = " DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' ";
            }
        }

        String strSQL_Cab = strSQL_case01;
        if (strSQL_case02 != "") {
            strSQL_Cab = strSQL_Cab + " " + strSQL_case02 + " ";
        }

        strSQL_Cab = strSQL_Cab + " END AS CS_Tipo_Fecha, ";

        filtros = filtros + " ( " + strSQL_case011;

        if (strSQL_case02 != "") {
            filtros = filtros + " or " + strSQL_case022;
        }

        filtros = filtros + ") AND ";

        String str_Comentarios = "SELECT \n" + "\t" + strSQL_Cab + " \n" + "\tusuario.*,\n" + "\tcanal.descripcion_canal AS Canal_Ingreso,\n"
                        + "\tDATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') AS Periodo,\n" + "\tcliente_respuesta.ultima_respuesta AS Fecha_Encuesta,\n"
                        + "\testado.descripcion_estado AS Estado_Encuesta,\n" + "\tcliente_respuesta.id_cliente_respuesta,\n" + "\tpregunta_cabecera.numero_pregunta,\n"
                        + "\trespuesta.id_pregunta_cabecera,\n" + "\tcategoria.descripcion_categoria AS categoria,\n" + "\tpregunta_cabecera.descripcion_1,\n" + "\trespuesta_detalle.valor1,\n"
                        + "\tCASE WHEN ISNULL(respuesta_detalle.valor2) THEN '---' ELSE respuesta_detalle.valor2 END AS valor2\n" + "FROM respuesta_detalle\n"
                        + "INNER JOIN  respuesta ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
                        + "INNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n" + "LEFT JOIN usuario ON respuesta.id_usuario = usuario.id_usuario\n"
                        + "INNER JOIN canal ON respuesta.id_canal = canal.id_canal\n" + "INNER JOIN estado ON cliente_respuesta.id_estado = estado.id_estado\n"
                        + "INNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
                        + "INNER JOIN categoria ON pregunta_cabecera.id_categoria = categoria.id_categoria\n" + "WHERE " + filtros + "\n" + "respuesta.id_cliente = " + ID_CLIENTE + "\n"
                        + filterMoment + " \n" + "ORDER BY cliente_respuesta.id_cliente_respuesta, respuesta.id_pregunta_cabecera";

        Statement stmt = null;
        ResultSet rs = null;
        String html = "";

        try {
            stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = null;
            rs = stmt.executeQuery(str_Comentarios);
            // int rowcount = 0;

            if (rs.last()) {
                // out.println("<table class='table table-bordered table-striped table-condensed' width='100%' border='0'>");
                // rowcount = rs.getRow();

                // String ultimo_id = "";

                rs.beforeFirst();

                while (rs.next()) {
                    String nombre = rs.getString("nombre_usuario");
                    if (nombre == null) {
                        nombre = "";
                    }
                    String edad = rs.getString("edad_usuario");
                    if (edad == null) {
                        edad = "";
                    }
                    String rut = rs.getString("rut_usuario");
                    if (rut == null) {
                        rut = "";
                    }
                    String sexo = rs.getString("genero_usuario");
                    if (sexo == null) {
                        sexo = "";
                    }
                    String icon = "";
                    if (sexo.equalsIgnoreCase("M")) {
                        sexo = "Masculino";
                        icon = "<span class=\"fa-stack fa-2x\">\n" + "\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n" + "\t<i class=\"fa fa-male fa-stack-1x fa-inverse\"></i>\n" + "</span>";
                    } else if (sexo.equalsIgnoreCase("F")) {
                        sexo = "Femenino";
                        icon = "<span class=\"fa-stack fa-2x\">\n" + "\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n" + "\t<i class=\"fa fa-female fa-stack-1x fa-inverse\"></i>\n" + "</span>";
                    } else {
                        icon = "<span class=\"fa-stack fa-2x\">\n" + "\t<i class=\"fa fa-circle fa-stack-2x\"></i>\n" + "\t<i class=\"fa fa-question fa-stack-1x fa-inverse\"></i>\n" + "</span>";
                    }
                    String categoria = rs.getString("categoria");
                    if (categoria == null) {
                        categoria = "";
                    }
                    String comentario = rs.getString("valor2");
                    if (comentario == null) {
                        comentario = "";
                    }
                    String pregunta = rs.getString("numero_pregunta");
                    if (pregunta == null) {
                        pregunta = "";
                    }
                    String once = rs.getString(11);
                    if (once == null) {
                        once = "";
                    }

                    if (pregunta.equalsIgnoreCase("1")) {
                        html += "<div class=\"row\" style=\"margin-bottom: 5px;\">";
                        html += "<div class=\"col-md-3\">";
                        html += "<ul class=\"list-unstyled\">";
                        html += "<li>" + icon + "</li>";
                        html += "<li><strong>Nombre:&nbsp;</strong>" + nombre + "</li>";
                        html += "<li><strong>Edad:&nbsp;</strong>" + edad + "</li>";
                        html += "<li><strong>RUT:&nbsp;</strong>" + rut + "</li>";
                        html += "<li><strong>Sexo:&nbsp;</strong>" + sexo + "</li>";
                        html += "<li><strong>Fecha:&nbsp;</strong>" + jspmkrfn.EW_FormatDateTime(rs.getTimestamp("Fecha_Encuesta"), 8, locale) + "</li>";
                        html += "</ul>";
                        html += "</div>";
                        html += "<div class=\"col-md-9\">";
                        html += "<ul class=\"list-unstyled\">";
                    }

                    html += "<li>" + "<strong>" + EntityDecoder.entityToHtml(categoria) + "</strong>" + "<br>" + EntityDecoder.entityToHtml(comentario) + "</li>";

                    if (pregunta.equalsIgnoreCase("4")) {
                        html += "</ul>";
                        html += "</div>";
                        html += "</div>";
                    }

                    // if (!once.equals(ultimo_id)) {
                    // if (!ultimo_id.equals("")) {
                    // html += "</ul>" + "</td>";
                    // html += "</tr>";
                    // html += "<tr>" + "<td colspan='2'>" + "<hr>" + "</td>" +
                    // "</tr>";
                    // out.println(html);
                    // }
                    //
                    // ultimo_id = once;
                    //
                    // html = "<tr valign='top'>";
                    // html += "<td class\"col-md-3\">"
                    // + "<ul class=\"list-unstyled\">"
                    // + "<li>" + icon + "<br>"
                    // +
                    // "<span class='dashboard-avatar icon32 icon-red icon-user'></span>"
                    // + "<strong>Nombre:</strong> " + nombre + "<br>"
                    // + "<strong>Edad:</strong> " + edad + "<br>"
                    // + "<strong>RUT:</strong> " + rut + "<br>"
                    // + "<strong>Sexo:</strong> " + sexo
                    // + "<br>" + "<strong>Fecha:</strong> " +
                    // jspmkrfn.EW_FormatDateTime(rs.getTimestamp("Fecha_Encuesta"),
                    // 8, locale) + "<br>"
                    // + "</li>"
                    // + "</ul>"
                    // + "</td>";
                    //
                    // html += "<td>" + "<ul class=\"list-unstyled\">" +
                    // "<li><strong>" + EntityDecoder.entityToHtml(categoria) +
                    // "</strong><br>"
                    // + EntityDecoder.entityToHtml(comentario) + "</li>";
                    // } else {
                    // if (pregunta.equalsIgnoreCase("1")) {
                    // html += "<ul class=\"list-unstyled\">";
                    // }
                    // html += "<li><br>" + "<strong>" +
                    // EntityDecoder.entityToHtml(categoria) + "</strong><br>" +
                    // EntityDecoder.entityToHtml(comentario)
                    // + "</li>";
                    // if (pregunta.equalsIgnoreCase("4")) {
                    // html += "</ul><br><hr>";
                    // }
                    // }
                }
                out.print(html);
            } else {
                String msg = ApiServlet.bs_alert("No existen comentarios");
                out.print(msg);
            }
        } catch (SQLException ex) {
            logger.error(ex.toString());
        } finally {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }

                if (!stmt.isClosed()) {
                    stmt.close();
                }
                dbtools.disconnectDB();
            } catch (SQLException sqle) {
                System.out.println(sqle.toString());
            }
        }

        out.close();
    }

    /**
     * @param locale
     * @param rs
     * 
     * @return
     * 
     * @throws SQLException
     */
    public String GenerateChartStatics(Locale locale, ResultSet rs) throws SQLException {
        System.out.println("GenerateChartStatics");
        String str_category = "";
        String str_promotores = "";
        String str_detractores = "";
        String str_nps = "";
        String strXML = "";

        while (rs.next()) {
            str_category = str_category + "<category label='" + rs.getString("Universidad") + "' />" + "\n";
            str_promotores = str_promotores + "<set value='" + cl.intelidata.util.jspmkrfn.EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_Promotor") * 100.0), 0, 0, 0, 0, locale) + "' />"
                            + "\n";
            str_detractores = str_detractores + "<set value='-" + cl.intelidata.util.jspmkrfn.EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_Detractor") * 100.0), 0, 0, 0, 0, locale)
                            + "' />" + "\n";
            str_nps = str_nps + "<set value='" + cl.intelidata.util.jspmkrfn.EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_NPS") * 100.0), 0, 0, 0, 0, locale) + "' />" + "\n";
        }

        strXML += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0'>\n";
        strXML += "<categories >";
        strXML += str_category;
        strXML += "</categories>";
        strXML += "<dataset seriesName='Prom.' >";
        strXML += str_promotores;
        strXML += "</dataset>";
        strXML += "<dataset seriesName='Detr.'>";
        strXML += str_detractores;
        strXML += "</dataset>";
        strXML += "<dataset seriesName='NPS' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
        strXML += str_nps;
        strXML += "</dataset>";
        strXML += "</chart>";

        return strXML;
    }

    /**
     * GENERATE CHART TO SHOW PROGRESS
     * 
     * @param avance
     * @param avance2
     * 
     * @return
     */
    public String getShowProgressChart(Double avance, String avance2, Long objetivo) {
        String strXML = "";
        strXML += "<chart bgColor='FFFFFF' bgAlpha='0' showBorder='0' numberSuffix='%' upperLimit='100' lowerLimit='0' gaugeRoundRadius='5' chartBottomMargin='45' ticksBelowGauge='0' showGaugeLabels='0' valueAbovePointer='1' pointerOnTop='1' pointerRadius='3'>"
                        + "\n";
        strXML += "<colorRange>" + "\n";
        if (avance <= 100.0) {
            strXML += "<color code='5C8F0E' minValue='0' maxValue='" + avance2 + "'/>" + "\n";
            strXML += "<color code='B40001' minValue='" + avance2 + "' maxValue='100'/>" + "\n";
        } else {
            strXML += "<color code='5C8F0E' minValue='0' maxValue='100'/>" + "\n";
            strXML += "<color code='FEDA3F' minValue='101' maxValue='" + avance2 + "'/>" + "\n";
        }
        strXML += "</colorRange>" + "\n";
        strXML += "<value>" + avance2 + "</value>" + "\n";
        strXML += "<styles>" + "\n";
        strXML += "<definition>" + "\n";
        strXML += "<style name='ValueFont' type='Font' bgColor='333333' size='10' color='FFFFFF'/>" + "\n";
        strXML += "</definition>" + "\n";
        strXML += "<application>" + "\n";
        strXML += "<apply toObject='VALUE' styles='valueFont'/>" + "\n";
        strXML += "</application>" + "\n";
        strXML += "</styles>" + "\n";
        strXML += "</chart>" + "\n";
        return strXML;
    }

    /**
     * GENERATE DEFAULT CHART TO SHOW PROGRESS
     * 
     * @return
     */
    private String getDefaultShowProgressChart() {
        String strXML = "";
        strXML += "<chart manageresize=\"1\" palette=\"3\" lowerlimit=\"0\" upperlimit=\"100\" majortmcolor=\"333333\" majortmalpha=\"100\" majortmheight=\"10\" majortmthickness=\"2\" minortmcolor=\"666666\" minortmalpha=\"100\" minortmheight=\"7\" usesamefillcolor=\"1\" showborder=\"0\">";

        strXML += "<colorRange>" + "\n";
        strXML += "<color code='5C8F0E' minValue='0' maxValue='0'/>" + "\n";
        strXML += "<color code='B40001' minValue='0' maxValue='100'/>" + "\n";
        strXML += "</colorRange>" + "\n";
        //
        strXML += "<value>0</value>" + "\n";
        //
        strXML += "<styles>" + "\n";
        //
        strXML += "<definition>" + "\n";
        strXML += "<style name='ValueFont' type='Font' bgColor='333333' size='10' color='FFFFFF'/>" + "\n";
        strXML += "</definition>" + "\n";
        //
        strXML += "<application>" + "\n";
        strXML += "<apply toObject='VALUE' styles='valueFont'/>" + "\n";
        strXML += "</application>" + "\n";
        //
        strXML += "</styles>" + "\n";
        //
        strXML += "</chart>" + "\n";
        return strXML;
    }
}
