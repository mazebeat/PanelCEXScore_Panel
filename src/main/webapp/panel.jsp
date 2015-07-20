<%@ page session="true" %>
<%@ page buffer="16kb" %>

<%@ page contentType="text/html; charset=ISO-8859-1" %>
%>
<%
	response.setDateHeader("Expires", 0); // date in the past
	response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.addHeader("Pragma", "no-cache"); // HTTP/1.0

	String[] Array_Facultades = (String[])request.getAttribute("Array_Facultades");
	String[] Array_Campus = (String[])request.getAttribute("Array_Campus");
	String ultimo_periodo = (String)request.getAttribute("ultimo_periodo");
	String ultimo_periodo_f = (String)request.getAttribute("ultimo_periodo_f");
	int Fila_Facultad = (Integer)request.getAttribute("Fila_Facultad");
	int Fila_Campus = (Integer)request.getAttribute("Fila_Campus");
%>

<!-- BEGIN HEADER -->
<%@ include file="inc/header2.jsp" %>
<!-- END HEADER -->

<!-- BEGIN HEADER CABECERA -->
<%@ include file="inc/header_cabecera.jsp" %>
<!-- END HEADER CABECERA -->


<!-- BEGIN FIRST ROW -->
<div class="row">
	<!-- BEGIN BOX 1 -->
	<div class="col-md-4">
		<%@ include file="modules/filters.jsp" %>
		<%@ include file="modules/show_progress.jsp" %>
	</div>
	<!-- END BOX 1 -->

	<!-- BEGIN BOX 2 -->
	<div class="col-md-4">
		<%@ include file="modules/result_last_period.jsp" %>
	</div>
	<!-- END BOX 2 -->

	<!-- BEGIN BOX 3 -->
	<div class="col-md-4">
		<%@ include file="modules/accumulated_results.jsp" %>
	</div>
	<!-- END BOX 4 -->
</div>
<!-- END FIRST ROW -->

<!--BEGIN CUADROS X -->
<div id="cuadros_sedes" class="row" style="display:none">
	<!-- <div id="cuadros_momentos" class="row"> -->
	<div class="col-md-6">
		<%@ include file="modules/module1.jsp" %>
	</div>

	<!-- 	<div class="col-md-6"> -->
	<!-- 		<%@ include file="modules/module2.jsp"%> -->
	<!-- 	</div> -->
</div>
<!--END CUADROS X SEDE-->%>
<% // VARS FOR THE BLOCKS
	int facultad_zz = 0;
	int facultad_zz_grupo = 0;
	int row_tmp = 0;

	int campus_zz = 0;
	int campus_zz_grupo = 0;
%>

<!--COMIENZO CUADROS X SEDE-FACULTAD-->
<%@ include file="modules/blocks_sedesFacultad.jsp" %>
<!--FIN CUADROS X SEDE-FACULTAD-->%>
<%row_tmp = 0;%>
<!-- BEGIN BLOCKS BY FACULTAD --><%-- <%@ include file="modules/blocks_facultad.jsp"%>--%>
<!-- END BLOCKS BY FACULTAD -->

<!-- BEGIN BLOCKS BY CAMPUS --><%-- <%@ include file="modules/blocks_campus.jsp"%>--%>
<!-- END BLOCKS BY CAMPUS -->

<div class="modal hide fade" id="myModal" style="display: none;">
	<div class="modal-header" id="body-modalT">&nbsp;</div>
	<div class="modal-body" id="body-modalC">&nbsp;</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">SALIR</a>
	</div>
</div>

<div class="modal hide fade" id="myModalR" style="display: none;">
	<div class="modal-header" id="body-modalTR">&nbsp;</div>
	<div class="modal-body" id="body-modalCR">&nbsp;</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">SALIR</a>
	</div>
</div>

