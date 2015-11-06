package cl.intelidata.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.Text;

public class UsuarioServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(UsuarioServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public UsuarioServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String function = request.getParameter("fn");

		// HEADERS
		response.setDateHeader("Expires", 0); // date in the past
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // HTTP/1.1
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache"); // HTTP/1.0

		// SESSION
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(30 * 60);

		// BEGIN VALIDATE USER LOGIN
		if (ApiServlet.ValidateUserLogin(request, response, session)) {
			return;
		}
		// END VALIDATE USER LOGIN

		Locale locale = Locale.getDefault();
		response.setLocale(locale);

		try {
			if (function.equalsIgnoreCase("cp")) {
				request.getRequestDispatcher("datos.jsp").forward(request, response);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String function = request.getParameter("fn");

		HttpSession session = request.getSession();

		// BEGIN VALIDATE USER LOGIN
		if (ApiServlet.ValidateUserLogin(request, response, session)) {
			return;
		}
		// END VALIDATE USER LOGIN

		try {
			if (function.equalsIgnoreCase("cp")) {
				changePassword(request, response);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	public void changePassword(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.setAttribute("Clave_" + Text.ProyectoID00 + "_status", "");

		String message = "";
		String type = "warning";
		
		try {			

//			String tmpfld0 = null;
			String tmpfld1 = null;
			String tmpfld2 = null;
			String escapeString = "\\\\'";

			request.setCharacterEncoding("UTF-8");

			String tkey = (String) session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_UserID");

			if (tkey == null || tkey.length() == 0) {
				response.sendRedirect("panel.jsp");
				response.flushBuffer();
				return;
			}

			// Get action
			String a = request.getParameter("action");
			if (a == null || a.length() == 0) {
				a = "I"; // Display with input box
			}

			// Get fields from form
			String x_passwordN0 = "";
			String x_passwordN1 = "";
			String x_passwordN2 = "";

			// Open Connection to the database
			DatabaseTools dbtools = new DatabaseTools();
			if (!dbtools.connectDB()) {
				request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">"
						+ "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">"
						+ "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + "Error al conectar con la BBDD!.." + "</div>"
						+ "</div>" + "</div>");
				request.getRequestDispatcher("datos.jsp").forward(request, response);
				return;
			}

			Statement stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = null;

			session.setAttribute("Clave_" + Text.ProyectoID00 + "_status", "Paso 01 - " + a);
			
			if (a.equals("U")) {// Update
				session.setAttribute("Clave_" + Text.ProyectoID00 + "_status", "Paso 02");

				// Get fields from form
				if (request.getParameter("passwordN0") != null) {
					x_passwordN0 = (String) request.getParameter("passwordN0");
				} else {
					x_passwordN0 = "jasdnvksnasdlkvnadknfl";
				}
				if (request.getParameter("passwordN1") != null) {
					x_passwordN1 = (String) request.getParameter("passwordN1");
				} else {
					x_passwordN1 = "sdfkjfksgfdlkf";
				}
				if (request.getParameter("passwordN2") != null) {
					x_passwordN2 = (String) request.getParameter("passwordN2");
				} else {
					x_passwordN2 = "dfmsadsksdkdsslwek";
				}

				// Open record
				tkey = "" + tkey.replaceAll("'", escapeString) + "";

				String passwd2 = "";
				MessageDigest alg = MessageDigest.getInstance("MD5");

				passwd2 = x_passwordN0;
				alg.reset();
				alg.update(passwd2.getBytes());
				byte[] digest = alg.digest();
				StringBuffer hashedpasswd = new StringBuffer();
				String hx;
				for (int i = 0; i < digest.length; i++) {
					hx = Integer.toHexString(0xFF & digest[i]);
					// 0x03 is equal to 0x3, but we need 0x03 for our md5sum
					if (hx.length() == 1) {
						hx = "0" + hx;
					}
					hashedpasswd.append(hx);
				}
				x_passwordN0 = hashedpasswd.toString();

				String strsql = "SELECT * FROM cs_usuarios WHERE id_usuario=" + tkey + " and pwdusuario= '" + x_passwordN0 + "'";
				session.setAttribute("Clave_" + Text.ProyectoID00 + "_status", strsql);
				rs = stmt.executeQuery(strsql);
				
				if (!rs.next()) {
					message = "La clave actual no coincide!..";
				} else {
					int pasos = 0;

					tmpfld1 = ((String) x_passwordN1).trim();
					if (tmpfld1.trim().length() > 0) {
						pasos = pasos + 1;
					}

					tmpfld2 = ((String) x_passwordN2).trim();
					if (tmpfld2.trim().length() > 0) {
						pasos = pasos + 1;
					}

					if (tmpfld1.equals(tmpfld2) && pasos == 2) {

						passwd2 = x_passwordN1;
						alg.reset();
						alg.update(passwd2.getBytes());
						digest = alg.digest();
						hashedpasswd = new StringBuffer();
						for (int i = 0; i < digest.length; i++) {
							hx = Integer.toHexString(0xFF & digest[i]);
							// 0x03 is equal to 0x3, but we need 0x03 for our
							// md5sum
							if (hx.length() == 1) {
								hx = "0" + hx;
							}
							hashedpasswd.append(hx);
						}
						x_passwordN1 = hashedpasswd.toString();

						rs.updateString("pwdusuario", x_passwordN1);
						rs.updateRow();

						message = "Datos Actualizados!..";
						type = "success";
					} else {
						message = "Las claves no coinciden!..";
					}			
				}
				
				request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">"
						+ "<div class=\"text-left alert alert-" + type + "\" style=\"text-align: left;\">"
						+ "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>" + message + "</div>" + "</div>"
						+ "</div>");

				request.getRequestDispatcher("datos.jsp").forward(request, response);
				
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static String Encripta(String passwd2) {
		MessageDigest algorith;
		StringBuffer hashedpasswd = null;
		try {
			algorith = MessageDigest.getInstance("MD5");

			algorith.reset();
			algorith.update(passwd2.getBytes());

			byte[] digest = algorith.digest();

			hashedpasswd = new StringBuffer();
			String hx;
			for (int i = 0; i < digest.length; i++) {
				hx = Integer.toHexString(0xFF & digest[i]);
				// 0x03 is equal to 0x3, but we need 0x03 for our md5sum
				if (hx.length() == 1) {
					hx = "0" + hx;
				}
				hashedpasswd.append(hx);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}

		return hashedpasswd.toString();
	}

}
