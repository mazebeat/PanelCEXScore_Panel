<%@ page session="true" buffer="16kb" import="java.sql.*,java.util.*,java.io.*,java.text.*"%>
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
String filtro_sede = request.getParameter("sede");
String filtro_facultad = request.getParameter("facultad");
String filtro_campus = request.getParameter("campus");
String filtros = "";

if (filtro_sede != "") 
{
//	filtros = filtros + " bdd_umayor.sede = '" + filtro_sede + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE( bdd_umayor.sede, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_sede + "' AND ";
}

if (filtro_facultad != "")
{
//	filtros = filtros + " bdd_umayor.facultad = '" + filtro_facultad + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE( bdd_umayor.facultad, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_facultad + "' AND ";
}

if (filtro_campus != "")
{
//	filtros = filtros + " bdd_umayor.campus = '" + filtro_campus + "' AND ";
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE( bdd_umayor.campus, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_campus + "' AND ";
}

String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (SELECT DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') as Periodo_Rpta, clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " AND clientes_respuesta.id_estado = 15 GROUP BY Periodo_Rpta, id_cliente ) as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";

//out.println("strSQL_Ultima_rpta_x_usuario: " + strSQL_Ultima_rpta_x_usuario + "<br>");


String strSQL_NPS = "SELECT Periodo_Rpta, Sum(case when Promedio >= 6 then 1 else 0 END) as NPS_7, Sum(case when Promedio < 6 and Promedio > 4 then 1 else 0 END) as NPS_5, Sum(case when Promedio <= 4 then 1 else 0 END) as NPS_4  FROM (SELECT respuestas.Periodo_Rpta, respuestas.id_cliente, ROUND(AVG(respuestas_detalle.valor1 ),1) as Promedio FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY respuestas.Periodo_Rpta, respuestas.id_cliente ) as Datos GROUP BY Periodo_Rpta ORDER BY Periodo_Rpta ASC";

//out.println("strSQL_NPS: " + strSQL_NPS + "<br>");

String strXML = "";
String str_category = "";
String str_promotores = "";
String str_detractores = "";
String str_nps = "";

Double nps_7 = 0.0;
Double nps_5 = 0.0;
Double nps_4 = 0.0;
Double porc_7 = 0.0;
Double porc_4 = 0.0;

try{
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	
	rs = stmt.executeQuery(strSQL_Cualificacion);
	int rowcount = 0;
	if (rs.last()) {
		rowcount = rs.getRow();
		rs.beforeFirst();
		while (rs.next())
		{
			str_category += "<category label='" + rs.getString("Periodo_Rpta") + "\n";

			nps_7 = rs.getDouble("NPS_7");
			nps_5 = rs.getDouble("NPS_5");
			nps_4 = rs.getDouble("NPS_4");

			if ((nps_7 + nps_5 + nps_4) == 0.0) {
				porc_7 = 0.0;
				porc_4 = 0.0;
				porc_nps = "0.0";
			}
			else {
				porc_7 = ((nps_7 / (nps_7 + nps_5 + nps_4)) * 100);
				porc_4 = ((nps_4 / (nps_7 + nps_5 + nps_4)) * 100);
				porc_nps   = EW_FormatNumber(String.valueOf(porc_7 - porc_4),0,0,0,0,locale);
			}
			str_nps += "<set value='" + porc_nps + "' />" + "\n";
		}
	}
	
	rs.close();

	strXML += "<chart showValues='0' numberSuffix='%' bgColor='ffffff' borderColor ='ffffff'  labelDisplay='Rotate' slantLabels='1'>" + "\n";
	strXML += "<categories>" + "\n";
	strXML += str_category;
	strXML += "</categories>" + "\n";
	strXML += "<dataset seriesName='NPS'>" + "\n";
	strXML += str_nps;
	strXML += "</dataset>" + "\n";
	strXML += "<styles>" + "\n";
	strXML += "<definition>" + "\n";
	strXML += "<style name='CanvasAnim' type='animation' param='_xScale' start='0' duration='1' />" + "\n";
	strXML += "</definition>" + "\n";
	strXML += "<application>" + "\n";
	strXML += "<apply toObject='Canvas' styles='CanvasAnim' />" + "\n";
	strXML += "</application>" + "\n";
	strXML += "</styles>" + "\n";
	strXML += "</chart>" + "\n";

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