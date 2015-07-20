
<%
    String[] Periodos = (String[]) request.getAttribute("Periodos");
    String[] Periodos2 = (String[]) request.getAttribute("Periodos2");
%>
<div class="panel panel-warning">
    <div class="panel-heading" data-original-title>
        <h3 class="panel-title">
            <i class="icon-th"></i> Filtros
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

        <div class="row">
            <div class="col-md-4">
                <h6>Periodo Inicial&nbsp;</h6>
            </div>
            <div class="col-md-4">
                <h6>desde&nbsp;</h6>
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
            <div class="col-md-4">
                <h6>hasta&nbsp;</h6>
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
                </select>&nbsp;
                <button class="btn btn-default btn-xs"
                        onclick="javascript:FiltrarDatos('boton', '', '', '', '', '', '', '');">
                    <i class="fa fa-search"></i>
                </button>
            </div>
        </div>

        <div class="row">
            <div class="col-md-4">
                <h6>Periodo Comparar&nbsp;</h6>
            </div>
            <div class="col-md-4">
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
            <div class="col-md-4">
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

        <!-- 		<div class="row"> -->
        <!-- 			<div class="col-md-3"> -->
        <!-- 				<h6> -->
        <!-- 					Visualizaci&oacute;n&nbsp;<br>por: -->
        <!-- 				</h6> -->
        <!-- 			</div> -->
        <!-- 			<div class="col-md-3"> -->
        <!-- 				<h6 class="radio"> -->
        <!-- 					<label class="radio"><input type="radio" name="TVista" -->
        <!-- 						id="TVista" value="sede">Sede</label> -->
        <!-- 				</h6> -->
        <!-- 			</div> -->
        <!-- 			<div class="col-md-3"> -->
        <!-- 				<h6 class="radio"> -->

        <!-- 					<label class="radio"><input type="radio" name="TVista" -->
        <!-- 						id="TVista" value="facultad">Facultad</label> -->
        <!-- 				</h6> -->

        <!-- 			</div> -->
        <!-- 			<div class="col-md-3"> -->
        <!-- 				<h6 class="radio"> -->
        <!-- 					<label class="radio"><input type="radio" name="TVista" -->
        <!-- 						id="TVista" value="campus">Campus</label> -->
        <!-- 				</h6> -->
        <!-- 			</div> -->
        <!-- 		</div> -->
    </div>
</div>