<%@ page session="true" buffer="16kb" import="java.sql.*,java.util.*,java.io.*,java.text.*"%>
<%@ page contentType="text/html; charset=UTF-8" %>
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
return; 
}%>

<%@ include file="../inc/db.jsp" %>
<%@ include file="../inc/jspmkrfn.jsp" %>

<%

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
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.sede, '�', 'a'),'�','e'),'�','i'),'�','o'),'�','u'),'�','n') = '" + filtro_sede + "' AND ";
}

if (filtro_facultad != "")
{
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.facultad, '�', 'a'),'�','e'),'�','i'),'�','o'),'�','u'),'�','n') = '" + filtro_facultad + "' AND ";
}

if (filtro_campus != "")
{
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(bdd_umayor.campus, '�', 'a'),'�','e'),'�','i'),'�','o'),'�','u'),'�','n') = '" + filtro_campus + "' AND ";
}

String strSQL_case01 = "";
String strSQL_case011 = "";
String strSQL_case02 = "";
String strSQL_case022 = "";

if (periodo_0 != "" && periodo_1 != "")
{
	if (periodo_0.equals(periodo_1))
	{
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

filtros = filtros + " ( " + strSQL_case011;

if (strSQL_case02 != "") filtros = filtros + " or " + strSQL_case022;

filtros = filtros + ") AND ";

String str_SQL_Base = "SELECT " + strSQL_Cab + " DATE_FORMAT(clientes_respuesta.ultima_respuesta,'%Y-%m') as CS_Rpta_Periodo, clientes_respuesta.id_cliente, Max(clientes_respuesta.id_cliente_respuesta) AS id_ultima_rpta FROM clientes_respuesta INNER JOIN clientes ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN bdd_umayor ON bdd_umayor.id_alumno = clientes.id_alumno WHERE " + filtros + " clientes_respuesta.id_cliente_respuesta <= " + session.getAttribute("Panel_" + ProyectoID00 + "_Max_Reg_Rpta") + " AND clientes_respuesta.id_estado = 15 GROUP BY CS_Tipo_Fecha, CS_Rpta_Periodo, id_cliente";

//out.println("str_SQL_Base: " + str_SQL_Base + "<br>");

String strSQL_Ultima_rpta_x_usuario = "INNER JOIN (" + str_SQL_Base + ") as Ultima_rpta_x_usuario ON clientes_respuesta.id_cliente = Ultima_rpta_x_usuario.id_cliente AND clientes_respuesta.id_cliente_respuesta = Ultima_rpta_x_usuario.id_ultima_rpta";

//out.println("strSQL_Ultima_rpta_x_usuario: " + strSQL_Ultima_rpta_x_usuario + "<br>");

String strSQL_NPS = "SELECT CS_Tipo_Fecha, CS_Rpta_Periodo, Sum(case when Promedio >= 6 then 1 else 0 END) as NPS_7, Sum(case when Promedio < 6 and Promedio > 4 then 1 else 0 END) as NPS_5, Sum(case when Promedio <= 4 then 1 else 0 END) as NPS_4  FROM (SELECT Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_cliente, ROUND(AVG(respuestas_detalle.valor1 ),1) as Promedio FROM bdd_umayor INNER JOIN clientes ON clientes.id_alumno = bdd_umayor.id_alumno INNER JOIN clientes_respuesta ON clientes.id_cliente = clientes_respuesta.id_cliente INNER JOIN respuestas ON clientes_respuesta.id_cliente_respuesta = respuestas.id_cliente_respuesta INNER JOIN respuestas_detalle ON respuestas.id_respuesta = respuestas_detalle.id_respuesta " + strSQL_Ultima_rpta_x_usuario + " WHERE (respuestas.id_pregunta = 1 or respuestas.id_pregunta = 2 or respuestas.id_pregunta = 3) GROUP BY Ultima_rpta_x_usuario.CS_Tipo_Fecha, Ultima_rpta_x_usuario.CS_Rpta_Periodo, respuestas.id_cliente ) as Datos GROUP BY CS_Tipo_Fecha ORDER BY CS_Tipo_Fecha, CS_Rpta_Periodo";

//out.println(strSQL_NPS);
//out.flush(); // Send out whatever hasn't been sent out yet.
//out.close(); // Close the stream. Future calls will fail.


String strXML="";
try{
	int mes = 0;
	String actual = "ACTUAL";
	String anterior = "ANTERIOR";
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	rs = stmt.executeQuery(strSQL_NPS);
	int rowcount = 0;
	if (rs.last()) {
		rowcount = rs.getRow();
		
		strXML += "<chart showValues='0' numberSuffix='%' bgColor='ffffff' borderColor ='ffffff'  labelDisplay='Rotate' slantLabels='1'>" + "\n";
		strXML += "<categories>" + "\n";
		rs.beforeFirst();
		while (rs.next()) {
			if (actual.equals(rs.getString("cs_tipo_Fecha"))) {
				mes += 1;
//				strXML += "<category label='Mes " + mes + "' />" + "\n";
				strXML += "<category label='" + rs.getString("CS_Rpta_Periodo") + "' />" + "\n";
			}
		}
		if (mes == 0) {
			rs.beforeFirst();
			while (rs.next()) {
				if (anterior.equals(rs.getString("cs_tipo_Fecha"))) {
					mes += 1;
//					strXML += "<category label='Mes " + mes + "' />" + "\n";
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
			if (actual.equals(rs.getString("cs_tipo_Fecha"))) {
				neta2 = ((rs.getDouble("NPS_7") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100) - ((rs.getDouble("NPS_4") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100);
				neta3 = EW_FormatNumber(String.valueOf(neta2),0,0,0,0,locale);
				neta3 = neta3.replace(",",".");
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
				if (anterior.equals(rs.getString("cs_tipo_Fecha"))) {
					neta2 = ((rs.getDouble("NPS_7") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100) - ((rs.getDouble("NPS_4") / (rs.getDouble("NPS_7") + rs.getDouble("NPS_5") + rs.getDouble("NPS_4"))) * 100);
					neta3 = EW_FormatNumber(String.valueOf(neta2),0,0,0,0,locale);
					neta3 = neta3.replace(",",".");
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

	}
	else {
		strXML += "<chart showValues='0' numberSuffix='%' bgColor='ffffff' borderColor ='ffffff' labelDisplay='Rotate' slantLabels='1'>" + "\n";
		strXML += "<categories>" + "\n";
		strXML += "<category label='-' />" + "\n";
		strXML += "</categories>" + "\n";
		strXML += "<dataset seriesName='Actual'>" + "\n";
		strXML += "<set value='0' />" + "\n";
		strXML += "</dataset>" + "\n";
		strXML += "<dataset seriesName='Compara'>" + "\n";
		strXML += "<set value='0'/>" + "\n";
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
		response.setContentType("text/xml");
	}
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
<%=strXML%>