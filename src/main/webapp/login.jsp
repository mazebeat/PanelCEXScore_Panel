<%@ include file="inc/header.jsp"%>
<script src="js/ew.js"></script>
<script type="text/javascript"></script>
<div class="row">
<!-- 	<div class="col-center-block col-md-8"> -->
<!-- 		<img src="http://www.customertrigger.com/wp-content/uploads/2015/05/logo-customer-trigger-large-2015.png" class="img-responsive logo center-block" alt=""> -->
<!-- 	</div> -->
	<div class="col-center-block col-md-6 login-header text-header text-center">
		<h1 style="text-align: center;">
			<img src="img/customertrigger-score.png" class="img-responsive logo center-block" alt="">
			<!-- <span style="color: #ff9900;"><b>Customer</b></span> <span style="color: #1e73be;">Experience SCORE</span> -->
		</h1>
	</div>
</div>

${message}

<div class="row">
	<div class="col-center-block col-md-4 login-box">
		<form class="" action="LoginServlet" method="POST">
			<fieldset>
				<legend>
					<i class="fa fa-sign-in fa-fw"></i>&nbsp;INICIO DE SESI&Oacute;N
				</legend>
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
				<button type="submit" class="btn btn-primary center-block">Ingresar</button>
			</fieldset>
		</form>
	</div>
	<!--/span-->
</div>
<!--/row-->

<%@ include file="inc/footer.jsp"%>