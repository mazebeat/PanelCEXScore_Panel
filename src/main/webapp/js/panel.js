/**
 *
 */


FusionCharts.setCurrentRenderer('javascript');

//FiltrarDatos();
FiltrarDatos('nav', '', '', '', '', '<%=ultimo_periodo_f%>', '', '');

/**
 *
 * COMENTARIOS
 */
function Comentarios(sede, facultad, campus)
{
	var param;
	var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista;
	periodo_0 = document.getElementById('Periodo-0B').value;
	periodo_1 = document.getElementById('Periodo-1B').value;
	periodo_2 = document.getElementById('Periodo-2B').value;
	periodo_3 = document.getElementById('Periodo-3B').value;

	param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=' + campus;

	document.getElementById('body-modalT').innerHTML = "<button type='button' class='close' data-dismiss='modal'>�</button><h3>Lo que dice la gente</h3>";
	cargarContenido('data/00_comentarios.jsp' + param,'body-modalC');
	$('#myModal').modal('show');
}

/**
 *
 * EW_SELECTOP2
 */
function EW_selectopt2(obj, value) {
	for (var i = obj.length-1; i>=0; i--) {
		if (obj.options[i].value.toUpperCase() == value.toUpperCase()) {
			return i;
		}
	}
	return 9999;
}

/**
 *
 * MENSAJERO
 */
function mensajero(txt){
	alert (txt);
	return false;
}

/**
 *
 * FILTRARDATOS2
 */
function FiltrarDatos2(periodo_0, periodo_1, periodo_2, periodo_3, sede, facultad, Titulo)
{
	document.getElementById("cuadros_sedes_facultad").style.display = "block";
	document.getElementById("titulo_sede_facultad").innerHTML = '<h3><i class="icon-th"></i>' + Titulo + '</h3>';

	imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('" + sede + "','" + facultad + "','');\">";
	imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('" + sede + "','" + facultad + "','');\"></span>";
	document.getElementById('exportar_sede_facultad').innerHTML = imgExcel + imgComen;

	//Santiago UMayor Grafico -> OK
	param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=grafico';
	myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_sede_facultad_id", "100%", "130", "0", "0");
	myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
	myChart.render("satisfaccion_sede_facultad");


	//Santiago UMayor Detalle -> OK
	param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=&mostrar_como=tabla';
	cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_sede_facultad_detalle');

	//Santiago UMayor Acumulado -> OK
	param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + sede + '&facultad=' + facultad + '&campus=';
	cargarContenido('data/01_acumulado.jsp' + param,'div_acum_sede_facultad');


	//Santiago UMayor Tendencia -> OK
	param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + sede + '&facultad=' + facultad + '&campus=';
	myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_sede_facultad_id", "100%", "200", "0", "0");
	myChart.setDataURL(escape('data/04_tendencia.jsp' + param));
	myChart.render("div_tendencia_sede_facultad");

}

/**
 *
 * FILTRARDATOS
 */
