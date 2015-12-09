c<%@ page
	import="cl.intelidata.servlet.MomentoServlet, cl.intelidata.util.Text"%>
<%@ page session="true"%>
<%@ page buffer="16kb"%>
<%@ page contentType="text/html; charset=ISO-8859-1"%>
<%
    response.setDateHeader("Expires", 0);
			response.addHeader("Cache-Control",
					"no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.addHeader("Pragma", "no-cache"); // HTTP/1.0

			String login = (String) session.getAttribute("Panel_"
					+ cl.intelidata.util.Text.ProyectoID00 + "_status");
			String context = request.getContextPath();

			if (login == null || !login.equalsIgnoreCase("login")) {
				response.sendRedirect(context + "/LoginServlet");
				return;
			}

			String[] Array_Facultades = (String[]) request
					.getAttribute("Array_Facultades");
			String[] Array_Campus = (String[]) request
					.getAttribute("Array_Campus");
			String[] Periodos = (String[]) request.getAttribute("Periodos");
			String[] Periodos2 = (String[]) request.getAttribute("Periodos2");
			String ultimo_periodo = (String) request
					.getAttribute("ultimo_periodo");
			String ultimo_periodo_f = (String) request
					.getAttribute("ultimo_periodo_f");
			String nombreCliente = (String) session.getAttribute("Panel_"
					+ Text.ProyectoID00 + "_status_NombreCliente");
			int Fila_Facultad = (Integer) request.getAttribute("Fila_Facultad");
			int Fila_Campus = (Integer) request.getAttribute("Fila_Campus");
			int visitCount = (Integer) request.getAttribute("visitCount");
%>
<!-- BEGIN HEADER -->
<%@ include file="inc/header2.jsp"%>
<!-- END HEADER -->
<!-- BEGIN HEADER CABECERA -->
<%@ include file="inc/header_cabecera.jsp"%>
<!-- END HEADER CABECERA -->
<div class="row">
	<div class="col-md-6 col-offset-md-3">${messagesPanel}</div>
	<div class="col-md-2 pull-right">
		<span class="pull-right"><strong>VISITAS:</strong> <%=visitCount%>
			Usuarios</span>
	</div>
</div>

<%--<!-- BEGIN FIRST ROW -->  row-eq-height--%>
<div class="row equal-height-panels">
	<!-- BEGIN BOX 2 -->
	<div class="col-md-5">
		<%@ include file="modules/result_last_period.jsp"%>
	</div>
	<!-- END BOX 2 -->
	<!-- BEGIN BOX 3 -->
	<div class="col-md-3">
		<%@ include file="modules/accumulated_results.jsp"%>
	</div>
	<!-- END BOX 2 -->
	<!-- BEGIN BOX 1 -->
	<div class="col-md-4">
		<div class="panel panel-warning">
			<div class="panel-heading" data-original-title>
				<h3 class="panel-title">
					<i class="fa fa-filter fa-fw"></i>Filtros
				</h3>
			</div>
			<div class="panel-body">
				<div class="row">
					<input type="hidden" name="Periodo-0B" id="Periodo-0B"> <input
						type="hidden" name="Periodo-1B" id="Periodo-1B"> <input
						type="hidden" name="Periodo-2B" id="Periodo-2B"> <input
						type="hidden" name="Periodo-3B" id="Periodo-3B"> <input
						type="hidden" name="TVistaB" id="TVistaB">
				</div>

				<strong>Periodos</strong>

				<div class="row">
					<div class="col-md-2">
						<h6>Inicial:</h6>
					</div>
					<div class="col-md-5">
						<h7>desde&nbsp;</h7>
						<select name="Periodo-0" id="Periodo-0" class="form-control">
							<option value="" selected></option>
							<%
							    int i = 0;
										for (i = 0; i < Periodos.length; i++) {
											if (Periodos[i] == null) {
												break;
											}
											out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i]
													+ "</option>");
										}
							%>
						</select>
					</div>
					<div class="col-md-5">
						<h7>hasta&nbsp;</h7>
						<select name="Periodo-1" id="Periodo-1" class="form-control">
							<option value="" selected></option>
							<%
							    int selected = 0;
										for (i = 0; i < Periodos.length; i++) {
											if (Periodos[i] == null) {
												break;
											}
											if (selected == 0) {
												selected = 1;
												out.print("<option selected value='" + Periodos2[i] + "'>"
														+ Periodos[i] + "</option>");
											} else {
												out.print("<option value='" + Periodos2[i] + "'>"
														+ Periodos[i] + "</option>");
											}
										}
							%>
						</select>
					</div>
					<%--</div>--%>

					<%--<div class="row">--%>
					<div class="col-md-2">
						<h6>Comparar:</h6>
					</div>
					<div class="col-md-5">
						<h6>desde&nbsp;</h6>
						<select name="Periodo-2" id="Periodo-2" class="form-control">
							<option value="" selected></option>
							<%
							    for (i = 0; i < Periodos.length; i++) {
											if (Periodos[i] == null) {
												break;
											}
											out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i]
													+ "</option>");
										}
							%>
						</select>
					</div>
					<div class="col-md-5">
						<h6>hasta&nbsp;</h6>
						<select name="Periodo-3" id="Periodo-3" class="form-control">
							<option value="" selected></option>
							<%
							    for (i = 0; i < Periodos.length; i++) {
											if (Periodos[i] == null) {
												break;
											}
											out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i]
													+ "</option>");
										}
							%>
						</select>
					</div>
				</div>

				<button class="btn btn-default btn-xs pull-right"
					onclick="javascript:FiltrarDatos('boton', '', '', '', '', '', '', '');">
					<i class="fa fa-search fa-fw"></i>Filtrar
				</button>
			</div>
		</div>
		<%@ include file="modules/show_progress.jsp"%>
	</div>
	<!-- END BOX 1 -->
