<%@ page session="true" buffer="500kb" import="java.sql.*,java.util.*,java.io.*,java.text.*"%>
<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
HOLA JIL
<%

// contentType="text/html; charset=UTF-8"
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
if (session.getAttribute("Panel_" + ProyectoID00 + "_status_UserLevel") != null) {
	int ewCurIdx = ((Integer) session.getAttribute("Panel_" + ProyectoID00 + "_status_UserLevel")).intValue();
	if (ewCurIdx == -1) { // system administrator
		ewCurSec = 31;
	} else if (ewCurIdx > 0 && ewCurIdx <= rowcountSeguridad) { 
//		ewCurSec = ew_SecTable[ewCurIdx-1];
		ewCurSec = (Integer) ew_SecTableSeguridad.get(ewCurIdx-1);
	}
}

if ((ewCurSec & ewAllowList) != ewAllowList) {
	response.sendRedirect("index.jsp");
	response.flushBuffer(); 
	return;
}

%>

<%@ include file="../inc/jspmkrfn.jsp" %>

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

String str_Exportar = "SELECT " + strSQL_Cab + "bdd_umayor.rut, bdd_umayor.apellido_paterno, bdd_umayor.apellido_materno, bdd_umayor.nombres, bdd_umayor.sexo, bdd_umayor.facultad, bdd_umayor.escuela, bdd_umayor.carrera, bdd_umayor.jornada, bdd_umayor.sede, bdd_umayor.campus, bdd_umayor.`año_ingreso_1año_carrera`, bdd_umayor.`año_ingreso_carrera`, bdd_umayor.`año_egreso_plan_regular`, canales.descripcion AS Canal_Ingreso, DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') AS Periodo, clientes_respuesta.ultima_respuesta AS Fecha_Encuesta, estados.descripcion AS Estado_Encuesta, clientes_respuesta.id_cliente_respuesta, respuestas.id_pregunta, preguntas.descripcion_1, respuestas_detalle.valor1, case WHEN ISNULL(respuestas_detalle.valor2) then '---' else respuestas_detalle.valor2 end as valor2 FROM respuestas_detalle INNER JOIN respuestas ON respuestas.id_respuesta = respuestas_detalle.id_respuesta INNER JOIN clientes_respuesta ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno INNER JOIN canales ON respuestas.id_canal = canales.id_canal INNER JOIN estados ON clientes_respuesta.id_estado = estados.id_estado INNER JOIN preguntas ON respuestas.id_pregunta = preguntas.numero_pregunta WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " ORDER BY clientes_respuesta.id_cliente_respuesta, respuestas.id_pregunta";

//and estados.id_estado = 15 

//out.println(str_Exportar + "<br>");
//out.flush(); // Send out whatever hasn't been sent out yet.
//out.close(); // Close the stream. Future calls will fail.

String html = "";
try{
	
	if ((ewCurSec & ewAllowAdd) == ewAllowAdd) {

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=UMayor_Rpta_" + currTime + ".xls");


		int rowcount = 0;
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = null;
		rs = stmt.executeQuery(str_Exportar);
		if (rs.last()) {
			rowcount = rs.getRow();	

			out.println("<table border='1'>");
				ResultSetMetaData metaData = rs.getMetaData();
				
				out.println("<tr>");
				int numberOfColumns =  metaData.getColumnCount();
				int column = 0;
				for(column = 0; column < 19; column++) {
					out.println("<td>" + metaData.getColumnLabel(column+1) + "</td>");
				}
				out.println("<td>Pregunta 01 Nota</td>");
				out.println("<td>Pregunta 01 Motivo</td>");
				out.println("<td>Pregunta 02 Nota</td>");
				out.println("<td>Pregunta 02 Motivo</td>");
				out.println("<td>Pregunta 03 Nota</td>");
				out.println("<td>Pregunta 03 Motivo</td>");
				out.println("<td>Pregunta 04 Recomienda</td>");
				out.println("<td>Clasificacion Alumno Nota</td>");
				out.println("<td>Clasificacion Alumno Estado</td>");
				out.println("</tr>");

				String ultimo_id = "";
				Double clasifica_alumno = 0.0;
				rs.beforeFirst();
				while (rs.next()) 
				{
					if (!rs.getObject(20).toString().equals(ultimo_id))
					{
						if (!ultimo_id.equals("")) out.println("</tr>");

						ultimo_id = rs.getObject(20).toString();
//						out.println("KK " + rs.getObject(20).toString());

						out.println("<tr>");
						clasifica_alumno = 0.0;

						for(column = 0; column < 19; column++)
						{
							if (rs.getObject(column + 1) == null)
							{
								out.print("<td></td>");
							}
							else {
								out.print("<td>" + rs.getObject(column + 1).toString() + "</td>");
							}
						}
							out.print("<td>" + rs.getObject(23).toString() + "</td>");
							out.print("<td>" + rs.getObject(24).toString() + "</td>");
							clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getObject(23).toString());
					}
					else
					{
						if (rs.getObject(21).toString().equals("1") || rs.getObject(21).toString().equals("2") || rs.getObject(21).toString().equals("3")) {
							out.print("<td>" + rs.getObject(23).toString() + "</td>");
							out.print("<td>" + rs.getObject(24).toString() + "</td>");
							clasifica_alumno = clasifica_alumno + Double.valueOf(rs.getObject(23).toString());
						}
						else if (rs.getObject(21).toString().equals("4")) {
							if (rs.getObject(23).toString().equals("1")) out.print("<td>NO</td>");
							else if (rs.getObject(23).toString().equals("2")) out.print("<td>SI</td>");
							//out.println("<td></td>");

							clasifica_alumno = clasifica_alumno / 3;
							out.print("<td>" + EW_FormatNumber(String.valueOf(clasifica_alumno),1,0,0,0,locale) + "</td>");
							
							if (clasifica_alumno >=	6.0) {	out.println("<td>Promotor</td>"); }
							else if (clasifica_alumno <= 4.0) {	out.println("<td>Detractor</td>"); }
							else {	out.print("<td>Neutro</td>"); }
						}
					}
					out.flush();
//					out.close();
				}
			out.println("</tr>");
			out.println("</table>");
			
		}
		else {
			out.println("NO EXISTEN RESPUESTAS");
		}
		rs.close();
		// Close recordset and connection
		rs = null;
		stmt.close();
		stmt = null;
	}
	else {
		out.println("NO TIENE PERMISO PARA EXPORTAR DATOS");
	}
	conn.close();
	conn = null;
}catch(SQLException ex){
	out.println(ex.toString());
}
%>