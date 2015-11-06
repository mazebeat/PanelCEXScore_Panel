<%@ page session="true" buffer="16kb"%>
<%@ page contentType="text/html; charset=ISO-8859-1"%>
<%@ include file="inc/header2.jsp"%>
<script language="JavaScript" src="js/ew.js"></script>
<script language="JavaScript">
	function EW_checkMyForm(EW_this) {
		var pass0 = '';
		var pass1 = '';
		var pass2 = '';

		if (!EW_hasValue(EW_this.passwordN0, "PASSWORD")) {
			if (!EW_onError(EW_this, EW_this.passwordN0, "PASSWORD",
					"Ingrese Clave Anterior"))
				return false;
		}
		if (!EW_hasValue(EW_this.passwordN1, "PASSWORD")) {
			if (!EW_onError(EW_this, EW_this.passwordN1, "PASSWORD",
					"Ingrese Nueva Clave"))
				return false;
		}
		if (!EW_hasValue(EW_this.passwordN2, "PASSWORD")) {
			if (!EW_onError(EW_this, EW_this.passwordN2, "PASSWORD",
					"Ingrese Repita Nueva Clave"))
				return false;
		}
		pass0 = document.getElementById('passwordN0').value;
		pass1 = document.getElementById('passwordN1').value;
		pass2 = document.getElementById('passwordN2').value;

		if (pass1 != pass2) {
			if (!EW_onError(EW_this, EW_this.passwordN2, "PASSWORD",
					"Los claves no coinciden"))
				return false;
		}

		return true;
	}
</script>

<%@ include file="inc/header_cabecera.jsp"%>

${message}

<div class="row">
	<div class="col-center-block col-md-4 login-box">
		<form class="form-horizontal" action="UsuarioServlet" method="POST"
			onSubmit="return EW_checkMyForm(this);">
			<fieldset>
				<legend>
					<i class="fa fa-key fa-fw"></i>&nbsp;CAMBIO DE CONTRASE&Ntilde;A
				</legend>
				<div class="form-group">
					<input type="hidden" id="action" name="fn" value="cp" /> 
					<input type="hidden" id="action" name="action" value="U" />
				</div>
				<div class="form-group">
					<label for="passwordN0">Contrase&ntilde;a Anterior</label>

					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-lock"></i>
						</div>
						<input autofocus class="form-control" name="passwordN0"
							id="passwordN0" type="password" value="" />
					</div>
				</div>
				<div class="form-group">
					<label for="passwordN1">Nueva Contrase&ntilde;a</label>

					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-lock"></i>
						</div>
						<input class="form-control" name="passwordN1" id="passwordN1"
							type="password" value="" />
					</div>
				</div>
				<div class="form-group">
					<label for="passwordN2">Repita Nueva Contrase&ntilde;a</label>

					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-lock"></i>
						</div>
						<input class="form-control" name="passwordN2" id="passwordN2"
							type="password" value="" />
					</div>
				</div>
				<button type="submit" class="btn btn-primary center-block">Modificar</button>
			</fieldset>
		</form>
	</div>
</div>
<!--/row-->
<%@ include file="inc/footer.jsp"%>