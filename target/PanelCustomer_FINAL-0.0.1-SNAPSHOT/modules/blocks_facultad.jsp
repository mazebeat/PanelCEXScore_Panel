<%
while (facultad_zz < Fila_Facultad) {
%>
<!--COMIENZO CUADROS X FACULTAD LINEA <%=facultad_zz_grupo%>-->
<div id="cuadros_facultad_<%=facultad_zz_grupo%>" style="display:none">
	<div class="row-fluid">
		<%
		row_tmp = 1;
		while (row_tmp <= 3 && ((facultad_zz_grupo * 3) + row_tmp) <= Fila_Facultad) {
		%>
		<!--Inicio cuadro facultad-->
		<div class="box span4">
			<div class="box-header2" data-original-title>
				<h3><i class="icon-th"></i><%=Array_Facultades[facultad_zz]%></h3>
			</div>

			<div class="box-content">
				<div class="row-fluid">
					<div class="span6">
						<div class="alert alert-info">
							<h4>Resultados Periodo</h4>
						</div>
						<div>
							<span class="label3 label-important3">
							<div id="exportar_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>">&nbsp;</div>
							</span>
						</div>
						<div align="center" class="box-content2" id="satisfaccion_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>">&nbsp;</div>
						<div align="center" class="box-content2" id="satisfaccion_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>_detalle">&nbsp;</div>
					</div>

	
					<div class="span6">
						<div class="alert alert-info">
							<h4>Resultados Acumulados</h4>
						</div>
						<div class="box-content2"
							id="div_acum_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>">&nbsp;</div>
						<div
							id="div_tendencia_<%=Limpia_String_Fac(Array_Facultades[facultad_zz])%>">&nbsp;</div>
					</div>
				</div>
			</div>
		</div>
		<!--Fin cuadro facultad-->
		<%
			facultad_zz += 1;
			row_tmp += 1;
		}
		%>
	</div>
</div>
<!--FIN CUADROS X FACULTAD LINEA <%=facultad_zz_grupo%>-->
<%
	facultad_zz_grupo += 1;
}
%>