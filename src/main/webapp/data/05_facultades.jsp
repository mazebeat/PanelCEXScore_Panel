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
Integer display_desde = Integer.parseInt(request.getParameter("display_desde"));
Integer display_hasta = Integer.parseInt(request.getParameter("display_hasta"));


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

String strSQL_Facultades = "SELECT bdd_umayor.facultad FROM bdd_umayor WHERE " + filtros + " 1=1 GROUP BY bdd_umayor.facultad order by bdd_umayor.facultad";


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

String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (" + str_SQL_Base + ") as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";

//out.println("strSQL_Ultima_rpta_x_usuario: " + strSQL_Ultima_rpta_x_usuario + "<br>");

String strSQL_Facultades_N = "SELECT bdd_umayor.facultad, Ultima_rpta_x_usuario.CS_Tipo_Fecha, COUNT(respuestas.id_cliente) as casos FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY bdd_umayor.facultad, Ultima_rpta_x_usuario.CS_Tipo_Fecha ORDER BY bdd_umayor.facultad, Ultima_rpta_x_usuario.CS_Tipo_Fecha";


//out.println("strSQL_Facultades_N: " + strSQL_Facultades_N + "<br>");
//out.flush(); // Send out whatever hasn't been sent out yet.
//out.close(); // Close the stream. Future calls will fail.

int i = 0;

try{
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;

	rs = stmt.executeQuery(strSQL_Facultades);
	int rowcount = 0;
	if (rs.last()) {
		rowcount = rs.getRow();
		
		String[][] arrayFacultades = new String[rowcount][3]; //null
		for(i=0;i < rowcount; i++)
		{
			arrayFacultades[i][0] = "";
			arrayFacultades[i][1] = "0";
			arrayFacultades[i][2] = "0";
		}

		i = 0;
		rs.beforeFirst();
		while (rs.next()) {
			arrayFacultades[i][0] = rs.getString("facultad");
			i++;
		}
		rs.close();


		String tmp_facultad = "";
		String tmp_facultad_vacio = "";
		String str_Actual = "ACTUAL";
		String str_Anterior = "ANTERIOR";
		int encontro = 0;
		rs = stmt.executeQuery(strSQL_Facultades_N);
		if (rs.last()) {
			rs.beforeFirst();
			while (rs.next()) {
				for (i=0; i<rowcount; i++)
				{
					tmp_facultad = rs.getString("facultad");
					if (tmp_facultad.equals(arrayFacultades[i][0])){
						if (str_Actual.equals(rs.getString("CS_Tipo_Fecha"))){
							arrayFacultades[i][1] = rs.getString("casos");
						}
						else if (str_Anterior.equals(rs.getString("CS_Tipo_Fecha"))){
							arrayFacultades[i][2] = rs.getString("casos");
						}
						break;
					}
				}
			}
		}


		
//		for(i=0;i < rowcount; i++)
//		{
//			out.println(arrayFacultades[i][0] + "&nbsp;-&nbsp;");
//			out.println(arrayFacultades[i][1] + "&nbsp;-&nbsp;");
//			out.println(arrayFacultades[i][2] + "&nbsp;<br>");
//		}
//
//		out.println("<hr><hr>");

		if (display_hasta > rowcount) display_hasta = rowcount - 1;

		for(i=display_desde;i <= display_hasta; i++)
		{
			if (arrayFacultades[i][1] == "0") {
//				out.println("<span class='label label-important'>" + arrayFacultades[i][0] + "</span><br>");
				out.println("<span class='label label-important' onclick=\"javascript:FiltrarDatos2(\'" + periodo_0 + "\',\'" + periodo_1 + "\',\'" + periodo_2 + "\',\'" + periodo_3 + "\',\'" + Limpia_String(filtro_sede) + "\',\'" + Limpia_String(arrayFacultades[i][0]) + "\',\'" + filtro_sede.replace("Division ","") + " - " + RecortaNombre(arrayFacultades[i][0]) + "\');\">" + RecortaNombre(arrayFacultades[i][0]) + "</span><br>");
			}
			else {
				out.println("<span class='label label-success2' onclick=\"javascript:FiltrarDatos2(\'" + periodo_0 + "\',\'" + periodo_1 + "\',\'" + periodo_2 + "\',\'" + periodo_3 + "\',\'" + Limpia_String(filtro_sede) + "\',\'" + Limpia_String(arrayFacultades[i][0]) + "\',\'" + filtro_sede.replace("Division ","") + " - " + RecortaNombre(arrayFacultades[i][0]) + "\');\">" + RecortaNombre(arrayFacultades[i][0]) + "</span><br>");
			}
		}
	}
	rs.close();

	

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