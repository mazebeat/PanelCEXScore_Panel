package cl.intelidata.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * Created by Maze on 19-07-2015.
 */
@WebServlet("/ChartServlet")
public class ChartServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doChart_00_muestra(request, response);
		} catch (SQLException ex) {
			System.out.println(ex.toString());
		}

	}

	public void doChart_00_muestra(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		// PRINTER
		PrintWriter out = response.getWriter();

		// HEADERS
		response.setContentType("text/xml");
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

		// VALIDATE PERMISION
		String login = (String)session.getAttribute("Panel_" + cl.intelidata.conf.Text.ProyectoID00 + "_status");
		if (login == null || !login.equals("login")) {
			//			response.sendRedirect("");
			//			response.flushBuffer();
			return;
		}

		// INSTANCE BBDD
		String fileName = "C:\\Users\\Maze\\Google Drive\\Intelidata\\Gemini's\\2015\\1231\\panelExperiencia.properties";
		cl.intelidata.tools.DatabaseTools dbtools = new cl.intelidata.tools.DatabaseTools();
		dbtools.connectDB(fileName);

		// SET VARS
		String periodo_0 = request.getParameter("periodo_0");
		String periodo_1 = request.getParameter("periodo_1");
		String periodo_2 = request.getParameter("periodo_2");
		String periodo_3 = request.getParameter("periodo_3");
		String tipoD = request.getParameter("tipoD");
		String tituloX = request.getParameter("Titulo");

		String strSQL_Periodo = "";
		String strSQL_case01 = "";
		String strSQL_case011 = "";
		String strSQL_case02 = "";
		String strSQL_case022 = "";

		if (periodo_0 != "" && periodo_1 != "") {
			if (periodo_0.equals(periodo_1)) {
				strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
				strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
				strSQL_Periodo = " Periodo = '" + periodo_1 + "' ";
			} else {
				strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' THEN 'ACTUAL' ";
				strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' ";
				strSQL_Periodo = " Periodo >= '" + periodo_0 + "' AND Periodo <= '" + periodo_1 + "' ";
			}
		} else if (periodo_0 == "" && periodo_1 != "") {
			strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";
			strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
			strSQL_Periodo = " Periodo = '" + periodo_1 + "' ";
		}

		//if (periodo_2 != "" && periodo_3 != "") {
		//	if (periodo_2.equals(periodo_3)) {
		//		strSQL_case02 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
		//		strSQL_case022 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' ";
		//		strSQL_Periodo = " Periodo = '" + periodo_3 + "' ";
		//	}
		//	else {
		//		strSQL_case02 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' THEN 'ANTERIOR' ";
		//		strSQL_case022 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' ";
		//		strSQL_Periodo = " Periodo >= '" + periodo_2 + "' AND Periodo <= '" + periodo_3 + "' ";
		//	}
		//}
		String filtros = "";
		String strSQL_Cab = strSQL_case01;

		if (strSQL_case02 != "") {
			strSQL_Cab = strSQL_Cab + " " + strSQL_case02 + " ";
		}

		strSQL_Cab = strSQL_Cab + " END AS CS_Tipo_Fecha, ";
		filtros = filtros + " ( " + strSQL_case011;

		if (strSQL_case02 != "") {
			filtros = filtros + " " + strSQL_case022;
		}

		filtros = filtros + ") AND ";
		String str_SQL_Base = "SELECT " + strSQL_Cab + " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') as CS_Rpta_Periodo, clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + cl.intelidata.conf.Text.ProyectoID00 + "_Max_Reg_Rpta") + " AND clientes_respuesta.id_estado = 15 GROUP BY CS_Tipo_Fecha, CS_Rpta_Periodo, id_cliente";
		String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (" + str_SQL_Base + ") as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";
		String strSQL_Cualificacion = "SELECT 'Encuestas' AS CS_Tipo, COUNT(id_cliente) AS CS_Casos, MAX(id_ultima_rpta) AS Ultima FROM (" + str_SQL_Base + ") AS CS_Temp_Cualificacion";

		String strSQL = "";
		String strSQL_case00 = "";

		if (strSQL_case011 != "") {
			strSQL_case00 = strSQL_case011;
		}

		if (strSQL_case022 != "") {
			strSQL_case00 = strSQL_case022;
		}

		strSQL = "SELECT 'Muestra' AS CS_Tipo, SUM(cs_periodos.meta) AS CS_Casos, '' AS Ultima FROM cs_periodos WHERE " + strSQL_Periodo;
		strSQL = strSQL + " UNION ";
		//strSQL = strSQL + "SELECT 'Encuestas' AS CS_Tipo, COUNT(respuestas.id_cliente) AS CS_Casos, MAX(clientes_respuesta.ultima_respuesta) as Ultima FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario;
		strSQL = strSQL + "SELECT 'Encuestas' AS CS_Tipo, COUNT(clientes_respuesta.id_cliente) AS CS_Casos, MAX(clientes_respuesta.ultima_respuesta) as Ultima FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente " + strSQL_Ultima_rpta_x_usuario;

		//		System.out.println(strSQL);
		//		out.println (strSQL);
		//		out.close();
		//	out.close(); // Close the stream. Future calls will fail.
		//out.println(strSQL + "<br>");
		String strXML = "";
		Statement stmt = null;
		ResultSet rs = null;
		Object x_FECHA = null;
		int rowcount = 0;

		try {
			//  CONSULTING DATABASE
			stmt = dbtools.getConexion().createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(strSQL);

			if (rs.last()) {
				//				out.println("paso 01<br>");
				rowcount = rs.getRow();
				//				out.println(rowcount + "<br>");
				rs.beforeFirst();
				rs.next();
				//				out.println("paso 02<br>");
				Long objetivo = rs.getLong("CS_Casos");
				rs.next();
				//				out.println("paso 03<br>");
				Long llevamos = rs.getLong("CS_Casos");

				if (rs.getTimestamp("Ultima") != null) {
					x_FECHA = rs.getTimestamp("Ultima");
				} else {
					x_FECHA = "";
				}

				rs.next();

				//				out.println("paso 04<br>");
				//				// llevamos = llevamos + rs.getLong("CS_Casos");
				//				out.println("paso 05<br>");
				Double avance = ((double)llevamos / (double)objetivo) * 100;
				String avance2 = cl.intelidata.otros.jspmkrfn.EW_FormatNumber(String.valueOf(avance), 0, 0, 0, 0, locale);
				avance2 = avance2.replace(",", ".");

				//		strXML = "<?xml version='1.0'?>" + "\n";
				// GENERATE CHART
				if (tipoD.equals("1")) {
					strXML += "<chart bgColor='FFFFFF' bgAlpha='0' showBorder='0' numberSuffix='%' upperLimit='100' lowerLimit='0' gaugeRoundRadius='5' chartBottomMargin='45' ticksBelowGauge='0' showGaugeLabels='0' valueAbovePointer='1' pointerOnTop='1' pointerRadius='3'>" + "\n";
					//
					strXML += "<colorRange>" + "\n";
					if (avance <= 100.0) {
						strXML += "<color code='5C8F0E' minValue='0' maxValue='" + avance2 + "'/>" + "\n";
						strXML += "<color code='B40001' minValue='" + avance2 + "' maxValue='100'/>" + "\n";
					} else {
						strXML += "<color code='5C8F0E' minValue='0' maxValue='100'/>" + "\n";
						strXML += "<color code='FEDA3F' minValue='101' maxValue='" + avance2 + "'/>" + "\n";
					}
					strXML += "</colorRange>" + "\n";
					//
					strXML += "<value>" + avance2 + "</value>" + "\n";
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
					response.setContentType("text/xml");
				} else {
					strXML = "<center><span class='label3 label-info2'>Total Muestra " + tituloX + "</span><br><span class='label3 label-info2'>Objetivo " + objetivo + " alumnos</span><br><span class='label3 label-info2'>Llevamos " + llevamos + " encuestados</span><br><span class='label3 label-info2'>&Uacute;ltima actualizaci&oacute;n:<br>" + cl.intelidata.otros.jspmkrfn.EW_FormatDateTime(x_FECHA, 8, locale) + "</span><center>";
				}
				// out.println(strXML);
			} else {
				if (tipoD.equals("1")) {
					strXML += "<chart bgColor='FFFFFF' bgAlpha='0' showBorder='0' numberSuffix='%' upperLimit='100' lowerLimit='0' gaugeRoundRadius='5' chartBottomMargin='45' ticksBelowGauge='0' showGaugeLabels='0' valueAbovePointer='1' pointerOnTop='1' pointerRadius='3'>" + "\n";
					//
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
					// response.setContentType("text/xml");
				} else {
					strXML = "<center><span class='label3 label-info2'>Total Muestra</span><br><span class='label3 label-info2'>Objetivo -- alumnos</span><br><span class='label3 label-info2'>Llevamos -- encuestados</span><br><span class='label3 label-info2'>&Uacute;ltima actualizaci&oacute;n:<br>--</span><center>";
				}
			}
			// Close recordset and connection

		} catch (Exception ex) {
			System.out.println(ex.toString());
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
				System.out.println(sqle.toString());
			}
		}
		//
		out.println(strXML);
		out.close();
	}
}
