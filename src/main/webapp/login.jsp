<%@ include file="inc/header.jsp"%>
<%
	response.setDateHeader("Expires", 0); // date in the past
	response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1 
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.addHeader("Pragma", "no-cache"); // HTTP/1.0
%>
<script src="js/ew.js"></script>
<script type="text/javascript"></script>
<div class="row">
	<div class="col-center-block col-md-8">
		<img src="img/customertrigger.png"
			class="img-responsive logo center-block" alt="">
	</div>

	<div class="col-md-12 login-header text-header text-center">
		<h1>Panel Customer EX Score</h1>
	</div>
</div>

${message}

<div class="row">
	<div class="col-center-block col-md-4 login-box">
		<form class="" action="LoginServlet" method="POST">
			<fieldset>
				<legend>Inicio de Sesi&oacute;n</legend>
				<div class="form-group">
					<input type="hidden" id="remember" />
				</div>
				<div class="form-group">
					<label for="username">Usuario</label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-user"></i>
						</div>
						<input type="text" class="form-control" name="username"
							id="username" placeholder="Usuario">
					</div>
				</div>
				<div class="form-group">
					<label for="password">Contrase&ntilde;a</label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-lock"></i>
						</div>
						<input type="password" class="form-control" name="password"
							id="password" placeholder="Contrase&ntilde;a">
					</div>
				</div>
				<button type="submit" id="asasd" class="btn btn-primary center-block">Ingresar</button>
			</fieldset>
		</form>
	</div>
	<!--/span-->
</div>
<!--/row-->

<%@ include file="inc/footer.jsp"%>