</div>
<!--BEGIN CUADROS X MOMENTOS-->
<div id="moment_boxes">
	<%
	    String moment_boxes = MomentoServlet.generateMoments(request,
						response);
				out.print(moment_boxes);
	%>
</div>
<!--END CUADROS X MOMENTOS-->

<%--MODALS--%>
<div id="myModal" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-info" id="body-modalT">
				<button type='button' class='close' data-dismiss='modal'>
					<i class='fa fa-times'></i>
				</button>
				<h3>
					<i class='fa fa-bullhorn fa-fw'></i>Lo que dice la gente
				</h3>
			</div>
			<div class="modal-body" id="body-modalC"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">SALIR</button>
			</div>
		</div>
	</div>
</div>
<div id="myModal2" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header bg-info" id="modalFindH">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true"><i class='fa fa-times'></i></span>
				</button>
				<h4 class="modal-title">
					<i class='fa fa-search fa-fw'></i> B&uacute;squeda sem&aacute;ntica
				</h4>
			</div>
			<div class="modal-body" id="modalFindB">
				<div class="row">
					<div class="col-xs-12">
						<form id="SimpleSearchForm" method="post" class="form-inline"
							role="form">
							<div class="form-group">
								<label class="sr-only" for="wordFind">Buscar </label> <input
									type="text" class="form-control" id="wordSearch"
									name="wordSearch" placeholder="Ingrese una palabra...">
							</div>
							<div class="form-group">
								<label class="sr-only" for="fromDate">Desde </label>

								<div id="fromDateDiv"></div>
								<select class="form-control" id="fromDate" name="fromDate"
									placeholder="Desde...">
									<option value="" selected></option>
									<%
									    for (i = 0; i < Periodos.length; i++) {
													if (Periodos[i] == null) {
														break;
													}
													out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i]
															+ "</option>");
												}
									%>
								</select>
							</div>
							<div class="form-group">
								<label class="sr-only" for="toDate">Hasta </label>

								<div id="toDateDiv"></div>
								<select class="form-control" id="toDate" name="toDate"
									placeholder="Hasta...">
									<option value="" selected></option>
									<%
									    selected = 0;
												for (i = 0; i < Periodos.length; i++) {
													if (Periodos[i] == null) {
														break;
													}
													if (selected == 0) {
														selected = 1;
														out.print("<option selected value='" + Periodos2[i] + "'>"
																+ Periodos[i] + "</option>");
													} else {
														out.print("<option value='" + Periodos2[i] + "'>"
																+ Periodos[i] + "</option>");
													}
												}
									%>
								</select>
							</div>
							<div class="form-group">
								<input type="hidden" class="form-control" id="chart"
									name="chart" value="00_simplesearch">
							</div>
							<button type="submit" class="btn btn-default">Buscar</button>
						</form>
					</div>
				</div>
				<br>

				<div class="row">
					<div class="col-xs-12">
						<div id="loadingSimpleSearch" class="text-center hidden">
							<i class="fa fa-spinner fa-2x fa-pulse"></i>
						</div>
						<div id="resultSimpleSearch"></div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">SALIR</button>
			</div>
		</div>
	</div>
