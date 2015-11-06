<div class="panel panel-warning">
	<div class="panel-heading" data-original-title>
		<h3 class="panel-title">
			<i class="fa fa-filter fa-fw"></i>Filtros
		</h3>
	</div>
	<div class="panel-body">
		<div class="row">
			<input type="hidden" name="Periodo-0B" id="Periodo-0B">
			<input type="hidden" name="Periodo-1B" id="Periodo-1B">
			<input type="hidden" name="Periodo-2B" id="Periodo-2B">
			<input type="hidden" name="Periodo-3B" id="Periodo-3B">
			<input type="hidden" name="TVistaB" id="TVistaB">
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
							out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i] + "</option>");
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
								out.print("<option selected value='" + Periodos2[i] + "'>" + Periodos[i] + "</option>");
							} else {
								out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i] + "</option>");
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
							out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i] + "</option>");
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
							out.print("<option value='" + Periodos2[i] + "'>" + Periodos[i] + "</option>");
						}
					%>
				</select>
			</div>
		</div>

		<button class="btn btn-default btn-xs pull-right" onclick="javascript:FiltrarDatos('boton', '', '', '', '', '', '', '');">
			<i class="fa fa-search fa-fw"></i>Filtrar
		</button>
	</div>
</div>