function FiltrarDatos(trigger, str_sede, str_facultad, str_campus, per_0, per_1, per_2, per_3)
{
	var param;
	var ValidarFiltros2 = true;
	var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista, newHTML;
	var periodo_0I = 0;
	var periodo_1I = 0;
	var periodo_2I = 0;
	var periodo_3I = 0;

	if (trigger == 'boton') {
		periodo_0 = document.getElementById('Periodo-0').value;
		periodo_1 = document.getElementById('Periodo-1').value;
		periodo_2 = document.getElementById('Periodo-2').value;
		periodo_3 = document.getElementById('Periodo-3').value;

		TipoVista = GetRadioValue(document.getElementsByName('TVista'));

		periodo_0I = EW_selectopt2(document.getElementById('Periodo-0'),document.getElementById('Periodo-0').value);
		periodo_1I = EW_selectopt2(document.getElementById('Periodo-1'),document.getElementById('Periodo-1').value);
		periodo_2I = EW_selectopt2(document.getElementById('Periodo-2'),document.getElementById('Periodo-2').value);
		periodo_3I = EW_selectopt2(document.getElementById('Periodo-3'),document.getElementById('Periodo-3').value);

		Dif_Actual = periodo_0I - periodo_1I ;
		Dif_Compara = periodo_2I - periodo_3I;

		if (periodo_0I > 0 && periodo_0I < periodo_1I) ValidarFiltros2 = mensajero("Periodo Inicial: Desde no puede ser mayor que Periodo Inicial: Hasta");

		else if (periodo_0I == 0 && (periodo_2I > 0 || periodo_3I > 0)) ValidarFiltros2 = mensajero("Periodo Inicial: Desde no puede ser vac\u00edo");

		else if (periodo_2I < periodo_3I) ValidarFiltros2 = mensajero("Periodo Comparar: Desde no puede ser mayor que Periodo Comparar: Hasta");

		else if (periodo_3I > 0 && periodo_3I <= periodo_0I) ValidarFiltros2 = mensajero("Periodo Comparar: Hasta no puede ser mayor o igual que Periodo Inicial: Desde");

		else if (periodo_2I > 0 && Dif_Actual != Dif_Compara)  ValidarFiltros2 = mensajero("Los Periodos a comparar no poseen la misma cantidad de meses");
	} else {
		periodo_0 = '';
		periodo_1 = per_1;
		periodo_2 = '';
		periodo_3 = '';
		TipoVista = 'sede';
	}

	if (ValidarFiltros2) {
		document.getElementById('Periodo-0B').value = periodo_0;
		document.getElementById('Periodo-1B').value = periodo_1;
		document.getElementById('Periodo-2B').value = periodo_2;
		document.getElementById('Periodo-3B').value = periodo_3;

		imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('','','');\">";
		imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment' onclick=\"javascript:Comentarios('','','');\"></span>";
		document.getElementById('exportar_general').innerHTML = imgExcel + imgComen;

		imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('Division Santiago','','');\">";
		imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('Division Santiago','','');\"></span>";
		document.getElementById('exportar_sede_santiago').innerHTML = imgExcel + imgComen;

		imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('Division Temuco','','');\">";
		imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('Division Temuco','','');\"></span>";
		document.getElementById('exportar_sede_temuco').innerHTML = imgExcel + imgComen;



		//General GE grafico -> OK
		param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=todas&mostrar_como=grafico';
		myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_GE_id", "100%", "130", "0", "0");
		myChart.setDataURL(escape('data/03_benchmark.jsp' + param));
		myChart.render("satisfaccion_GE");

		//General GE detalle -> OK
		param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=Todas&mostrar_como=tabla';
		cargarContenido('data/03_benchmark.jsp' + param,'satisfaccion_GE_detalle');

		//General UMayor Grafico -> OK
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=&mostrar_como=grafico';
//		alert(param);
		myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor0_id", "100%", "130", "0", "0");
		myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
		myChart.render("satisfaccion_mayor0");

		//General UMayor Detalle -> OK
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=&mostrar_como=tabla';
		cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_mayor0_detalle');

		//General UMayor Acumulado -> OK
		param = '?currTime=' + getTimeForURL() + '&anio=2014&anio2=2015&sede=&facultad=&campus=';
		cargarContenido('data/01_acumulado.jsp' + param,'div_acum_global');

		if (TipoVista == 'sede') {
			document.getElementById("cuadros_sedes").style.display = "block";
			document.getElementById("cuadros_sedes_facultad").style.display = "none";
			<%
			for (row_tmp = 0; row_tmp < facultad_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_facultad_" + row_tmp + "').style.display = 'none';" + "\n");
			}

			for (row_tmp = 0; row_tmp < campus_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_campus_" + row_tmp + "').style.display = 'none';" + "\n");
			}
			%>

			//Santiago GE Grafico -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n Santiago') + '&mostrar_como=grafico';
			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_santiago_GE_id", "100%", "130", "0", "0");
			myChart.setDataURL(escape('data/03_benchmark.jsp' + param));
			myChart.render("satisfaccion_mayor_santiago_GE");

			//Santiago GE Detalle -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n Santiago') + '&mostrar_como=tabla';
			cargarContenido('data/03_benchmark.jsp' + param,'satisfaccion_mayor_santiago_GE_detalle');

			//Santiago UMayor Grafico -> OK
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=&mostrar_como=grafico';
			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_santiago_id", "100%", "130", "0", "0");
			myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
			myChart.render("satisfaccion_mayor_santiago");

			//Santiago UMayor Detalle -> OK
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=&mostrar_como=tabla';
			cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_mayor_santiago_detalle');

			//Santiago UMayor Acumulado -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=';
			cargarContenido('data/01_acumulado.jsp' + param,'div_acum_division_santiago');

			//Santiago UMayor Tendencia -> OK
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=';
			myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_division_santiago_id", "100%", "200", "0", "0");
			myChart.setDataURL(escape('data/04_tendencia.jsp' + param));
			myChart.render("div_tendencia_division_santiago");

			//Lista de Facultades 01
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=&display_desde=0&display_hasta=6';
//			document.location.href = 'data/05_facultades.jsp' + param;
			cargarContenido('data/05_facultades.jsp' + param,'fac_santiago_list01');

			//Lista de Facultades 02
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Santiago') + '&facultad=&campus=&display_desde=7&display_hasta=100';
//			document.location.href = 'data/05_facultades.jsp' + param;
			cargarContenido('data/05_facultades.jsp' + param,'fac_santiago_list02');


			///////TEMUCO

			//temuco GE Grafico -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n temuco') + '&mostrar_como=grafico';
			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_temuco_GE_id", "100%", "130", "0", "0");
			myChart.setDataURL(escape('data/03_benchmark.jsp' + param));
			myChart.render("satisfaccion_mayor_temuco_GE");

			//temuco GE Detalle -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n temuco') + '&mostrar_como=tabla';
			cargarContenido('data/03_benchmark.jsp' + param,'satisfaccion_mayor_temuco_GE_detalle');

			//temuco UMayor Grafico
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n temuco') + '&facultad=&campus=&mostrar_como=grafico';
			myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_mayor_temuco_id", "100%", "130", "0", "0");
			myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
			myChart.render("satisfaccion_mayor_temuco");

			//temuco UMayor Detalle -> OK
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n temuco') + '&facultad=&campus=&mostrar_como=tabla';
			cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_mayor_temuco_detalle');

			//temuco UMayor Acumulado -> OK
			param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=' + Limpia_String('Divisi�n temuco') + '&facultad=&campus=';
			cargarContenido('data/01_acumulado.jsp' + param,'div_acum_division_temuco');

			//temuco UMayor Tendencia -> OK
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n temuco') + '&facultad=&campus=';
			myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_division_temuco_id", "100%", "200", "0", "0");
			myChart.setDataURL(escape('data/04_tendencia.jsp' + param));
			myChart.render("div_tendencia_division_temuco");

			//Lista de Facultades 01
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Temuco') + '&facultad=&campus=&display_desde=0&display_hasta=5';
//			document.location.href = 'data/05_facultades.jsp' + param;
			cargarContenido('data/05_facultades.jsp' + param,'fac_temuco_list01');

			//Lista de Facultades 02
			param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=' + Limpia_String('Divisi�n Temuco') + '&facultad=&campus=&display_desde=6&display_hasta=100';
//			document.location.href = 'data/05_facultades.jsp' + param;
			cargarContenido('data/05_facultades.jsp' + param,'fac_temuco_list02');
		}

		if (TipoVista == 'facultad') {
			document.getElementById("cuadros_sedes").style.display = "none";
			document.getElementById("cuadros_sedes_facultad").style.display = "none";
			<%
			for (row_tmp = 0; row_tmp < facultad_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_facultad_" + row_tmp + "').style.display = 'block';" + "\n");
			}

			for (row_tmp = 0; row_tmp < campus_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_campus_" + row_tmp + "').style.display = 'none';" + "\n");
			}

			facultad_zz = 0;
			row_tmp = 0;

			while (facultad_zz < Fila_Facultad) {
			%>
				imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('','<%=Limpia_String(Array_Facultades[facultad_zz])%>','');\">";
				imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('','<%=Limpia_String(Array_Facultades[facultad_zz])%>','');\"></span>";
				document.getElementById('exportar_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>').innerHTML = imgExcel + imgComen;

				//Santiago UMayor Grafico -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=<%=Limpia_String(Array_Facultades[facultad_zz])%>&campus=&mostrar_como=grafico';
				myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>_id", "100%", "130", "0", "0");
				myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
				myChart.render("satisfaccion_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>");

				//Santiago UMayor Detalle -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=<%=Limpia_String(Array_Facultades[facultad_zz])%>&campus=&mostrar_como=tabla';
				cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>_detalle');

				//Santiago UMayor Acumulado -> OK
				param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=&facultad=<%=Limpia_String(Array_Facultades[facultad_zz])%>&campus=';
				cargarContenido('data/01_acumulado.jsp' + param,'div_acum_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>');


				//Santiago UMayor Tendencia -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=<%=Limpia_String(Array_Facultades[facultad_zz])%>&campus=';
//					document.location.href = 'data/04_tendencia.jsp' + param;
					myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>_id", "100%", "200", "0", "0");
				myChart.setDataURL(escape('data/04_tendencia.jsp' + param));
				myChart.render("div_tendencia_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>");
			<%
				facultad_zz += 1;
			}
			%>
		}

		if (TipoVista == 'campus') {
			document.getElementById("cuadros_sedes").style.display = "none";
			document.getElementById("cuadros_sedes_facultad").style.display = "none";
			<%
			for (row_tmp = 0; row_tmp < facultad_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_facultad_" + row_tmp + "').style.display = 'none';" + "\n");
			}

			for (row_tmp = 0; row_tmp < campus_zz_grupo; row_tmp++)
			{
				out.println("document.getElementById('cuadros_campus_" + row_tmp + "').style.display = 'block';" + "\n");
			}

			campus_zz = 0;
			row_tmp = 0;

			while (campus_zz < Fila_Campus) {
			%>
				imgExcel = "UMayor&nbsp;&nbsp;<img title='Exportar Encuestas' src='img/excel-file.gif' border='0' onclick=\"javascript:Exportar('','','<%=Limpia_String(Array_Campus[campus_zz])%>');\">";
				imgComen = "<span title='Ver Comentarios' class='icon icon-color icon-comment mySpan' onclick=\"javascript:Comentarios('','','<%=Limpia_String(Array_Campus[campus_zz])%>');\"></>";
				document.getElementById('exportar_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>').innerHTML = imgExcel + imgComen;

				//Santiago UMayor Grafico -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=<%=Limpia_String(Array_Campus[campus_zz])%>&mostrar_como=grafico';
				myChart = new FusionCharts("charts/MSCombi2D.swf", "satisfaccion_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>_id", "100%", "130", "0", "0");
				myChart.setDataURL(escape('data/02_periodo_indicadores.jsp' + param));
				myChart.render("satisfaccion_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>");

				//Santiago UMayor Detalle -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=<%=Limpia_String(Array_Campus[campus_zz])%>&mostrar_como=tabla';
				cargarContenido('data/02_periodo_indicadores.jsp' + param,'satisfaccion_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>_detalle');

				//Santiago UMayor Acumulado -> OK
				param = '?currTime=' + getTimeForURL() + '&anio=2014&sede=&facultad=&campus=<%=Limpia_String(Array_Campus[campus_zz])%>';
				cargarContenido('data/01_acumulado.jsp' + param,'div_acum_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>');


				//Santiago UMayor Tendencia -> OK
				param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3 + '&sede=&facultad=&campus=<%=Limpia_String(Array_Campus[campus_zz])%>';
				myChart = new FusionCharts("charts/MSLine.swf", "div_tendencia_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>_id", "100%", "200", "0", "0");
				myChart.setDataURL(escape('data/04_tendencia.jsp' + param));
				myChart.render("div_tendencia_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>");
<%campus_zz += 1;
			}%>
	}
		}
	}

	/**
	 *
	 * EXPORTAR
	 */
	function Exportar(sede, facultad, campus) {
		var periodo_0, periodo_1, periodo_2, periodo_3, TipoVista;
		periodo_0 = document.getElementById('Periodo-0B').value;
		periodo_1 = document.getElementById('Periodo-1B').value;
		periodo_2 = document.getElementById('Periodo-2B').value;
		periodo_3 = document.getElementById('Periodo-3B').value;
		TipoVista = document.getElementById('TVistaB').value;

		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0
				+ '&periodo_1=' + periodo_1 + '&periodo_2=' + periodo_2
				+ '&periodo_3=' + periodo_3 + "&sede=" + sede + "&facultad="
				+ facultad + "&campus=" + campus;
		document.location.target = '_blank';
		document.location.href = 'data/06_exportar.jsp' + param;
	}

	function showProgress() {
	//General Muestra Texto Inicial -> OK
	if (periodo_2I > 0) {
		document.getElementById('div_avances').innerHTML = '<div class="col-center-block col-md-4"><div id="myChart01z" style="z-index:100">&nbsp;</div><div id="myChart01z_detallle">&nbsp;</div></div><div class="col-center-block col-md-6"><br><div id="myChart01" style="z-index:100">&nbsp;</div><div id="myChart01_detallle">&nbsp;</div></div>';
	}
	else {
		document.getElementById('div_avances').innerHTML = '<div class="col-center-block col-xs-6 col-sm-6 col-md-8"><div id="myChart01z" style="z-index:100">&nbsp;</div><div id="myChart01z_detallle">&nbsp;</div></div>';
	}

	param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_0 + '&periodo_1=' + periodo_1;
	// + '&periodo_2=' + periodo_2 + '&periodo_3=' + periodo_3;
	cargarContenido('data/00_muestra.jsp' + param + '&tipoD=0&Titulo=Inicial','myChart01z_detallle');

	//General Muestra Grafico Inicial
	var myChart;
	var anchoGraf = 250;
	if (periodo_2I > 0) {
		anchoGraf = 200;
	}

	//General Muestra Grafico Inicial -> OK
	myChart = new FusionCharts("charts/HLinearGauge.swf", "myChart01z_id", anchoGraf, "45", "0", "0");
	myChart.setDataURL(escape('data/00_muestra.jsp' + param + '&tipoD=1&Titulo=Inicial'));
	myChart.render("myChart01z");

	if (periodo_2I > 0) {
		param = '?currTime=' + getTimeForURL() + '&periodo_0=' + periodo_2 + '&periodo_1=' + periodo_3;
		cargarContenido('data/00_muestra.jsp' + param + '&tipoD=0&Titulo=Comparar','myChart01_detallle');

		//General Muestra Grafico Inicial -> OK
		myChart = new FusionCharts("charts/HLinearGauge.swf", "myChart01_id", anchoGraf, "45", "0", "0");
		myChart.setDataURL(escape('data/00_muestra.jsp' + param + '&tipoD=1&Titulo=Comparar'));
		myChart.render("myChart01");
	}
}