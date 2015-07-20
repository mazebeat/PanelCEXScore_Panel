<%@ page session="true" buffer="16kb" import="java.sql.*,java.util.*,java.io.*,java.text.*"%>
<%@ page contentType="text/html; charset=ISO-8859-1" %>
HOLA JIL
<%
response.setDateHeader("Expires", 0); // date in the past
response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1 
response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 
response.addHeader("Pragma", "no-cache"); // HTTP/1.0 
%>
<% Locale locale = Locale.getDefault();
response.setLocale(locale);%>
<% session.setMaxInactiveInterval(30*60); %>
<%@ include file="../inc/ProyectoID00.jsp" %>
<% 
String login = (String) session.getAttribute("Panel_" + ProyectoID00 + "_status");
if (login == null || !login.equals("login")) {
response.sendRedirect("");
response.flushBuffer(); 
return; 
}%>

<%@ include file="../inc/db.jsp" %>

<% 

Statement stmtSeguridad = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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

// get current table security
int ewCurSec = 0; // initialise
int ID_Perfil = 0;
if (session.getAttribute("Panel_" + ProyectoID00 + "_status_UserLevel") != null) {
	int ewCurIdx = ((Integer) session.getAttribute("Panel_" + ProyectoID00 + "_status_UserLevel")).intValue();
	ID_Perfil = ewCurIdx;
	if (ewCurIdx == -1) { // system administrator
		ewCurSec = 31;
	} else if (ewCurIdx > 0 && ewCurIdx <= rowcountSeguridad) { 
//		ewCurSec = ew_SecTable[ewCurIdx-1];
		ewCurSec = (Integer) ew_SecTableSeguridad.get(ewCurIdx-1);
	}
}
%>

<%@ include file="../inc/jspmkrfn.jsp" %>

<%

String periodo_0 = request.getParameter("periodo_0");
String periodo_1 = request.getParameter("periodo_1");
String periodo_2 = request.getParameter("periodo_2");
String periodo_3 = request.getParameter("periodo_3");
String mostrar_como = request.getParameter("mostrar_como");

String filtro_sede = request.getParameter("sede");
String filtro_facultad = request.getParameter("facultad");
String filtro_campus = request.getParameter("campus");
String filtros = "";

if (filtro_sede != "") 
{
	//filtros = filtros + " bdd_umayor.facultad = '" + filtro_facultad + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.sede, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_sede + "' AND ";
}

if (filtro_facultad != "")
{
	//filtros = filtros + " bdd_umayor.facultad = '" + filtro_facultad + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.facultad, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_facultad + "' AND ";
}

if (filtro_campus != "")
{
	//filtros = filtros + " bdd_umayor.campus = '" + filtro_campus + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.campus, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_campus + "' AND ";
}




String strSQL_case01 = "";
String strSQL_case011 = "";
String strSQL_case02 = "";
String strSQL_case022 = "";

if (periodo_0 != "" && periodo_1 != ""){

	if (periodo_0.equals(periodo_1)) {
		strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";

		strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
	}
	else {
		strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' THEN 'ACTUAL' ";

		strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_0 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_1 + "' ";
	}
}
else if (periodo_0 == "" && periodo_1 != "") {

	strSQL_case01 = " CASE WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' THEN 'ACTUAL' ";

	strSQL_case011 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_1 + "' ";
}

if (periodo_2 != "" && periodo_3 != "") {

	if (periodo_2.equals(periodo_3)) {

		strSQL_case02 = " WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' THEN 'ANTERIOR' ";
	
		strSQL_case022 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') = '" + periodo_3 + "' ";
		
	}
	else {
		
		strSQL_case02 = " WHEN DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' THEN 'ANTERIOR' ";
	
		strSQL_case022 = " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') >= '" + periodo_2 + "' AND DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') <= '" + periodo_3 + "' ";
	}

}

