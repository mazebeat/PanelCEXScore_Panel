<div class="row">
	<div class="col-md-12">
		<div class="row">
			<div class="col-md-4 pull-left">
				<a class="brand img-responsive" href="index.jsp"></a>
			</div>
			<div class="col-md-4" style="margin-bottom: 10px;">
				<p class="text-center">
					<img src="img/customertrigger.png" class="img-responsive logo" alt="">
				</p>
			</div>
			<div class="col-md-4 pull-right">
				<div class="btn-group pull-right">
					<a class="btn btn-warning dropdown-toggle" data-toggle="dropdown" href="#">
						<i class="icon-user"></i><span class="hidden-phone"> 
						<%= session.getAttribute("Panel_" + cl.intelidata.conf.Text.ProyectoID00 + "_status_User") %></span> <span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<% 
							int ewCurIdxL = ((Integer) session.getAttribute("Panel_" + cl.intelidata.conf.Text.ProyectoID00 + "_status_UserLevel")).intValue();
							if (ewCurIdxL > 0) { // system administrator 
						%>
						<li><a href="datos.jsp">Contrase&ntilde;a</a></li>
						<% } %>
						<li class="divider"></li>
						<li><a href="panel.jsp">Panel</a></li>
						<% if (ewCurIdxL == 1 || ewCurIdxL == 4) { // system administrator %>
						<li><a href="usuarios.jsp">Usuarios</a></li>
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