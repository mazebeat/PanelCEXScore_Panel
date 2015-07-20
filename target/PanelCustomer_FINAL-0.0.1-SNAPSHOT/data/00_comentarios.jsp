<%@ page session="true" buffer="16kb" import="java.sql.*,java.util.*,java.io.*,java.text.*, cl.intelidata.otros.jspmkrfn"%>
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
//response.sendRedirect("");
//response.flushBuffer(); 
return; 
}%>

<%@ include file="../inc/db.jsp" %>

<%
String currTime = request.getParameter("currTime");
String periodo_0 = request.getParameter("periodo_0");
String periodo_1 = request.getParameter("periodo_1");
String periodo_2 = request.getParameter("periodo_2");
String periodo_3 = request.getParameter("periodo_3");

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

%>

<%

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

String str_Exportar = "SELECT " + strSQL_Cab + "bdd_umayor.rut, bdd_umayor.apellido_paterno, bdd_umayor.apellido_materno, bdd_umayor.nombres, bdd_umayor.sexo, bdd_umayor.facultad, bdd_umayor.escuela, bdd_umayor.carrera, bdd_umayor.jornada, bdd_umayor.sede, bdd_umayor.campus, bdd_umayor.`año_ingreso_1año_carrera`, bdd_umayor.`año_ingreso_carrera`, bdd_umayor.`año_egreso_plan_regular`, canales.descripcion AS Canal_Ingreso, DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') AS Periodo, clientes_respuesta.ultima_respuesta AS Fecha_Encuesta, estados.descripcion AS Estado_Encuesta, clientes_respuesta.id_cliente_respuesta, respuestas.id_pregunta, preguntas.descripcion_1, respuestas_detalle.valor1, respuestas_detalle.valor2 FROM respuestas_detalle INNER JOIN respuestas ON respuestas.id_respuesta = respuestas_detalle.id_respuesta INNER JOIN clientes_respuesta ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno INNER JOIN canales ON respuestas.id_canal = canales.id_canal INNER JOIN estados ON clientes_respuesta.id_estado = estados.id_estado INNER JOIN preguntas ON respuestas.id_pregunta = preguntas.numero_pregunta WHERE " + filtros + " clientes_respuesta.id_estado = 15 AND (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) AND clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " ORDER BY clientes_respuesta.id_cliente_respuesta DESC, respuestas.id_pregunta ASC LIMIT 0, 60";

//out.println(strSQL + "<br>");

String html = "";
try{
	String Comentario = "";
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	rs = stmt.executeQuery(str_Exportar);
	int rowcount = 0;
	if (rs.last()) {
		out.println("<table width='100%' border='0'>");
		rowcount = rs.getRow();
//		out.println(rowcount + "<br>");
		String ultimo_id = "";
		rs.beforeFirst();
		while (rs.next())
		{
			Comentario = rs.getString("valor2");

			if (!rs.getString(20).toString().equals(ultimo_id))
			{
				if (!ultimo_id.equals(""))
				{
					html += "</ul></td>";
					html += "</tr>";
					html += "<tr><td colspan='2'><hr></td></tr>";
					out.println(html);
				}

				ultimo_id = rs.getObject(20).toString();

				html = "<tr valign='top'>";
				html += "<td><ul class='dashboard-list'><li><span class='dashboard-avatar icon32 icon-red icon-user'></span><strong>Sede:</strong>&nbsp;" + rs.getString("Sede") + "<br><strong>Nombre:</strong>&nbsp;" + rs.getString("nombres") + " " + rs.getString("apellido_paterno") + " " + rs.getString("apellido_materno") + "<br><strong>Fecha:</strong>&nbsp;" + EW_FormatDateTime(rs.getTimestamp("Fecha_Encuesta"),8,locale) + "<br><strong>Facultad:</strong>&nbsp;" + rs.getString("facultad")  + "<br><strong>Campus:</strong>&nbsp;" + rs.getString("campus")+ "</li></ul></td>";
				html += "<td><ul class='dashboard-list'><li><strong>Pregunta " + rs.getString("id_pregunta") + "</strong>&nbsp;<br><strong>Respuesta:</strong>&nbsp;" + Comentario + "</li>";
			}
			else{
				html += "<li><br><strong>Pregunta " + rs.getString("id_pregunta") + "</strong>&nbsp;<br><strong>Respuesta:</strong>&nbsp;" + Comentario + "</li>";
			}
		}
		html += "</ul></td>";
		html += "</tr>";
		html += "<tr><td colspan='2'><hr></td></tr>";
		html += "</table>";
		out.println(html);
	}
	else {
		out.println("NO EXISTEN COMENTARIOS<br>");
	}
	// Close recordset and connection
	rs.close();
	rs = null;
	stmt.close();
	stmt = null;
	conn.close();
	conn = null;
}catch(SQLException ex){
	out.println(ex.toString());
}
%>