String strSQL_Cab = strSQL_case01;
if (strSQL_case02 != "") strSQL_Cab = strSQL_Cab + " " + strSQL_case02 + " ";
strSQL_Cab = strSQL_Cab + " END AS CS_Tipo_Fecha, ";

//if (filtros != "") filtros = filtros + " AND ";

filtros = filtros + " ( " + strSQL_case011;

if (strSQL_case02 != "") filtros = filtros + " or " + strSQL_case022;

filtros = filtros + ") AND ";

String str_SQL_Base = "SELECT " + strSQL_Cab + " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') as CS_Rpta_Periodo, clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " AND clientes_respuesta.id_estado = 15 GROUP BY CS_Tipo_Fecha, CS_Rpta_Periodo, id_cliente";

//out.println("str_SQL_Base: " + str_SQL_Base + "<br>");

String strSQL_Cualificacion = "Select count(id_cliente) as Casos from (" + str_SQL_Base + ") as CS_Temp_Cualificacion";

//out.println("strSQL_Cualificacion: " + strSQL_Cualificacion + "<br>");

String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (" + str_SQL_Base + ") as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";

//out.println("strSQL_Ultima_rpta_x_usuario: " + strSQL_Ultima_rpta_x_usuario + "<br>");

String strSQL_NPS = "SELECT CS_Tipo_Fecha, Sum(case when Promedio >= 6 then 1 else 0 END) as NPS_7, Sum(case when Promedio < 6 and Promedio > 4 then 1 else 0 END) as NPS_5, Sum(case when Promedio <= 4 then 1 else 0 END) as NPS_4  FROM (SELECT Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_cliente, ROUND(AVG(respuestas_detalle.valor1 ),1) as Promedio FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_cliente ) as Datos GROUP BY CS_Tipo_Fecha ORDER BY CS_Tipo_Fecha";

//out.println("strSQL_NPS: " + strSQL_NPS + "<br>");

String strSQL_Lealtad = "SELECT CS_Tipo_Fecha, Sum(case when valor1 = 1 then 1 else 0 END) as Leal_NO, Sum(case when valor1 = 2 then 1 else 0 END) as Leal_SI FROM (SELECT Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_cliente, respuestas_detalle.valor1 FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 4)) as Datos GROUP BY CS_Tipo_Fecha ORDER BY CS_Tipo_Fecha";

//out.println("strSQL_Lealtad: " + strSQL_Lealtad + "<br>");

String strSQL_Preguntas = "SELECT CS_Tipo_Fecha, id_pregunta, SUM(NPS_7) as NPS_7, SUM(NPS_5) as NPS_5, SUM(NPS_4) as NPS_4 FROM (SELECT Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_pregunta, case when respuestas_detalle.valor1 >= 6 then 1 else 0 end AS NPS_7, case when respuestas_detalle.valor1 < 6 and respuestas_detalle.valor1 > 4 then 1 else 0 end AS NPS_5, case when respuestas_detalle.valor1 <= 4  then 1 else 0 end AS NPS_4 FROM respuestas INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta INNER JOIN clientes_respuesta ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 OR respuestas.id_pregunta = 2 OR respuestas.id_pregunta = 3) GROUP BY Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_respuesta ) AS Datos_Tmp GROUP BY CS_Tipo_Fecha, id_pregunta ORDER BY CS_Tipo_Fecha, id_pregunta";

//out.println("strSQL_Preguntas: " + strSQL_Preguntas + "<br>");


int cant_registro_rpta = 0;

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

Double cant_promotores = 0.0;
Double cant_detractores = 0.0;

Double cant_leal = 0.0;
Double cant_leal_no = 0.0;

String porc_cualificacion = "";
String porc_nps = "";
String porc_promotores  = "";
String porc_detractores = "";

String porc_lealtad = "";

String html_preg_etiqueta = "";
String html_preg = "";

String etiqueta_porc_7 = "";
String etiqueta_porc_4 = "";
String etiqueta_porc_nps = "";

String porc_nps_2 = "";

String strXML="";

int encontro_anterior = 0;

