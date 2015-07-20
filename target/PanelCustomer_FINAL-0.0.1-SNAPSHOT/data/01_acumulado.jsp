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

String filtro_anio = request.getParameter("anio");
String filtro_anio2 = request.getParameter("anio2");
String filtro_sede = request.getParameter("sede");
String filtro_facultad = request.getParameter("facultad");
String filtro_campus = request.getParameter("campus");
String filtros = "";

if (filtro_sede != "") 
{
//	filtros = filtros + " bdd_umayor.sede = '" + filtro_sede + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.sede, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_sede + "' AND ";
}

if (filtro_facultad != "")
{
//	filtros = filtros + " bdd_umayor.facultad = '" + filtro_facultad + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.facultad, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_facultad + "' AND ";
}

if (filtro_campus != "")
{
//	filtros = filtros + " bdd_umayor.campus = '" + filtro_campus + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.campus, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_campus + "' AND ";
}

String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (SELECT clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " AND ((YEAR(clientes_respuesta.ultima_respuesta) = " + filtro_anio + " and YEAR(clientes.created_at) = " + filtro_anio + ") or (YEAR(clientes_respuesta.ultima_respuesta) = " + filtro_anio2 + " and YEAR(clientes.created_at) = " + filtro_anio2 + ")) AND clientes_respuesta.id_estado = 15 GROUP BY id_cliente ) as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";

//out.println("strSQL_Ultima_rpta_x_usuario: " + strSQL_Ultima_rpta_x_usuario + "<br>");

String strSQL_Cualificacion = "SELECT 'Total Base' as Tipo, COUNT(clientes.id_cliente) as Casos FROM bdd_umayor INNER JOIN clientes ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + "  ((YEAR(clientes.created_at) =  " + filtro_anio + ") or (YEAR(clientes.created_at) =  " + filtro_anio2 + ")) ";
strSQL_Cualificacion = strSQL_Cualificacion + " UNION ALL ";
strSQL_Cualificacion = strSQL_Cualificacion + " SELECT 'Con Rpta' as Tipo, COUNT(DISTINCT id_cliente) as Casos FROM ( SELECT clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE  " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " AND ((YEAR(clientes_respuesta.ultima_respuesta) = " + filtro_anio + " and YEAR(clientes.created_at) = " + filtro_anio + ") or (YEAR(clientes_respuesta.ultima_respuesta) = " + filtro_anio2 + " and YEAR(clientes.created_at) = " + filtro_anio2 + ")) AND clientes_respuesta.id_estado = 15 GROUP BY id_cliente ) AS Datos";

//out.println("strSQL_Cualificacion: " + strSQL_Cualificacion + "<br>");

String strSQL_NPS = "SELECT Sum(case when Promedio >= 6 then 1 else 0 END) as NPS_7, Sum(case when Promedio < 6 and Promedio > 4 then 1 else 0 END) as NPS_5, Sum(case when Promedio <= 4 then 1 else 0 END) as NPS_4  FROM (SELECT respuestas.id_cliente, ROUND(AVG(respuestas_detalle.valor1 ),1) as Promedio FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY respuestas.id_cliente ) as Datos";

//out.println("strSQL_NPS: " + strSQL_NPS + "<br>");

//String strSQL_promotor_detractor = "SELECT Sum(case when Promedio >= 4 then 1 else 0 END) as Sobre_4, Sum(case when Promedio < 4 then 1 else 0 END) as Bajo_4 FROM (SELECT respuestas.id_cliente, ROUND(AVG(respuestas_detalle.valor1 ),1) as Promedio FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE " + filtros + " clientes_respuesta.id_estado = 15 AND YEAR(clientes_respuesta.ultima_respuesta) = " + filtro_anio + " AND YEAR(clientes.created_at) = " + filtro_anio + " AND (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY respuestas.id_cliente ) as Datos";

//out.println("strSQL_promotor_detractor: " + strSQL_promotor_detractor + "<br>");

String strSQL_Lealtad = "SELECT Sum(case when valor1 = 1 then 1 else 0 END) as Leal_NO, Sum(case when valor1 = 2 then 1 else 0 END) as Leal_SI FROM (SELECT respuestas.id_cliente, respuestas_detalle.valor1 FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 4)) as Datos";

//out.println("strSQL_Lealtad: " + strSQL_Lealtad + "<br>");

String strSQL_Preguntas = "SELECT id_pregunta, SUM(NPS_7) as NPS_7, SUM(NPS_5) as NPS_5, SUM(NPS_4) as NPS_4 FROM (SELECT respuestas.id_pregunta, case when respuestas_detalle.valor1 >= 6 then 1 else 0 end AS NPS_7, case when respuestas_detalle.valor1 < 6 and respuestas_detalle.valor1 > 4 then 1 else 0 end AS NPS_5, case when respuestas_detalle.valor1 <= 4  then 1 else 0 end AS NPS_4 FROM respuestas INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta INNER JOIN clientes_respuesta ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 OR respuestas.id_pregunta = 2 OR respuestas.id_pregunta = 3) GROUP BY respuestas.id_respuesta ) AS Datos_Tmp GROUP BY id_pregunta ORDER BY id_pregunta";

//out.println("strSQL_Preguntas: " + strSQL_Preguntas + "<br>");


Double cant_registro_total = 0.0;
Double cant_registro_rpta = 0.0;

Double nps_7 = 0.0;
Double nps_5 = 0.0;
Double nps_4 = 0.0;
Double porc_7 = 0.0;
Double porc_4 = 0.0;

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

try{
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	
	rs = stmt.executeQuery(strSQL_Cualificacion);
	int rowcount = 0;
	if (rs.last()) {
		rowcount = rs.getRow();
		rs.beforeFirst();
		rs.next();
			cant_registro_total = rs.getDouble("Casos");
		rs.next();
			cant_registro_rpta = rs.getDouble("Casos");

		porc_cualificacion = EW_FormatNumber(String.valueOf((cant_registro_rpta / cant_registro_total) * 100),0,0,0,0,locale) + "% <br> " + EW_FormatNumber(String.valueOf(cant_registro_rpta),0,0,0,0,locale) + " de " + EW_FormatNumber(String.valueOf(cant_registro_total),0,0,0,0,locale);
	}
	
	rs.close();
	
	if (cant_registro_rpta > 0.0) {
//		rs = stmt.executeQuery(strSQL_promotor_detractor);
//		if (rs.last()) {
//			rowcount = rs.getRow();
//			rs.beforeFirst();
//			rs.next();
//				cant_promotores  = rs.getDouble("Sobre_4");
//				cant_detractores = rs.getDouble("Bajo_4");
//				porc_promotores  = EW_FormatNumber(String.valueOf((cant_promotores  / (cant_promotores + cant_detractores)) * 100),0,0,0,0,locale) + "%";
//				porc_detractores = EW_FormatNumber(String.valueOf((cant_detractores / (cant_promotores + cant_detractores)) * 100),0,0,0,0,locale) + "%";
//		}
//		rs.close();

		rs = stmt.executeQuery(strSQL_NPS);
		if (rs.last()) {
			rowcount = rs.getRow();
			rs.beforeFirst();
			rs.next();
				nps_7 = rs.getDouble("NPS_7");
				nps_5 = rs.getDouble("NPS_5");
				nps_4 = rs.getDouble("NPS_4");
				porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
				porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
				porc_nps   = EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale) + "%";

//				cant_promotores  = nps_7;
//				cant_detractores = nps_4;
				porc_promotores  = EW_FormatNumber(String.valueOf((nps_7  / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";
				porc_detractores = EW_FormatNumber(String.valueOf((nps_4 / (nps_7 + nps_5 + nps_4)) * 100),0,0,0,0,locale) + "%";
		}
		rs.close();

		rs = stmt.executeQuery(strSQL_Lealtad);
		if (rs.last()) {
			rowcount = rs.getRow();
			rs.beforeFirst();
			rs.next();
				cant_leal  = rs.getDouble("Leal_SI");
				cant_leal_no = rs.getDouble("Leal_NO");
				porc_lealtad  = EW_FormatNumber(String.valueOf((cant_leal  / (cant_leal + cant_leal_no)) * 100),0,0,0,0,locale) + "%";
		}
		rs.close();

		rs = stmt.executeQuery(strSQL_Preguntas);
		if (rs.last()) {
			rowcount = rs.getRow();
			rs.beforeFirst();
			while (rs.next()) {
				nps_7 = rs.getDouble("NPS_7");
				nps_5 = rs.getDouble("NPS_5");
				nps_4 = rs.getDouble("NPS_4");
				porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
				porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);

				if (rs.getInt("id_pregunta") == 1){
					html_preg_etiqueta = "Efectivo";
				}
				else if (rs.getInt("id_pregunta") == 2){
					html_preg_etiqueta = "F&aacute;cil";
				}
				else if (rs.getInt("id_pregunta") == 3){
					html_preg_etiqueta = "Agradable";
				}

				html_preg = html_preg + "<tr>";
				html_preg = html_preg + "	<td>&nbsp;" + html_preg_etiqueta + "</td>";
				html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(porc_7),0,0,0,0,locale) + "%</td>";
				html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(porc_4),0,0,0,0,locale) + "%</td>";
				html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale) + "%</td>";
				html_preg = html_preg + "</tr>";
			}
		}
		rs.close();

	}
	else
	{
		porc_nps = "-";
		porc_promotores = "-";
		porc_detractores = "-";
		porc_lealtad = "-";

	}

	

	out.println("<table class='table table-bordered table-striped table-condensed'>");
	out.println("<thead>");
	out.println("  <tr>");
	out.println("	<th colspan='3'>Tasa de Cualificaci&oacute;n: " + porc_cualificacion +"</th>");
	out.println("  </tr>");
	out.println("</thead>");
	out.println("<tbody>");
	out.println("  <tr>");
	out.println("	<td><img src='img/ico_like2.png' width='24' height='24' border='0'></td>");
	out.println("	<td>&nbsp;Promotores:</td>");
	out.println("	<td>&nbsp;" + porc_promotores + "</td>");
	out.println("  </tr>");
	out.println("  <tr>");
	out.println("	<td><img src='img/ico_unlike2.png' width='24' height='24' border='0'></td>");
	out.println("	<td>&nbsp;Detractores:</td>");
	out.println("	<td>&nbsp;" + porc_detractores + "</td>");
	out.println("  </tr>");
	out.println("</tbody>");
	out.println("</table>");
	out.println("<table class='table table-bordered table-striped table-condensed'>");
	out.println("<tbody>");
	out.println("  <tr>");
	out.println("	<td>&nbsp;<span title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span>:</td>");
	out.println("	<td>&nbsp;" + porc_nps + "</td>");
	out.println("  </tr>");
	out.println("  <tr>");
	out.println("	<td>&nbsp;Recomendaci&oacute;n:</td>");
	out.println("	<td>&nbsp;" + porc_lealtad + "&nbsp;<img src='img/corazon.gif' width='24' height='24' border='0'></td>");
	out.println("  </tr>");
	out.println("</tbody>");
	out.println("</table>");
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

	// Close recordset and connection
	rs = null;
	stmt.close();
	stmt = null;
	conn.close();
	conn = null;
}catch(SQLException ex){
	out.println(ex.toString());
}

%>