<%
    while (campus_zz < Fila_Campus) {
%>
<!--COMIENZO CUADROS X CAMPUS LINEA <%=campus_zz_grupo%>-->
<div id="cuadros_campus_<%=campus_zz_grupo%>" style="display:none">
    <div class="row-fluid">
        <%
            row_tmp = 1;
            while (row_tmp <= 3 && ((campus_zz_grupo * 3) + row_tmp) <= Fila_Campus) {
        %>
        <!--Inicio cuadro facultad-->
        <div class="box span4">
            <div class="box-header2" data-original-title>
                <h2>
                    <i class="icon-th"></i><%=Array_Campus[campus_zz]%></h2>
            </div>

            <div class="box-content">
                <div class="row-fluid">
                    <div class="span6">
                        <div class="alert alert-info">
                            <h4>Resultados Periodo</h4>
                        </div>
                        <div>
                            <span class="label3 label-important3"><div id="exportar_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>">&nbsp;</div></span>
                        </div>
                        <div align="center" id="satisfaccion_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>">&nbsp;</div>
                        <div align="center" class="box-content2" id="satisfaccion_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>_detalle">&nbsp;</div>
                    </div>

                    <div class="span6">
                        <div class="alert alert-info"><h4>Resultados Acumulados</h4></div>
                        <div class="box-content2" id="div_acum_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>">&nbsp;</div>
                        <div id="div_tendencia_<%=Limpia_String_Fac(Array_Campus[campus_zz])%>">&nbsp;</div>
                    </div>
                </div>
            </div>
        </div>
        <!--Fin cuadro facultad-->
        <%
                campus_zz += 1;
                row_tmp += 1;
            }
        %>
    </div>
</div>
<!--FIN CUADROS X CAMPUS LINEA <%=campus_zz_grupo%>-->
<%
        campus_zz_grupo += 1;
    }
%>