try{
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	
	//out.println("Paso 01 <br>");
	//out.println(strSQL_Cualificacion);
	//out.flush(); // Send out whatever hasn't been sent out yet.
	//out.close(); // Close the stream. Future calls will fail.


	rs = stmt.executeQuery(strSQL_Cualificacion);
	int rowcount = 0;
	if (rs.last()) {
		rs.beforeFirst();
		rs.next();
		cant_registro_rpta = rs.getInt("Casos");
	}
	rs.close();
	
	if (cant_registro_rpta > 0)
	{
		if (mostrar_como.equals("tabla"))
		{
			int Largo_Array = 4;
			int i = 0;
			String[][] arrayPreguntas = new String[Largo_Array][7]; //null
			for(i=0;i < Largo_Array; i++)
			{
				arrayPreguntas[i][0] = String.valueOf(i);
				arrayPreguntas[i][1] = "0.0";
				arrayPreguntas[i][2] = "0.0";
				arrayPreguntas[i][3] = "0.0";
				arrayPreguntas[i][4] = "0.0";
				arrayPreguntas[i][5] = "0.0";
				arrayPreguntas[i][6] = "0.0";
			}

			String ID_Pregunta_Str = "";
			encontro_anterior = 0;

			rs = stmt.executeQuery(strSQL_Preguntas);
			if (rs.last()) {
				rowcount = rs.getRow();
				rs.beforeFirst();
				while (rs.next())
				{
					for(i=1;i < Largo_Array; i++)
					{
						ID_Pregunta_Str = rs.getString("id_pregunta");
						if (ID_Pregunta_Str.equals(arrayPreguntas[i][0])){
							if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL") ) {
								arrayPreguntas[i][1] = rs.getString("NPS_7");
								arrayPreguntas[i][2] = rs.getString("NPS_5");
								arrayPreguntas[i][3] = rs.getString("NPS_4");
							}
							else if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR") ) {
								encontro_anterior = 1;
								arrayPreguntas[i][4] = rs.getString("NPS_7");
								arrayPreguntas[i][5] = rs.getString("NPS_5");
								arrayPreguntas[i][6] = rs.getString("NPS_4");
							}
							break;
						}
					}
				}
			}
			rs.close();

			for(i=1;i < Largo_Array; i++)
			{
				if (i == 1){
					html_preg_etiqueta = "Efectivo";
				}
				else if (i == 2){
					html_preg_etiqueta = "F&aacute;cil";
				}
				else if (i == 3){
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
				}
				else {
					porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
					porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
				}

				if ((nps_7_2 + nps_5_2 + nps_4_2) == 0.0) {
					porc_7_2 = 0.0;
					porc_4_2 = 0.0;
				}
				else {
					porc_7_2 = ((nps_7_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
					porc_4_2 = ((nps_4_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
				}

				if (encontro_anterior == 1){
					if (porc_7 > porc_7_2) {
						 etiqueta_porc_7 = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
					}
					else if (porc_7 < porc_7_2) {
						etiqueta_porc_7 = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
					}
					else if (porc_7 == porc_7_2) {
						etiqueta_porc_7 = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
					}

					if (porc_4 > porc_4_2) {
						 etiqueta_porc_4 = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
					}
					else if (porc_4 < porc_4_2) {
						etiqueta_porc_4 = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
					}
					else if (porc_4 == porc_4_2) {
						etiqueta_porc_4 = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
					}


					if ((porc_7 - porc_4) > (porc_7_2 - porc_4_2)) {
						 etiqueta_porc_nps = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
					}
					else if ((porc_7 - porc_4) < (porc_7_2 - porc_4_2)) {
						etiqueta_porc_nps = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
					}
					else if ((porc_7 - porc_4) == (porc_7_2 - porc_4_2)) {
						etiqueta_porc_nps = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
					}

				}


				html_preg = html_preg + "<tr>";
				html_preg = html_preg + "	<td>&nbsp;" + html_preg_etiqueta + "</td>";
				html_preg = html_preg + "	<td>" + EW_FormatNumber(String.valueOf(porc_7),0,0,0,0,locale) + "%" + etiqueta_porc_7 + "</td>";
				html_preg = html_preg + "	<td>" + EW_FormatNumber(String.valueOf(porc_4),0,0,0,0,locale) + "%" + etiqueta_porc_4 + "</td>";
				html_preg = html_preg + "	<td>" + EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale) + "%" + etiqueta_porc_nps + "</td>";
				html_preg = html_preg + "</tr>";
			}
		}

//		out.println("Paso 02 <br>");
//		out.println("strSQL_NPS: " + strSQL_NPS + "<br>");
		rs = stmt.executeQuery(strSQL_NPS);
		encontro_anterior = 0;
		if (rs.last()) {
			rowcount = rs.getRow();
			rs.beforeFirst();
			rs.next();
			
			nps_7 = 0.0;
			nps_5 = 0.0;
			nps_4 = 0.0;
			porc_7 = 0.0;
			porc_4 = 0.0;
			porc_nps = "";
			porc_promotores = "";
			porc_detractores = "";

			if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL") ) {
				nps_7 = rs.getDouble("NPS_7");
				nps_5 = rs.getDouble("NPS_5");
				nps_4 = rs.getDouble("NPS_4");

				if ((nps_7 + nps_5 + nps_4) == 0.0) {
					porc_7 = 0.0;
					porc_4 = 0.0;
					porc_nps = "0.0%";
					porc_promotores = "0.0%";
					porc_detractores = "0.0%";
				}
				else {
					porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
					porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
					porc_nps   = EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale) + "%";
					porc_promotores  = EW_FormatNumber(String.valueOf((nps_7  / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";
					porc_detractores = EW_FormatNumber(String.valueOf((nps_4 / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";
				}

//				porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
//				porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
//				porc_nps   = EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale) + "%";

//				porc_promotores  = EW_FormatNumber(String.valueOf((nps_7  / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";
//				porc_detractores = EW_FormatNumber(String.valueOf((nps_4 / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";

				if (rowcount == 2) {
					rs.next();
				}
			}
			
			if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR") ) {
				encontro_anterior = 1;
				nps_7_2 = rs.getDouble("NPS_7");
				nps_5_2 = rs.getDouble("NPS_5");
				nps_4_2 = rs.getDouble("NPS_4");

				if ((nps_7_2 + nps_5_2 + nps_4_2) == 0.0) {
					porc_7_2 = 0.0;
					porc_4_2 = 0.0;
				}
				else {
					porc_7_2 = ((nps_7_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
					porc_4_2 = ((nps_4_2 / (nps_7_2 + nps_5_2 + nps_4_2)) * 100);
				}

				porc_nps_2 = EW_FormatNumber(String.valueOf(nps_7_2 - nps_4_2),0,0,0,0,locale) + "%";

				if (porc_7 > porc_7_2) {
					 etiqueta_porc_7 = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
				}
				else if (porc_7 < porc_7_2) {
					etiqueta_porc_7 = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
				}
				else if (porc_7 == porc_7_2) {
					etiqueta_porc_7 = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
				}

				if (porc_4 > porc_4_2) {
					 etiqueta_porc_4 = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
				}
				else if (porc_4 < porc_4_2) {
					etiqueta_porc_4 = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
				}
				else if (porc_4 == porc_4_2) {
					etiqueta_porc_4 = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
				}


				if ((porc_7 - porc_4) > (porc_7_2 - porc_4_2)) {
					 etiqueta_porc_nps = "&nbsp;<span title='Mejor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
				}
				else if ((porc_7 - porc_4) < (porc_7_2 - porc_4_2)) {
					etiqueta_porc_nps = "&nbsp;<span title='Peor Porcentaje - Anterior " + EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2),0,0,0,0,locale) + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
				}
				else if ((porc_7 - porc_4) == (porc_7_2 - porc_4_2)) {
					etiqueta_porc_nps = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
				}

			}
			
			
			if (mostrar_como.equals("tabla"))
			{
				html_preg = html_preg + "<tr>";
				html_preg = html_preg + "	<td>&nbsp;<span title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></td>";
				html_preg = html_preg + "	<td>" + porc_promotores + etiqueta_porc_7 + "</td>";
				html_preg = html_preg + "	<td>" + porc_detractores + etiqueta_porc_4 + "</td>";
				html_preg = html_preg + "	<td>" + porc_nps + etiqueta_porc_nps + "</td>";
				html_preg = html_preg + "</tr>";
			}
			else if (mostrar_como.equals("grafico"))
			{

				String actual_superior2 = EW_FormatNumber(String.valueOf(porc_7),0,0,0,0,locale);
				actual_superior2 = actual_superior2.replace(",",".");

				String actual_inferior2 = EW_FormatNumber(String.valueOf(porc_4),0,0,0,0,locale);;
				actual_inferior2 = "-" + actual_inferior2.replace(",",".");

				String actual_diferencia2 = porc_nps;
				actual_diferencia2 = actual_diferencia2.replace(",",".");
				actual_diferencia2 = actual_diferencia2.replace("%","");


				if (encontro_anterior == 0)
				{
					strXML += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0' yAxisMinValue='-100' yAxisMaxValue='100' showLimits='1' >" + "\n";
					strXML += "<categories >" + "\n";
					strXML += "<category label='Inicial' />" + "\n";
					strXML += "</categories>" + "\n";
					strXML += "<dataset seriesName='Prom.' >" + "\n";
					strXML += "<set value='" + actual_superior2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "<dataset seriesName='Detr.'>" + "\n";
					strXML += "<set value='" + actual_inferior2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "<dataset seriesName='Sat. Neta' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
					strXML += "<set value='" + actual_diferencia2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "</chart>" + "\n";

				}
				else
				{
					String anterior_superior2 = EW_FormatNumber(String.valueOf(porc_7_2),0,0,0,0,locale);
					anterior_superior2 = anterior_superior2.replace(",",".");
					String anterior_inferior2 = EW_FormatNumber(String.valueOf(porc_4_2),0,0,0,0,locale);
					anterior_inferior2 = "-" + anterior_inferior2.replace(",",".");
					String anterior_diferencia2 = EW_FormatNumber(String.valueOf(porc_7_2 - porc_4_2),0,0,0,0,locale); // porc_nps_2;
					anterior_diferencia2 = anterior_diferencia2.replace(",",".");
					anterior_diferencia2 = anterior_diferencia2.replace("%","");

					strXML += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0'>" + "\n";
					strXML += "<categories >" + "\n";
					strXML += "<category label='Comparar' />" + "\n";
					strXML += "<category label='Inicial' />" + "\n";
					strXML += "</categories>" + "\n";
					strXML += "<dataset seriesName='Sat.' >" + "\n";
					strXML += "<set value='" + anterior_superior2 + "' />" + "\n";
					strXML += "<set value='" + actual_superior2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "<dataset seriesName='Insat.'>" + "\n";
					strXML += "<set value='" + anterior_inferior2 + "' />" + "\n";
					strXML += "<set value='" + actual_inferior2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "<dataset seriesName='Sat. Neta' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
					strXML += "<set value='" + anterior_diferencia2 + "' />" + "\n";
					strXML += "<set value='" + actual_diferencia2 + "' />" + "\n";
					strXML += "</dataset>" + "\n";
					strXML += "</chart>" + "\n";
				}
				response.setContentType("text/xml");
			}
			else
			{
				out.println ("paso 01");
			}

		}
		rs.close();


		if (mostrar_como.equals("tabla"))
		{
	//		out.println("Paso 03 <br>");
	//		out.println("strSQL_Lealtad: " + strSQL_Lealtad + "<br>");
			
			etiqueta_porc_7 = "";
			etiqueta_porc_4 = "";
			etiqueta_porc_nps = "";

			rs = stmt.executeQuery(strSQL_Lealtad);
			if (rs.last()) {
				rowcount = rs.getRow();
				rs.beforeFirst();
				rs.next();

				cant_leal = 0.0;
				cant_leal_no = 0.0;
				porc_lealtad = "";
				Double porc_lealtad1 = 0.0;

				if (rs.getString("CS_Tipo_Fecha").equals("ACTUAL") ) {
					cant_leal  = rs.getDouble("Leal_SI");
					cant_leal_no = rs.getDouble("Leal_NO");
					porc_lealtad1 = (cant_leal  / (cant_leal + cant_leal_no)) * 100;
					porc_lealtad  = EW_FormatNumber(String.valueOf(porc_lealtad1),0,0,0,0,locale) + "%";

					if (rowcount == 2) {
						rs.next();
					}
				}
				
				if (rs.getString("CS_Tipo_Fecha").equals("ANTERIOR") ) {
					Double cant_leal_2 = 0.0;
					Double cant_leal_no_2 = 0.0;
					String porc_lealtad_2 = "";
					Double porc_lealtad1_2 = 0.0;
					cant_leal_2  = rs.getDouble("Leal_SI");
					cant_leal_no_2 = rs.getDouble("Leal_NO");
					porc_lealtad1_2 = (cant_leal_2  / (cant_leal_2 + cant_leal_no_2)) * 100;
					porc_lealtad_2  = EW_FormatNumber(String.valueOf(porc_lealtad1_2),0,0,0,0,locale) + "%";

					if (porc_lealtad1 > porc_lealtad1_2) {
						 etiqueta_porc_7 = "&nbsp;<span title='Mejor Porcentaje - Anterior " + porc_lealtad_2 + "%" + "' class='icon icon-color icon-arrowthick-n'></span>";
					}
					else if (porc_lealtad1 < porc_lealtad1_2) {
						etiqueta_porc_7 = "&nbsp;<span title='Peor Porcentaje - Anterior " + porc_lealtad_2 + "%" + "' class='icon icon-red icon-arrowthick-s'></span>";
					}
					else if (porc_lealtad1 == porc_lealtad1_2) {
						etiqueta_porc_7 = "&nbsp;<span title='Porcentaje Iguales' class='icon icon-orange icon-arrow-e-w'></span>";
					}

					porc_lealtad = porc_lealtad + etiqueta_porc_7;

				}
			}
			rs.close();
		}

	}
	else
	{
		porc_nps = "-";
		porc_promotores = "-";
		porc_detractores = "-";
		porc_lealtad = "-";

	}

	if (mostrar_como.equals("tabla"))
	{
		if (filtro_sede != "" || filtro_facultad != "" || filtro_campus != "") 
		{
			out.println("N de Encuestas: " + cant_registro_rpta);
			out.println("<hr>");
		}

		
		out.println("<table class='table table-bordered table-striped table-condensed'>");
		out.println("<thead>");
		out.println("  <tr>");
		out.println("	<th>&nbsp;</th>");
		out.println("	<th><img src='img/ico_like2.png' width='24' height='24' border='0'></th>");
		out.println("	<th><img src='img/ico_unlike2.png' width='24' height='24' border='0'></th>");
		out.println("	<th><span title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></th>");
		out.println("  </tr>");
		out.println("</thead>");
		out.println("<tbody>");
		out.println(html_preg);
		out.println("</tbody>");
		out.println("</table>");
		out.println("<hr>");
		out.println("<span class='label3 label-important3'>Recomendaci&oacute;n&nbsp;<img src='img/corazon.gif' width='24' height='24' border='0'>&nbsp;" + porc_lealtad + "</span>");
	}

	// Close recordset and connection
	rs = null;
	stmt.close();
	stmt = null;
	conn.close();
	conn = null;
}catch(SQLException ex){
	out.println(ex.toString());
}

if (mostrar_como.equals("grafico"))
{
	out.println(strXML);
}
%>