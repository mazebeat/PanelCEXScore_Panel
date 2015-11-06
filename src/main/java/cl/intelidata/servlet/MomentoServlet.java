package cl.intelidata.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.EntityDecoder;
import cl.intelidata.util.Text;
import cl.intelidata.util.jspmkrfn;

public class MomentoServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(MomentoServlet.class);
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
	public MomentoServlet() {
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
	 * <p/>
	 * This method is called when a HTTP delete request is received.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * 
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * <p/>
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * 
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
			if (chart.toLowerCase().equalsIgnoreCase("02_periodo_indicadores")) {
				doChart_02_periodo_indicadores(request, response);
			} else if (chart.toLowerCase().equalsIgnoreCase("03_benchmark")) {
				doChart_03_benchmark(request, response);
			} else if (chart.toLowerCase().equalsIgnoreCase("04_tendencia")) {
				doChart_04_tendencia(request, response);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
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

			// MOMENTO
			String filterMoment = "";
			String filterMomentNPS = "";
			String moment = "";
			try {
				moment = request.getParameter("moment");

				if (moment == null || moment == "") {
					filterMoment = "";
					filterMomentNPS = "";
				} else {
					filterMoment = " AND respuesta.id_momento = " + moment;
					filterMomentNPS = " AND nps.id_momento = " + moment;
				}
			} catch (Exception ex) {
				logger.error(ex.toString());
			}

			// FILTROS
			String strSQL_Cab = ApiServlet.getPeriodsQuery(periodo_0, periodo_1, periodo_2, periodo_3);
			String filtros = ApiServlet.getFiltersQuery(periodo_0, periodo_1, periodo_2, periodo_3);
			String strSQL_CabNPS = ApiServlet.getPeriodsQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);
			String filtrosNPS = ApiServlet.getFiltersQueryNPS(periodo_0, periodo_1, periodo_2, periodo_3);

			// QUERY'S -----------------------------
			// SQL CUALIFICACION
			String strSQL_Cualificacion = "SELECT  \n" + "'Encuesta' AS CS_Tipo,\n" + "CONVERT(count(*)/ 4,UNSIGNED INTEGER) As CS_Casos,\n" + "'' AS Ultima\n"
							+ "FROM cliente_respuesta \n" + "INNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
							+ "INNER JOIN respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n" + "WHERE\n" + "\t\t" + filtros + " \n"
							+ "\t\tAND cliente.id_cliente = " + ID_CLIENTE + " \n" + filterMoment + " \n";

			// SQL NPS
			String strSQL_NPS = "SELECT \n" + strSQL_CabNPS + " \n" + " nps.promedio \n" + "FROM nps \n" + "WHERE nps.id_cliente = " + ID_CLIENTE + " \n"
							+ "AND " + filtrosNPS + " \n" + filterMomentNPS;

			// SQL LEALTAD
			String strSQL_Lealtad = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN valor2 = 'NO' THEN 1 ELSE 0 END) AS Leal_NO,\n"
							+ "\tSUM(CASE WHEN valor2 = 'SI' THEN 1 END) AS Leal_SI\n" + "FROM (\n" + "\tSELECT \n" + "\t\t"
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
							+ "\tWHERE cliente.id_cliente = "
							+ ID_CLIENTE
							+ "\n"
							+ "\tAND respuesta.id_encuesta = cliente.id_encuesta\n"
							+ "\tAND (pregunta_cabecera.numero_pregunta = 4)"
							+ filterMoment
							+ " \n"
							+ "\tAND ("
							+ filtros
							+ ") \n"
							+ ") AS Datos\n"
							+ "GROUP BY CS_Tipo_Fecha\n" + "ORDER BY CS_Tipo_Fecha\n";

			// SQL PREGUNTAS
			String strSQL_Preguntas = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tnumero_pregunta,\n" + "\tSUM(NPS_7) AS NPS_7,\n" + "\tSUM(NPS_5) AS NPS_5,\n"
							+ "\tSUM(NPS_4) AS NPS_4\n" + "FROM ( \n" + "\tSELECT \n" + "\t\t"
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
							+ filterMoment
							+ " \n"
							+ "\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
							+ "\tAND ("
							+ filtros
							+ ") \n"
							+ "\tGROUP BY respuesta.id_respuesta\n"
							+ ") AS Datos_Tmp\n"
							+ "GROUP BY CS_Tipo_Fecha , numero_pregunta\n" + "ORDER BY CS_Tipo_Fecha , numero_pregunta;";

			int cant_registro_rpta = 0;
			String porc_lealtad = "";
			String html_preg = "";
			String strXML = "";
			String base = "";

			ENCONTRO_ANTERIOR = 0;
			cant_registro_rpta = ApiServlet.getNumberOfQuestions(strSQL_Cualificacion);
			logger.info("CANT RESPUESTAS MOMENTO (" + moment + "/" + mostrar_como + "): " + cant_registro_rpta);

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
	 * DOCHART_03_BENCHMARK -------------------------------------- OK
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

			// FILTROS
			String strSQL_Cab = ApiServlet.getPeriodsQuery(periodo_0, periodo_1, periodo_2, periodo_3);
			String filtros = ApiServlet.getFiltersQuery(periodo_0, periodo_1, periodo_2, periodo_3);
			// String strSQL_CabNPS = ApiServlet.getPeriodsQueryNPS(periodo_0,
			// periodo_1, periodo_2, periodo_3);
			// String filtrosNPS = ApiServlet.getFiltersQueryNPS(periodo_0,
			// periodo_1, periodo_2, periodo_3);

			// QUERY'S ----------------------
			// SQL CUALIFICACION
			String strSQL_Cualificacion = "SELECT  \n" + "'Encuesta' AS CS_Tipo,\n" + "CONVERT(count(*)/ 4,UNSIGNED INTEGER) As CS_Casos,\n" + "'' AS Ultima\n"
							+ "FROM cliente_respuesta \n" + "INNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
							+ "INNER JOIN respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n" + "WHERE\n" + "\t\t" + filtros + " \n"
							+ "\t\tAND cliente.id_cliente != " + ID_CLIENTE + " \n" + "\t\tAND cliente.id_sector = " + ID_SECTOR + " \n" + filterMoment + " \n"
							+ "AND DATE_FORMAT(ultima_respuesta, '%Y-%m') between '2014-12' AND '2015-07'\n";

			// SQL NPS
			String strSQL_NPS = "SELECT\n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN Promedio >= 6 THEN 1 ELSE 0 END) AS NPS_7,\n"
							+ "\tSUM(CASE WHEN Promedio < 6 AND Promedio > 4 THEN 1 ELSE 0 END) AS NPS_5,\n"
							+ "\tSUM(CASE WHEN Promedio <= 4 THEN 1 ELSE 0 END) AS NPS_4\n" + "FROM ( \n" + "\tSELECT \n"
							+ "\tUltima_rpta_x_usuario.CS_Tipo_Fecha,\n" + "\tUltima_rpta_x_usuario.CS_Rpta_Periodo,\n" + "\trespuesta.id_cliente,\n"
							+ "\t'' CS_Rpta_Perio,\n" + "\tROUND(AVG(respuesta_detalle.valor1), 1) AS Promedio\n" + "\tFROM respuesta\n"
							+ "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
							+ "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
							+ "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
							+ "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
							+ "\tINNER JOIN (\n" + "\t\tSELECT \n" + "\t\t\t"
							+ strSQL_Cab
							+ "\n"
							+ "\t\t\tDATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') AS CS_Rpta_Periodo, \n"
							+ "\t\t\tcliente_respuesta.id_cliente, \n"
							+ "\t\t\tMAX(cliente_respuesta.id_cliente_respuesta) AS id_ultima_rpta\n"
							+ "\t\tFROM cliente_respuesta\n"
							+ "\t\tGROUP BY CS_Tipo_Fecha , CS_Rpta_Periodo , id_cliente\n"
							+ "\t ) AS Ultima_rpta_x_usuario\n"
							+ "\tON cliente_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente\n"
							+ "\t\t\t\n"
							+ "\tWHERE cliente.id_cliente != "
							+ ID_CLIENTE
							+ "\n"
							+ "\t\tAND cliente.id_sector = "
							+ ID_SECTOR
							+ " \n"
							+ "\tAND respuesta.id_encuesta = cliente.id_encuesta\n"
							+ filterMoment
							+ " \n"
							+ "\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
							+ "\tAND ("
							+ filtros
							+ ") \n"
							+ "\tGROUP BY respuesta.id_respuesta \n"
							+ ") AS Datos_Tmp\n"
							+ "GROUP BY CS_Tipo_Fecha\n"
							+ "ORDER BY CS_Tipo_Fecha";

			// SQL LEALTAD
			String strSQL_Lealtad = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN valor2 = 'NO' THEN 1 ELSE 0 END) AS Leal_NO,\n"
							+ "\tSUM(CASE WHEN valor2 = 'SI' THEN 1 END) AS Leal_SI\n" + "FROM (\n" + "\tSELECT \n" + "\t\t"
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
							+ filterMoment
							+ " \n"
							+ "\tAND (pregunta_cabecera.numero_pregunta = 4)"
							+ "\tAND (" + filtros + ") \n" + ") AS Datos\n" + "GROUP BY CS_Tipo_Fecha\n" + "ORDER BY CS_Tipo_Fecha\n";

			// SQL LEALTAD
			String strSQL_Preguntas = "SELECT \n" + "\tCS_Tipo_Fecha,\n" + "\tnumero_pregunta,\n" + "\tSUM(NPS_7) AS NPS_7,\n" + "\tSUM(NPS_5) AS NPS_5,\n"
							+ "\tSUM(NPS_4) AS NPS_4\n" + "FROM ( \n" + "\tSELECT \n" + "\t\t"
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
							+ filterMoment
							+ " \n"
							+
							// "\tAND DATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') between '2015-07' AND '2015-07'\n"
							// +
							"\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
							+ "\tAND ("
							+ filtros
							+ ") \n"
							+ "\tGROUP BY respuesta.id_respuesta\n"
							+ ") AS Datos_Tmp\n"
							+ "GROUP BY CS_Tipo_Fecha , numero_pregunta\n" + "ORDER BY CS_Tipo_Fecha , numero_pregunta;";

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

				porc_lealtad = "-";
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
	 * 
	 * @param request
	 * @param response
	 * @throws java.io.IOException
	 */
	public void doChart_04_tendencia(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
		PrintWriter out = null;
		ResultSet rs = null;
		Statement stmt = null;

		// INSTANCE BBDD
		DatabaseTools dbtools = new DatabaseTools();
		dbtools.connectDB();

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

			String periodo_0 = request.getParameter("periodo_0");
			String periodo_1 = request.getParameter("periodo_1");
			String periodo_2 = request.getParameter("periodo_2");
			String periodo_3 = request.getParameter("periodo_3");
			String filtros = "";

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

			String strSQL_case01 = "";
			String strSQL_case011 = "";
			String strSQL_case02 = "";
			String strSQL_case022 = "";

			if (periodo_0 != "" && periodo_1 != "") {
				if (periodo_0.equals(periodo_1)) {
					strSQL_case01 = " CASE WHEN DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
					strSQL_case011 = " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
				} else {
					strSQL_case01 = " CASE WHEN DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0
									+ "' AND DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' THEN 'ACTUAL' ";
					strSQL_case011 = " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0
									+ "' AND DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' ";
				}
			} else if (periodo_0 == "" && periodo_1 != "") {
				strSQL_case01 = " CASE WHEN DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
				strSQL_case011 = " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
			}

			if (periodo_2 != "" && periodo_3 != "") {
				if (periodo_2.equals(periodo_3)) {
					strSQL_case02 = " WHEN DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
					strSQL_case022 = " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' ";
				} else {
					strSQL_case02 = " WHEN DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2
									+ "' AND DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' THEN 'ANTERIOR' ";
					strSQL_case022 = " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2
									+ "' AND DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' ";
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

			// QUERY'S ----------------------
			// SQL CUALIFICACION
			// String str_SQL_Base = "SELECT "
			// + strSQL_Cab
			// +
			// " DATE_FORMAT( cliente_respuesta.ultima_respuesta,'%Y-%m') as CS_Rpta_Periodo,  cliente_respuesta.id_cliente FROM  cliente_respuesta INNER JOIN clientes ON clientes.id_cliente =  cliente_respuesta.id_cliente WHERE "
			// + filtros +
			// " GROUP BY CS_Tipo_Fecha, CS_Rpta_Periodo, id_cliente";

			// SQL NSP
			String strSQL_NPS = "SELECT\n" + "\tCS_Rpta_Periodo,\n" + "\tCS_Tipo_Fecha,\n" + "\tSUM(CASE WHEN Promedio >= 6 THEN 1 ELSE 0 END) AS NPS_7,\n"
							+ "\tSUM(CASE WHEN Promedio < 6 AND Promedio > 4 THEN 1 ELSE 0 END) AS NPS_5,\n"
							+ "\tSUM(CASE WHEN Promedio <= 4 THEN 1 ELSE 0 END) AS NPS_4\n" + "FROM ( \n" + "\tSELECT \n"
							+ "\tUltima_rpta_x_usuario.CS_Tipo_Fecha,\n" + "\tUltima_rpta_x_usuario.CS_Rpta_Periodo,\n" + "\trespuesta.id_cliente,\n"
							+ "\t'' CS_Rpta_Perio,\n" + "\tROUND(AVG(respuesta_detalle.valor1), 1) AS Promedio\n" + "\tFROM respuesta\n"
							+ "\tINNER JOIN respuesta_detalle ON respuesta.id_respuesta = respuesta_detalle.id_respuesta\n"
							+ "\tINNER JOIN cliente_respuesta ON cliente_respuesta.id_respuesta = respuesta.id_respuesta\n"
							+ "\tINNER JOIN cliente ON cliente.id_cliente = cliente_respuesta.id_cliente\n"
							+ "\tINNER JOIN pregunta_cabecera ON respuesta.id_pregunta_cabecera = pregunta_cabecera.id_pregunta_cabecera\n"
							+ "\tINNER JOIN (\n" + "\t\tSELECT \n" + "\t\t\t"
							+ strSQL_Cab
							+ "\n"
							+ "\t\t\tDATE_FORMAT(cliente_respuesta.ultima_respuesta, '%Y-%m') AS CS_Rpta_Periodo, \n"
							+ "\t\t\tcliente_respuesta.id_cliente, \n"
							+ "\t\t\tMAX(cliente_respuesta.id_cliente_respuesta) AS id_ultima_rpta\n"
							+ "\t\tFROM cliente_respuesta\n"
							+ "\t\tGROUP BY CS_Tipo_Fecha , CS_Rpta_Periodo , id_cliente\n"
							+ "\t ) AS Ultima_rpta_x_usuario\n"
							+ "\tON cliente_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente\n"
							+ "\t\t\t\n"
							+ "\tWHERE cliente.id_cliente = "
							+ ID_CLIENTE
							+ "\n"
							+ "\tAND "
							+ filtros
							+ " \n"
							+ "\trespuesta.id_encuesta = cliente.id_encuesta\n"
							+ "\tAND (pregunta_cabecera.numero_pregunta = 1 OR pregunta_cabecera.numero_pregunta = 2 OR pregunta_cabecera.numero_pregunta = 3)\n"
							+ filterMoment
							+ " \n"
							+ "\tGROUP BY respuesta.id_respuesta \n"
							+ ") AS Datos_Tmp\n"
							+ "GROUP BY CS_Tipo_Fecha\n"
							+ "ORDER BY CS_Tipo_Fecha";

			String strXML = "";

			int mes = 0;
			String actual = "ACTUAL";
			String anterior = "ANTERIOR";
			stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(strSQL_NPS);
			// int rowcount = 0;
			if (rs.last()) {
				// rowcount = rs.getRow();

				strXML += "<chart showValues='0' numberSuffix='%' bgColor='ffffff' borderColor ='ffffff'  labelDisplay='Rotate' slantLabels='1'>" + "\n";
				strXML += "<categories>" + "\n";
				rs.beforeFirst();
				while (rs.next()) {
					if (actual.equals(rs.getString("CS_Tipo_Fecha"))) {
						mes += 1;
						// strXML += "<category label='Mes " + mes + "' />" +
						// "\n";
						strXML += "<category label='" + rs.getString("CS_Rpta_Periodo") + "' />" + "\n";
					}
				}
				if (mes == 0) {
					rs.beforeFirst();
					while (rs.next()) {
						if (anterior.equals(rs.getString("CS_Tipo_Fecha"))) {
							mes += 1;
							// strXML += "<category label='Mes " + mes + "' />"
							// + "\n";
							strXML += "<category label='" + rs.getString("CS_Rpta_Periodo") + "' />" + "\n";
						}
					}
				}
				if (mes == 0) {
					strXML += "<category label='Mes' />" + "\n";
				}
				strXML += "</categories>" + "\n";

				strXML += "<dataset seriesName='Actual'>" + "\n";
				rs.beforeFirst();
				Double neta2 = 0.0;
				String neta3 = "";
				while (rs.next()) {
					if (actual.equals(rs.getString("CS_Tipo_Fecha"))) {
						neta2 = ((rs.getDouble("NPS_7") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100)
										- ((rs.getDouble("NPS_4") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100);
						neta3 = jspmkrfn.EW_FormatNumber(String.valueOf(neta2), 0, 0, 0, 0, locale);
						neta3 = neta3.replace(",", ".");
						strXML += "<set value='" + neta3 + "' />" + "\n";
					}
				}
				if (neta3 == "") {
					strXML += "<set value='0' />" + "\n";
				}
				strXML += "</dataset>" + "\n";

				if (strSQL_case02 != "") {
					strXML += "<dataset seriesName='Compara'>" + "\n";
					rs.beforeFirst();
					neta3 = "";
					while (rs.next()) {
						if (anterior.equals(rs.getString("CS_Tipo_Fecha"))) {
							neta2 = ((rs.getDouble("NPS_7") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100)
											- ((rs.getDouble("NPS_4") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100);
							neta3 = jspmkrfn.EW_FormatNumber(String.valueOf(neta2), 0, 0, 0, 0, locale);
							neta3 = neta3.replace(",", ".");
							strXML += "<set value='" + neta3 + "' />" + "\n";
						}
					}
					if (neta3 == "") {
						strXML += "<set value='0' />" + "\n";
					}
					strXML += "</dataset>" + "\n";
				}

				strXML += "<styles>" + "\n";
				strXML += "<definition>" + "\n";
				strXML += "<style name='CanvasAnim' type='animation' param='_xScale' start='0' duration='1' />" + "\n";
				strXML += "</definition>" + "\n";
				strXML += "<application>" + "\n";
				strXML += "<apply toObject='Canvas' styles='CanvasAnim' />" + "\n";
				strXML += "</application>" + "\n";
				strXML += "</styles>" + "\n";
				strXML += "</chart>" + "\n";
				response.setContentType("text/xml");

			} else {
				strXML += "<chart showValues='0' numberSuffix='%' bgColor='ffffff' borderColor ='ffffff' labelDisplay='Rotate' slantLabels='1'>" + "\n";
				strXML += "<categories>" + "\n";
				strXML += "<category label='-' />" + "\n";
				strXML += "</categories>" + "\n";
				strXML += "<dataset seriesName='Actual'>" + "\n";
				strXML += "<set value='0' />" + "\n";
				strXML += "</dataset>" + "\n";
				// strXML += "<dataset seriesName='Compara'>" + "\n";
				// strXML += "<set value='0'/>" + "\n";
				// strXML += "</dataset>" + "\n";
				strXML += "<styles>" + "\n";
				strXML += "<definition>" + "\n";
				strXML += "<style name='CanvasAnim' type='animation' param='_xScale' start='0' duration='1' />" + "\n";
				strXML += "</definition>" + "\n";
				strXML += "<application>" + "\n";
				strXML += "<apply toObject='Canvas' styles='CanvasAnim' />" + "\n";
				strXML += "</application>" + "\n";
				strXML += "</styles>" + "\n";
				strXML += "</chart>" + "\n";
				response.setContentType("text/xml");
			}
			out.println(strXML);

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
	 * The doPost method of the servlet. <br>
	 * <p/>
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * 
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
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
	 * <p/>
	 * This method is called when a HTTP put request is received.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * 
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Put your code here
	}

	/**
	 * Returns information about the servlet, such as author, version, and
	 * copyright.
	 * 
	 * @return String information about this servlet
	 */
	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
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

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public static String generateMoments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// HEADERS
		HttpSession session = request.getSession();

		DatabaseTools dbtools = new DatabaseTools();
		dbtools.connectDB();

		// PrintWriter out = null;
		ResultSet rs = null;
		Statement stmt = null;
		String output = "";

		try {
			int count_moments = 0;
			ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());
			ID_SECTOR = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdSector").toString());
			ArrayList<String> momentsNames = new ArrayList<String>();
			ArrayList<String> canal = new ArrayList<String>();

			// SELECT ALL MOMENTS BY CLIENT
			String strSQL_moments = "SELECT \n"
							+ "\tmomento_encuesta.id_momento, \n"
							+ "\tmomento_encuesta.descripcion_momento, \n" 
							+ "\tcanal.descripcion_canal \n"
							+ "FROM momento_encuesta \n"
							+ "INNER JOIN encuesta ON momento_encuesta.id_encuesta = encuesta.id_encuesta \n"
							+ "INNER JOIN cliente ON encuesta.id_encuesta = cliente.id_encuesta \n"
							+ "INNER JOIN pregunta_cabecera ON encuesta.id_encuesta = pregunta_cabecera.id_encuesta \n"
							+ "INNER JOIN respuesta ON pregunta_cabecera.id_pregunta_cabecera = respuesta.id_pregunta_cabecera \n"
							+ "INNER JOIN canal ON respuesta.id_canal = canal.id_canal \n"
							+ "WHERE cliente.id_cliente = " + ID_CLIENTE + " \n"
							+ "GROUP BY momento_encuesta.id_momento \n" 
							+ "ORDER BY momento_encuesta.id_momento ASC";

			stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(strSQL_moments);
			count_moments = ApiServlet.getRows(rs);
			count_moments = count_moments + 1;
			request.setAttribute("count_moments", count_moments);

			while (rs.next()) {
				momentsNames.add(EntityDecoder.charToHtml(rs.getString("descripcion_momento")));
				canal.add(EntityDecoder.charToHtml(rs.getString("descripcion_canal")));			
			}

			logger.info("MOMENTOS ENCONTRADOS PARA ID_CLIENTE " + ID_CLIENTE + ": " + momentsNames.toString());

			int moments = 1;
			int row = 1;
			int cantByRow = 3;
			int classRowMax = 12;

			if (cantByRow > classRowMax) {
				cantByRow = classRowMax;
			}

			int classColByRow = classRowMax / cantByRow;

			for (int y = 1; y < count_moments; y++) {
				String name = momentsNames.get((moments - 1));
				String can = canal.get((moments - 1));
				logger.info("CARGANDO MOMENTO: " + name);

				// BEGIN ROW
				if (row == 1) {
					output += "<div id='moment_boxes_" + y + "' class='row'>";
				}

				// BEGIN COL-MD-*
				output += "<div class='col-md-" + classColByRow + "'>";
				// BEGIN PANEL
				output += "<div class='panel panel-warning'>";
				// // BEGIN PANEL-HEADING
				output += "\t<div class='panel-heading' data-original-title>";
				output += "\t\t<h3 class='panel-title'>";
				output += "\t\t\t<i class='fa fa-code-fork fa-fw'></i> " + name + "<span class='pull-right'>" + can + "</span>";
				output += "\t\t</h3>";
				output += "</div>";
				// // END PANEL-HEADING
				
				// // BEGIN PANEL-BODY
				output += "<div class='panel-body'>";

				// // BEGIN CONTENT MOMENT
				output += "<div class='row'>";
				output += "\t<div class='col-md-12'>";
				output += "\t\t<div class='row'>";
				output += "\t\t\t<div class='col-md-12'>";
				output += "\t\t\t\t<span class='text-left'><div id='export_moment_" + y + "'></div></span>";
				output += "\t\t\t</div>";
				output += "\t\t\t<div class='box-content col-md-12' id='chart_moment_" + y + "'></div>";
				output += "\t\t\t<div class='box-content2 col-md-12' id='detail_moment_" + y + "'></div>";
				output += "\t\t</div>";
				output += "\t</div>";
				output += "</div>";
				output += "<div class='row'>";
				output += "\t<div class='col-md-12'>";
				output += "\t\t<h6>Tendencia</h6>";				
				output += "\t\t<div class='box-content2 col-md-12' id='trend_moment_" + y + "'></div>";
				output += "\t</div>";
				output += "</div>";
				// // END CONTENT MOMENT

				output += "</div>";
				// // END PANEL-BODY

				output += "</div>";
				// END PANEL

				output += "</div>";
				// END COL-MD-6

				if (row == cantByRow && moments >= cantByRow || y == (count_moments - 1)) {
					output += "</div>";
					row = 1;
				} else {
					row++;
				}
				// END ROW
				moments++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

		return output;
	}
}