<!-- BEGIN SCRIPTS -->
<script type="text/javascript">
	var context = '<%= request.getContextPath()%>';
	console.info('CONTEXT PATH: ' + context);

	FusionCharts.setCurrentRenderer('javascript');

	//FiltrarDatos();
	FiltrarDatos('nav', '', '', '', '', '<%= ultimo_periodo_f %>', '', '');

	function Comentarios(sede, facultad, campus) {
		var param;
		var periodo_0, periodo_1, periodo_2, pe9riodo_3, TipoVista;

		periodo_0 = $('#Periodo-0B').val();
		periodo_1 = $('#Periodo-1B').val();
		periodo_2 = $('#Periodo-2B').val();
		periodo_3 = $('#Periodo-3B').val();

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=' + campus;

		$('#body-modalT').html($("<button type='button' class='close' data-dismiss='modal'>×</button><h3>Lo que dice la gente</h3>"));
		cargarContenido('/ChartServlet' + param, 'body-modalC');
		$('#myModal').modal('show');
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
	function FiltrarDatos2(periodo_0, periodo_1, periodo_2, periodo_3, sede, facultad, Titulo) {
		$("#cuadros_sedes_facultad").css('display', 'block');
		$("#titulo_sede_facultad").html($('<h3><i class="icon-th"></i>' + Titulo + '</h3>'));

		imgExcel = "UMayor  <img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('" + sede + "','" + facultad + "','');\">";
		imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('" + sede + "','" + facultad + "','');\"></span>";
		var content = imgExcel + imgComen;
		$('#exportar_sede_facultad').html($(content));

		//Santiago UMayor Grafico -> OK
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=grafico';
		myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_sede_facultad_id", "100%", "130", "0", "0");
		myChart.setDataURL(encodeURIComponent('/02_periodo_indicadores.jsp' + param));
		myChart.render("satisfaccion_sede_facultad");

		//Santiago UMayor Detalle -> OK
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=tabla';
		cargarContenido('data/02_periodo_indicadores.jsp' + param, 'satisfaccion_sede_facultad_detalle');

		//Santiago UMayor Acumulado -> OK
		param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + sede + '&facultad=' + facultad + '&campus=';
		cargarContenido('data/01_acumulado.jsp' + param, 'div_acum_sede_facultad');

		//Santiago UMayor Tendencia -> OK
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=';
		myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_sede_facultad_id", "100%", "200", "0", "0");
		myChart.setDataURL(encodeURIComponent('data/04_tendencia.jsp' + param));
		myChart.render("div_tendencia_sede_facultad");
	}

	/**
	 *
	 * @param trigger
	 * @param str_sede
	 * @param str_facultad
	 * @param str_campus
	 * @param per_0
	 * @param per_1
	 * @param per_2
	 * @param per_3
	 * @constructor
	 */
	function FiltrarDatos(trigger, str_sede, str_facultad, str_campus, per_0, per_1, per_2, per_3) {

		console.log("INIT FILTRARDATOS......");

		var param;
		var ValidarFiltros2 = true;
		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista, newHTML;
		var periodo_0I = 0;
		var periodo_1I = 0;
		var periodo_2I = 0;
		var periodo_3I = 0;

		console.log("QUESTION TRIGGER......");

		if (trigger === 'boton') {
			console.log("TRIGGER EQUALS 'BOTON'......");

			periodo_0 = $('#Periodo-0').val();
			periodo_1 = $('#Periodo-1').val();
			periodo_2 = $('#Periodo-2').val();
			periodo_3 = $('#Periodo-3').val();

			TipoVista = GetRadioValue($("[name=TVista]"));

			console.log("VALUES......", periodo_0, periodo_1, periodo_2, periodo_3, TipoVista);

			periodo_0I = EW_selectopt2($('#Periodo-0'), $('#Periodo-0').val());
			periodo_1I = EW_selectopt2($('#Periodo-1'), $('#Periodo-1').val());
			periodo_2I = EW_selectopt2($('#Periodo-2'), $('#Periodo-2').val());
			periodo_3I = EW_selectopt2($('#Periodo-3'), $('#Periodo-3').val());

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
		}
		else {
			console.log("TRIGGER ELSE......");

			periodo_0 = '';
			periodo_1 = per_1;
			periodo_2 = '';
			periodo_3 = '';
			TipoVista = 'sede';
		}

		console.log("VALIDATE 'VALIDARFILTROS2'......", ValidarFiltros2);

		if (ValidarFiltros2) {

			console.log("GET VALUES......");

			$('#Periodo-0B').val(periodo_0);
			$('#Periodo-1B').val(periodo_1);
			$('#Periodo-2B').val(periodo_2);
			$('#Periodo-3B').val(periodo_3);

			// ADD BUTTONS EXPORT AND COMMENT
			imgExcel = "<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('','','');\">";
			imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment' onclick=\"javascript:Comentarios('','','');\"></span>";

			var content = imgExcel + imgComen;
			$('#exportar_general').html(content);

			// GENERATE GRAPH INIT
			//General Muestra Texto Inicial -> OK
			if (periodo_2I > 0) {
				$('#div_avances').html($('<div class="col-md-6"><br><div id="myChart01z" style="z-index:100"></div><div id="myChart01z_detallle"></div></div><div class="col-md-6"><br><div id="myChart01" style="z-index:100"></div><div id="myChart01_detallle"></div></div>'));
			}
			else {
				$('#div_avances').html($('<div class="col-md-12"><br><div id="myChart01z" style="z-index:100"></div><div id="myChart01z_detallle"></div></div>'));
			}

			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1;// + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3;

			// LOAD CONTENT TO 'myChart01z_detallle'
			console.log('/ChartServlet' + param + '&tipoD=0&Titulo=Inicial');
			cargarContenido('/ChartServlet' + param + '&tipoD=0&Titulo=Inicial', 'myChart01z_detallle');

			//General Muestra Grafico Inicial
			var myChart;
			var anchoGraf = 250;

			if (periodo_2I > 0) {
				anchoGraf = 200;
			}

			//General Muestra Grafico Inicial -> OK
			console.log('SHOW INIT CHART');
			myChart = new FusionCharts("charts/HLinearGauge.swf", "myChart01z_id", anchoGraf, "45", "0", "0");
			myChart.setDataURL(encodeURIComponent('/ChartServlet' + param + '&tipoD=1&Titulo=Inicial'));
			myChart.render("myChart01z");

			if (periodo_2I > 0) {
				console.log('INIT CHART.......PERIOD 2');
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_2 + '&periodo_1=' + periodo_3;
				cargarContenido('/ChartServlet' + param + '&tipoD=0&Titulo=Comparar', 'myChart01_detallle');

				console.log('SHOW INIT CHART 2');
				myChart = new FusionCharts("charts/HLinearGauge.swf", "myChart01_id", anchoGraf, "45", "0", "0");
				myChart.setDataURL(encodeURIComponent('/ChartServlet' + param + '&tipoD=1&Titulo=Comparar'));
				myChart.render("myChart01");
			}

			// GENERATE GRAPH 'Resultados último periodo' ...
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=todas&mostrar_como=grafico';
			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_GE_id", "100%", "130", "0", "0");
			myChart.setDataURL(encodeURIComponent('data/03_benchmark.jsp' + param));
			myChart.render("satisfaccion_GE");

//			// General GE detalle -> OK
//			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=Todas&mostrar_como=tabla';
//			cargarContenido('data/03_benchmark.jsp' + param, 'satisfaccion_GE_detalle');

//			//General UMayor Grafico -> OK
//			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=&mostrar_como=grafico';

			alert(param);

			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor0_id", "100%", "130", "0", "0");
			myChart.setDataURL(encodeURIComponent('data/02_periodo_indicadores.jsp' + param));
			myChart.render("satisfaccion_mayor0");

//			//General UMayor Detalle -> OK
//			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=&mostrar_como=tabla';
//			cargarContenido('data/02_periodo_indicadores.jsp' + param, 'satisfaccion_mayor0_detalle');
//
//			//General UMayor Acumulado -> OK
//			param = '?currTime=' + getTimeForURL() + '&anio=2014&anio2=2015&sede=&facultad=&campus=';
//			cargarContenido('data/01_acumulado.jsp' + param, 'div_acum_global');

			if (TipoVista == 'sede') {
				<%--document.getElementById("cuadros_sedes").style.display = "block";--%>
				<%--document.getElementById("cuadros_sedes_facultad").style.display = "none";--%>
				<%--<%--%>

				<%--for (row_tmp = 0; row_tmp < facultad_zz_grupo; row_tmp++) {--%>
				<%--out.println("document.getElementById('cuadros_facultad_" + row_tmp + "').style.display = 'none';" + "\n");--%>
				<%--}--%>

				<%--for (row_tmp = 0; row_tmp < campus_zz_grupo; row_tmp++) {--%>
				<%--out.println("document.getElementById('cuadros_campus_" + row_tmp + "').style.display = 'none';" + "\n");--%>
				<%--}--%>
				<%--%>--%>


				<%--//Santiago GE Grafico -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&mostrar_como=grafico';--%>
				<%--myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_santiago_GE_id", "100%", "130", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/03_benchmark.jsp' + param));--%>
				<%--myChart.render("satisfaccion_mayor_santiago_GE");--%>

				<%--//Santiago GE Detalle -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&mostrar_como=tabla';--%>
				<%--cargarContenido('data/03_benchmark.jsp' + param, 'satisfaccion_mayor_santiago_GE_detalle');--%>


				<%--//Santiago UMayor Grafico -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=&mostrar_como=grafico';--%>
				<%--myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_santiago_id", "100%", "130", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/02_periodo_indicadores.jsp' + param));--%>
				<%--myChart.render("satisfaccion_mayor_santiago");--%>


				<%--//Santiago UMayor Detalle -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=&mostrar_como=tabla';--%>
				<%--cargarContenido('data/02_periodo_indicadores.jsp' + param, 'satisfaccion_mayor_santiago_detalle');--%>

				<%--//Santiago UMayor Acumulado -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=';--%>
				<%--cargarContenido('data/01_acumulado.jsp' + param, 'div_acum_division_santiago');--%>

				<%--//Santiago UMayor Tendencia -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=';--%>
				<%--myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_division_santiago_id", "100%", "200", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/04_tendencia.jsp' + param));--%>
				<%--myChart.render("div_tendencia_division_santiago");--%>

				<%--//Lista de Facultades 01--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=&display_desde=0&display_hasta=6';--%>
				<%--//			document.location.href = 'data/05_facultades.jsp' + param;--%>
				<%--cargarContenido('data/05_facultades.jsp' + param, 'fac_santiago_list01');--%>

				<%--//Lista de Facultades 02--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Santiago') + '&facultad=&campus=&display_desde=7&display_hasta=100';--%>
				<%--//			document.location.href = 'data/05_facultades.jsp' + param;--%>
				<%--cargarContenido('data/05_facultades.jsp' + param, 'fac_santiago_list02');--%>


				<%--///////TEMUCO--%>

				<%--//temuco GE Grafico -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&mostrar_como=grafico';--%>
				<%--myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_temuco_GE_id", "100%", "130", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/03_benchmark.jsp' + param));--%>
				<%--myChart.render("satisfaccion_mayor_temuco_GE");--%>

				<%--//temuco GE Detalle -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&mostrar_como=tabla';--%>
				<%--cargarContenido('data/03_benchmark.jsp' + param, 'satisfaccion_mayor_temuco_GE_detalle');--%>

				<%--//temuco UMayor Grafico--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&facultad=&campus=&mostrar_como=grafico';--%>
				<%--myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_temuco_id", "100%", "130", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/02_periodo_indicadores.jsp' + param));--%>
				<%--myChart.render("satisfaccion_mayor_temuco");--%>

				<%--//temuco UMayor Detalle -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&facultad=&campus=&mostrar_como=tabla';--%>
				<%--cargarContenido('data/02_periodo_indicadores.jsp' + param, 'satisfaccion_mayor_temuco_detalle');--%>

				<%--//temuco UMayor Acumulado -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&facultad=&campus=';--%>
				<%--cargarContenido('data/01_acumulado.jsp' + param, 'div_acum_division_temuco');--%>


				<%--//temuco UMayor Tendencia -> OK--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División temuco') + '&facultad=&campus=';--%>
				<%--myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_division_temuco_id", "100%", "200", "0", "0");--%>
				<%--myChart.setDataURL(encodeURIComponent('data/04_tendencia.jsp' + param));--%>
				<%--myChart.render("div_tendencia_division_temuco");--%>

				<%--//Lista de Facultades 01--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Temuco') + '&facultad=&campus=&display_desde=0&display_hasta=5';--%>
				<%--document.location.href = 'data/05_facultades.jsp' + param;--%>
				<%--cargarContenido('data/05_facultades.jsp' + param, 'fac_temuco_list01');--%>

				<%--//Lista de Facultades 02--%>
				<%--param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + cl.intelidata.otros.jspmkrfn.Limpia_String('División Temuco') + '&facultad=&campus=&display_desde=6&display_hasta=100';--%>
				<%--document.location.href = 'data/05_facultades.jsp' + param;--%>
				<%--cargarContenido('data/05_facultades.jsp' + param, 'fac_temuco_list02');--%>
			}
		}
	}

	function Exportar(sede, facultad, campus) {
		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista;
		periodo_0 = $('#Periodo-0B').val();
		periodo_1 = $('#Periodo-1B').val();
		periodo_2 = $('#Periodo-2B').val();
		periodo_3 = $('#Periodo-3B').val();
		TipoVista = $('#TVistaB').val();

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + "&sede=" + sede + "&facultad=" + facultad + "&campus=" + campus;
		document.location.target = '_blank';
		document.location.href = 'data/06_exportar.jsp' + param;
	}

</script>
<!-- END SCRIPTS -->

<!-- BEGIN FOOTER -->
<%@ include file="inc/footer.jsp" %>
<!-- END FOOTER -->