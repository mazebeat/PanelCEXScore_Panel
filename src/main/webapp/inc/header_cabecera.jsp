<div class="row">
	<div class="col-md-12">
		<div class="row">
			<div class="col-md-3 pull-left">
				<a class="brand img-responsive" href="index.jsp"></a>
			</div>
			<div class="col-xs-12 col-md-6" style="margin-bottom: 10px;">
				<p class="text-center">
					<img src="img/customertrigger-score.png" class="img-responsive center-block logo" alt="">
				</p>
			</div>
			<div class="col-xs-12 col-md-3 pull-right">
				<div class="btn-group pull-right">
					<a class="btn btn-warning dropdown-toggle" data-toggle="dropdown" href="#">
						<i class="icon-user"></i><span class="hidden-phone"> 
						<%=session.getAttribute("Panel_" + cl.intelidata.util.Text.ProyectoID00 + "_status_User")%></span> <span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<%
						String status_UsserLevel = session.getAttribute("Panel_" + cl.intelidata.util.Text.ProyectoID00 + "_status_UserLevel").toString();
						int ewCurIdxL = Integer.parseInt(status_UsserLevel);
						if (ewCurIdxL > 0) { // system administrator
						%>
						<li><a href="UsuarioServlet?fn=cp">Contrase&ntilde;a</a></li>
						<% } %>
						<li class="divider"></li>
						<li><a href="PanelServlet">Panel</a></li>
						<% if (ewCurIdxL == 1 || ewCurIdxL == 4) { // system administrator %>
<!-- 						<li><a href="UsuarioServlet?function=users">Usuarios</a></li> -->
						<% } else if (ewCurIdxL == 1) { // system administrator	%>
						<li><a href="periodos.jsp">Periodos</a></li>
						<% } %>
						<li class="divider"></li>
						<li><a href="logout.jsp">Logout</a></li>
					</ul>
				</div>
				<!-- user dropdown ends -->
			</div>
		</div>
	</div>
</div>