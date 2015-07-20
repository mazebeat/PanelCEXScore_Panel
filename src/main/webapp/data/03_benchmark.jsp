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

String anio = request.getParameter("anio");
String mostrar_como = request.getParameter("mostrar_como");

String filtro_sede = request.getParameter("sede");
String filtros = "";

if (filtro_sede != "") 
{
	filtros = filtros + " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(Sede, 'á', 'a'),'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') = '" + filtro_sede + "' ";
	if (mostrar_como.equals("tabla"))
	{
		filtros = filtros + " AND Universidad = 'Todas'";
	}
}


String str_SQL_Base = "SELECT * FROM cs_benchmark WHERE " + filtros + " ORDER BY id_orden";

//out.println (str_SQL_Base);

int cant_registro_rpta = 0;

int N_Encuestas = 0;
Double Satisfaccion_Promotor = 0.0;
Double Satisfaccion_Detractor = 0.0;
Double Satisfaccion_NPS = 0.0;

Double Efectividad_Promotor = 0.0;
Double Efectividad_Detractor = 0.0;
Double Efectividad_NPS = 0.0;

Double Facil_Promotor = 0.0;
Double Facil_Detractor = 0.0;
Double Facil_NPS = 0.0;

Double Agradable_Promotor = 0.0;
Double Agradable_Detractor = 0.0;
Double Agradable_NPS = 0.0;

Double Lealtad = 0.0;

String strXML = "";
String html_preg = "";
String str_category = "";
String str_promotores = "";
String str_detractores = "";
String str_nps = "";

try
{
	Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = null;
	
	//out.println("Paso 01 <br>");
	//out.println(strSQL_Cualificacion);
	//out.flush(); // Send out whatever hasn't been sent out yet.
	//out.close(); // Close the stream. Future calls will fail.


	rs = stmt.executeQuery(str_SQL_Base);
	int rowcount = 0;
	if (rs.last()) 
	{
		rs.beforeFirst();
		
		if (mostrar_como.equals("tabla"))
		{
			rs.next();
			N_Encuestas = rs.getInt("N_Encuestas");
			Satisfaccion_Promotor = rs.getDouble("Satisfaccion_Promotor") * 100.0;
			Satisfaccion_Detractor = rs.getDouble("Satisfaccion_Detractor") * 100.0;
			Satisfaccion_NPS = rs.getDouble("Satisfaccion_NPS") * 100.0;

			Efectividad_Promotor = rs.getDouble("Efectividad_Promotor") * 100.0;
			Efectividad_Detractor = rs.getDouble("Efectividad_Detractor") * 100.0;
			Efectividad_NPS = rs.getDouble("Efectividad_NPS") * 100.0;

			Facil_Promotor = rs.getDouble("Facil_Promotor") * 100.0;
			Facil_Detractor = rs.getDouble("Facil_Detractor") * 100.0;
			Facil_NPS = rs.getDouble("Facil_NPS") * 100.0;

			Agradable_Promotor = rs.getDouble("Agradable_Promotor") * 100.0;
			Agradable_Detractor = rs.getDouble("Agradable_Detractor") * 100.0;
			Agradable_NPS = rs.getDouble("Agradable_NPS") * 100.0;

			Lealtad = rs.getDouble("Lealtad") * 100.0;
		}
		else if (mostrar_como.equals("grafico"))
		{
			while (rs.next())
			{
				str_category = str_category + "<category label='" + rs.getString("Universidad") + "' />" + "\n";
				
				str_promotores  = str_promotores + "<set value='" + EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_Promotor") * 100.0),0,0,0,0,locale) + "' />" + "\n";
				str_detractores = str_detractores + "<set value='-" + EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_Detractor") * 100.0),0,0,0,0,locale) + "' />" + "\n";
				str_nps = str_nps + "<set value='" + EW_FormatNumber(String.valueOf(rs.getDouble("Satisfaccion_NPS") * 100.0),0,0,0,0,locale) + "' />" + "\n";
			}
		}
	}
	rs.close();

	if (mostrar_como.equals("tabla"))
	{

		html_preg = html_preg + "<tr>";
		html_preg = html_preg + "	<td>&nbsp;Efectivo</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Efectividad_Promotor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Efectividad_Detractor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Efectividad_NPS),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "</tr>";
		html_preg = html_preg + "<tr>";
		html_preg = html_preg + "	<td>&nbsp;F&aacute;cil</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Facil_Promotor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Facil_Detractor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Facil_NPS),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "</tr>";
		html_preg = html_preg + "<tr>";
		html_preg = html_preg + "	<td>&nbsp;Agradable</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Agradable_Promotor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Agradable_Detractor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Agradable_NPS),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "</tr>";
		html_preg = html_preg + "<tr>";
		html_preg = html_preg + "	<td>&nbsp;<span title='NPS: Índice de Promotores Netos&#13;Obtenido por la diferencia entre el % de promotores menos el % de detractores'>NPS</span></td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Satisfaccion_Promotor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Satisfaccion_Detractor),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "	<td>&nbsp;" + EW_FormatNumber(String.valueOf(Satisfaccion_NPS),0,0,0,0,locale) + "%</td>";
		html_preg = html_preg + "</tr>";
		
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
		out.println("<span class='label3 label-important3'>Recomendaci&oacute;n&nbsp;<img src='img/corazon.gif' width='24' height='24' border='0'>&nbsp;" +  EW_FormatNumber(String.valueOf(Lealtad),0,0,0,0,locale) + "%</span>");
		out.println("<br>");
		out.println("Resultados sobre " + N_Encuestas + " encuestas realizadas en el Periodo Oct-14");

	}
	else if (mostrar_como.equals("grafico"))
	{
		strXML += "<chart showLegend='0' bgColor='ffffff' borderColor ='ffffff' numberSuffix='%' palette='1' lineThickness='2' showValues='1' anchorRadius='6' areaOverColumns='0' formatNumberScale='0'>" + "\n";
		strXML += "<categories >" + "\n";
		strXML += str_category;
		strXML += "</categories>" + "\n";
		strXML += "<dataset seriesName='Prom.' >" + "\n";
		strXML += str_promotores;
		strXML += "</dataset>" + "\n";
		strXML += "<dataset seriesName='Detr.'>" + "\n";
		strXML += str_detractores;
		strXML += "</dataset>" + "\n";
		strXML += "<dataset seriesName='NPS' renderAs='Line' color='ff0000' anchorBgColor='ff0000' lineThickness='3'>" + "\n";
		strXML += str_nps;
		strXML += "</dataset>" + "\n";
		strXML += "</chart>" + "\n";
		response.setContentType("text/xml");
	}

	// Close recordset and connection
	rs = null;
	stmt.close();
	stmt = null;
	conn.close();
	conn = null;
}
catch(SQLException ex)
{
	out.println(ex.toString());
}
%>

<%=strXML%>