</div>
<div class="modal hide fade" id="myModalR" style="display: none;">
	<div class="modal-header" id="body-modalTR">&nbsp;</div>
	<div class="modal-body" id="body-modalCR">&nbsp;</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">SALIR</a>
	</div>
</div>
<!-- <script src="js/jquery.matchHeight-min.js"></script> -->
<!-- BEGIN SCRIPTS -->
<script type="text/javascript">
	// 	$('.equal-height-panels .panel').matchHeight();
	// 	setTimeout(2000);

	// General Muestra Grafico Inicial
	var myChart;
	var anchoGraf = '100%';

	var context = '<%=request.getContextPath()%>';
	var count_moments = <%=request.getAttribute("count_moments")%>;
	var nombre_cliente = '<%=nombreCliente%>';
	var ultimo_periodo_f = '<%=ultimo_periodo_f%>';

	FusionCharts.setCurrentRenderer('javascript');

	/**
	 *
	 */
	function Comentarios(moment) {

		if (typeof moment === 'undefined') {
			moment = "";
		}

		var param;
		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista;
		var method = "&chart=00_comentarios";

		periodo_0 = $('#Periodo-0B').val();
		periodo_1 = $('#Periodo-1B').val();
		periodo_2 = $('#Periodo-2B').val();
		periodo_3 = $('#Periodo-3B').val();

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&moment=' + moment;

		$('#myModal').modal('show');
		$('#body-modalT')
				.html(
						$("<button type='button' class='close' data-dismiss='modal'><i class='fa fa-times'></i></button><h3><i class='fa fa-bullhorn fa-fw'></i>Lo que dice la gente</h3>"));
		cargarContenido(context + '/ChartServlet' + param + method,
				'body-modalC');
	}

	/**
	 *
	 * @param obj
	 * @param value
	 * @returns {number}
	 * @constructor
	 */
	function EW_selectopt2(obj, value) {
		for (var i = obj.length - 1; i >= 0; i--) {
			if (obj.options[i].value.toUpperCase() == value.toUpperCase()) {
				return i;
			}
		}
		return 9999;
	}

	/**
	 *
	 * @param txt
	 * @returns {boolean}
	 */
	function mensajero(txt) {
		alert(txt);
		return false;
	}

	/**
	 *
	 * @param periodo_0
	 * @param periodo_1
	 * @param periodo_2
	 * @param periodo_3
	 * @param sede
	 * @param facultad
	 * @param Titulo
	 * @constructor
	 */
	//	function FiltrarDatos2(periodo_0, periodo_1, periodo_2, periodo_3, sede, facultad, Titulo) {
	//		$("#cuadros_sedes_facultad").css('display', 'block');
	//		$("#titulo_sede_facultad").html($('<h3><i class="icon-th"></i>' + Titulo + '</h3>'));
	//
	//		var imgExcel = "UMayor  <img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('" + sede + "','" + facultad + "','');\">";
	//		var imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('" + sede + "','" + facultad + "','');\"></span>";
	//		var content = imgExcel + imgComen;
	//		$('#exportar_sede_facultad').html($(content));
	//
	//		//Santiago UMayor Grafico -> OK
	//		var param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=grafico';
	//		myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_sede_facultad_id", "100%", "130", "0", "0");
	//		myChart.setDataURL(encodeURIComponent('/02_periodo_indicadores.jsp' + param));
	//		myChart.render("satisfaccion_sede_facultad");
	//
	//		//Santiago UMayor Detalle -> OK
	//		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=tabla';
	//		cargarContenido('data/02_periodo_indicadores.jsp' + param, 'satisfaccion_sede_facultad_detalle');
	//
	//		//Santiago UMayor Acumulado -> OK
	//		param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + sede + '&facultad=' + facultad + '&campus=';
	//		cargarContenido('data/01_acumulado.jsp' + param, 'div_acum_sede_facultad');
	//
	//		//Santiago UMayor Tendencia -> OK
	//		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=';
	//		myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_sede_facultad_id", "100%", "200", "0", "0");
	//		myChart.setDataURL(encodeURIComponent('data/04_tendencia.jsp' + param));
	//		myChart.render("div_tendencia_sede_facultad");
	//	}
	/**
	 *
	 */
	function addButtonExport(id_div, moment) {
		// ADD BUTTONS EXPORT AND COMMENT
		var imgExcel = "<a title='Exportar Encuestas' href='#' onclick='javascript:Exportar("
				+ moment
				+ ");return false;'><i class='fa fa-file-excel-o fa-lg fa-fw export'></i></a>";
		var imgComen = "<a title='Ver Comentarios' href='#' onclick='javascript:Comentarios("
				+ moment
				+ ");return false;'><i class='fa fa-comment fa-lg fa-fw comment'></i></a>";
		var imgSearch = "<a title='Busqueda Simple' href='#' onclick='javascript:SimpleSearch_GET();return false;\'><i class='fa fa-search fa-lg fa-fw simpleSearch'></i></a>";
		var content = "<strong>" + nombre_cliente + "</strong>&nbsp;"
				+ imgExcel + imgComen + imgSearch;
		$("#" + id_div).html($(content));
	}

	/**
	 *
	 */
	function GenerateShowProgressChart(periodo_2I, periodo_0, periodo_1,
			periodo_2, periodo_3) {
		var method = "&chart=00_muestra";
		var content;
		if (periodo_2I > 0) {
			content = $('<div class="col-md-6"><div id="myChart01" class="col-center-block col-xs-12 col-sm-12 col-md-12 col-lg-12" style="z-index:100"></div><div id="myChart01_detallle"></div></div><div class="col-md-6"><div id="myChart01z" style="z-index:100"></div><div id="myChart01z_detallle"></div></div>');
			$('#div_avances').html(content);
		} else {
			content = $('<div class="col-md-12"><div id="myChart01z" class="col-center-block col-xs-12 col-sm-12 col-md-12 col-lg-12" style="z-index:100"></div><div id="myChart01z_detallle"></div></div>');
			$('#div_avances').html(content);
		}

		var param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1;

		// LOAD CONTENT TO 'myChart01z_detallle'
		cargarContenido(context + '/ChartServlet' + param + method
				+ '&tipoD=0&Titulo=Inicial', 'myChart01z_detallle');

		if (periodo_2I > 0) {
			anchoGraf = '100%';
		}

		// General Muestra Grafico Inicial -> OK
		if (FusionCharts("myChart01z_id")) {
			FusionCharts("myChart01z_id").dispose();
		}

		myChart = new FusionCharts("charts/HLinearGauge.swf", "myChart01z_id",
				anchoGraf, "65", false, false);
		myChart.setDataURL(encodeURIComponent(context + '/ChartServlet' + param
				+ method + '&tipoD=1&Titulo=Inicial'));
		myChart.render("myChart01z");

		if (periodo_2I > 0) {
			param = '?currTime=' + getTimeForURL() + '&periodo_2=' + periodo_2
					+ '&periodo_3=' + periodo_3;
			cargarContenido(context + '/ChartServlet' + param + method
					+ '&tipoD=0&Titulo=Comparar', 'myChart01_detallle');

			if (FusionCharts("myChart01_id")) {
				FusionCharts("myChart01_id").dispose();
			}

			myChart = new FusionCharts("charts/HLinearGauge.swf",
					"myChart01_id", anchoGraf, "65", false, false);
			myChart.setDataURL(encodeURIComponent(context + '/ChartServlet'
					+ param + method + '&tipoD=1&Titulo=Comparar'));
			myChart.render("myChart01");
		}
	}

	/**
	 *
	 */
	function GenerateResultLastPeriodChart(periodo_0, periodo_1, periodo_2,
			periodo_3) {
		//  GENERATE GRAPH 'RESULTADOS ULTIMO PERIODO'
		var path, param, method = "&chart=03_benchmark";

		//  COMPETENCIA
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&mostrar_como=grafico';

		if (FusionCharts("GE_id")) {
			FusionCharts("GE_id").dispose();
		}

		myChart = new FusionCharts("charts/MSCombi2D.swf", "GE_id", "100%",
				"130", false, false);
		myChart.setDataURL(encodeURIComponent(context + '/ChartServlet' + param
				+ method));
		myChart.render("GE_chart");

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&mostrar_como=tabla';
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&mostrar_como=tabla';
		path = context + '/ChartServlet' + param + method;
		cargarContenido(context + '/ChartServlet' + param + method, 'GE_detail');

		//  CLIENTE
		method = "&chart=02_periodo_indicadores";
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&mostrar_como=grafico';

		if (FusionCharts("client__id")) {
			FusionCharts("client__id").dispose();
		}

		myChart = new FusionCharts("charts/MSCombi2D.swf", "client__id",
				"100%", "130", false, false);
		myChart.setDataURL(encodeURIComponent(context + '/ChartServlet' + param
				+ method));
		myChart.render("client_chart");

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&mostrar_como=tabla';
		path = context + '/ChartServlet' + param + method;
		cargarContenido(path, 'client_detail');
	}

	/**
	 *
	 */
	function GenerateAccumulatedResult() {
		var method = "&chart=01_acumulado";
		var param = '?currTime=' + getTimeForURL();
		cargarContenido(context + '/ChartServlet' + param + method,
				'div_acum_global');
	}

	/**
	 *
	 */
	function GenerateMoments(number, periodo_0, periodo_1, periodo_2, periodo_3) {
		//  CLIENTE
		/// CHART
		method = "&chart=02_periodo_indicadores";
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&moment=' + number
				+ '&mostrar_como=grafico';

		if (FusionCharts("moment_" + number + "_id")) {
			FusionCharts("moment_" + number + "_id").dispose();
		}

		myChart = new FusionCharts("charts/MSCombi2D.swf", "moment_" + number
				+ "_id", "100%", "130", false, false);
		myChart.setDataURL(encodeURIComponent(context + '/MomentoServlet'
				+ param + method));
		myChart.render("chart_moment_" + number);

		/// DETAIL
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&moment=' + number
				+ '&mostrar_como=tabla';
		path = context + '/MomentoServlet' + param + method;
		cargarContenido(path, 'detail_moment_' + number);

		// TENDENCE
		method = "&chart=04_tendencia";
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + '&moment=' + number;

		if (FusionCharts("trend_moment_" + number + "_id")) {
			FusionCharts("trend_moment_" + number + "_id").dispose();
		}

		myChart = new FusionCharts("charts/MSLine.swf", "trend_moment_"
				+ number + "_id", "100%", "200", "0", "0");
		myChart.setDataURL(encodeURIComponent(context + '/MomentoServlet'
				+ param + method));
		myChart.render("trend_moment_" + number);
	}

	/**
	 *
	 * @param trigger
	 * @param str_facultad
	 * @param str_sede
	 * @param str_campus
	 * @param per_0
	 * @param per_1
	 * @param per_2
	 * @param per_3
	 * @constructor
	 */
	function FiltrarDatos(trigger, str_sede, str_facultad, str_campus, per_0,
			per_1, per_2, per_3) {

		var param, method, path;
		var ValidarFiltros2 = true;
		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista, newHTML;
		var periodo_0I = 0;
		var periodo_1I = 0;
		var periodo_2I = 0;
		var periodo_3I = 0;

		if (trigger === 'boton') {
			periodo_0 = $('#Periodo-0').val();
			periodo_1 = $('#Periodo-1').val();
			periodo_2 = $('#Periodo-2').val();
			periodo_3 = $('#Periodo-3').val();

			periodo_0I = EW_selectopt2(document.getElementById('Periodo-0'),
					document.getElementById('Periodo-0').value);
			periodo_1I = EW_selectopt2(document.getElementById('Periodo-1'),
					document.getElementById('Periodo-1').value);
			periodo_2I = EW_selectopt2(document.getElementById('Periodo-2'),
					document.getElementById('Periodo-2').value);
			periodo_3I = EW_selectopt2(document.getElementById('Periodo-3'),
					document.getElementById('Periodo-3').value);

			Dif_Actual = periodo_0I - periodo_1I;
			Dif_Compara = periodo_2I - periodo_3I;

			if (periodo_0I > 0 && periodo_0I < periodo_1I)
				ValidarFiltros2 = mensajero("Periodo Inicial: Desde no puede ser mayor que Periodo Inicial: Hasta");

			else if (periodo_0I === 0 && (periodo_2I > 0 || periodo_3I > 0))
				ValidarFiltros2 = mensajero("Periodo Inicial: Desde no puede ser vac\u00edo");

			else if (periodo_2I < periodo_3I)
				ValidarFiltros2 = mensajero("Periodo Comparar: Desde no puede ser mayor que Periodo Comparar: Hasta");

			else if (periodo_3I > 0 && periodo_3I <= periodo_0I)
				ValidarFiltros2 = mensajero("Periodo Comparar: Hasta no puede ser mayor o igual que Periodo Inicial: Desde");

			else if (periodo_2I > 0 && Dif_Actual !== Dif_Compara)
				ValidarFiltros2 = mensajero("Los Periodos a comparar no poseen la misma cantidad de meses");
		} else {
			periodo_0 = '';
			periodo_1 = per_1;
			periodo_2 = '';
			periodo_3 = '';
			TipoVista = 'sede';
		}

		if (ValidarFiltros2) {
			$('#Periodo-0B').val(periodo_0);
			$('#Periodo-1B').val(periodo_1);
			$('#Periodo-2B').val(periodo_2);
			$('#Periodo-3B').val(periodo_3);

			addButtonExport('client_export', '');

			//  GENERATE GRAPH INIT -- CAJA 1
			GenerateShowProgressChart(periodo_2I, periodo_0, periodo_1,
					periodo_2, periodo_3);

			//  GENERATE GRAPH '' -- CAJA 2
			GenerateResultLastPeriodChart(periodo_0, periodo_1, periodo_2,
					periodo_3);

			//  GENERATE GRAPH 'RESULTADOS ACUMULADOS' -- CAJA 3
			GenerateAccumulatedResult();

			/**
			 * GENERA MOMENTOS --- CICLOS POR CADA MOMENTO ASIGNADO A LA ENCUESTA QUE TIENE EL CLIENTE
			 */
			for (var i = 1; i < count_moments; i++) {
				setTimeout(2000);
				GenerateMoments(i, periodo_0, periodo_1, periodo_2, periodo_3);
				addButtonExport('export_moment_' + i, i);
			}

			$('#moment_boxes').show('swing');
		}
	}

	/**
	 *
	 * @param sede
	 * @param facultad
	 * @param campus
	 * @constructor
	 */
	function Exportar(moment) {
		if (typeof moment == 'undefined') {
			moment = "";
		}

		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista;
		periodo_0 = $('#Periodo-0B').val();
		periodo_1 = $('#Periodo-1B').val();
		periodo_2 = $('#Periodo-2B').val();
		periodo_3 = $('#Periodo-3B').val();
		TipoVista = $('#TVistaB').val();

		var param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + "&moment=" + moment;

		param = param + '&action=export';
		document.location.target = '_blank';
		document.location.href = context + '/ExportServlet' + param;
	}

	function SimpleSearch_GET() {
		$('#resultSimpleSearch').empty()
		$('#myModal2').modal('show')
	}

	$('#SimpleSearchForm').submit(
			function(e) {
				e.preventDefault();
				var params = $(this).serializeArray();
				var method = '00_simplesearch';
				var wordSearch = $('#wordSearch').val();
				var fromDate = $('#fromDate').val();
				var toDate = $('#toDate').val();

				cargarContenidoPost(context + '/ChartServlet', params,
						'resultSimpleSearch')
			});

	FiltrarDatos('nav', '', '', '', '', ultimo_periodo_f, '', '');
</script>
<!-- END SCRIPTS -->
<!-- BEGIN FOOTER -->
<%@ include file="inc/footer.jsp"%>
<!-- END FOOTER -->