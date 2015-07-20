package cl.intelidata.servlet;

import cl.intelidata.conf.Text;
import cl.intelidata.tools.DatabaseTools;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public LoginServlet() {
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
		String action = request.getParameter("action");

		Locale locale = Locale.getDefault();
		response.setLocale(locale);

		request.setAttribute("ewAllowAdd", 1);
		request.setAttribute("ewAllowDelete", 2);
		request.setAttribute("ewAllowEdit", 4);
		request.setAttribute("ewAllowView", 8);
		request.setAttribute("ewAllowList", 8);
		request.setAttribute("ewAllowSearch", 8);
		request.setAttribute("ewAllowAdmin", 16);

		request.getRequestDispatcher("login.jsp").forward(request, response);
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
		boolean validpwd = false;

		String sql = "";
		String escapeString = "\\\\'";
		String ClaveDura = "5a1ed03731bbdf75efe8b9e8516ad815"; // CustomeR

		HttpSession session = request.getSession();
		String userid = request.getParameter("username");
		String passwd = request.getParameter("password");
		String passwd2 = passwd;

		if (userid != null && userid.length() > 0 && passwd != null && passwd.length() > 0) {

			StringBuffer hashedpasswd = hashedPassword(passwd2);

			String fileName = "C:\\Users\\Maze\\Google Drive\\Intelidata\\Gemini's\\2015\\1231\\panelExperiencia.properties";

			DatabaseTools dbtools = new DatabaseTools();
			dbtools.connectDB(fileName);

			Statement stmt = null;

			try {
				stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

				if (("admin".toUpperCase().equals(userid.toUpperCase())) && (ClaveDura.equals(hashedpasswd.toString()))) {
					session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_User", "admin");
					session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserID", 0);
					session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserLevel", new Integer(-1)); // System

					// administrator
					sql = "INSERT INTO cs_usuarios_log (idusuario) VALUES (0)";
					stmt.executeUpdate(sql);
					validpwd = true;
					System.out.println("Administrator Sign In");
				}

				if (!validpwd) {
					ResultSet rs = null;

					System.out.println("Serching user");
					sql = "SELECT * FROM cs_usuarios WHERE usuario = '" + userid.replaceAll("'", escapeString) + "' and activo = 1";
					rs = stmt.executeQuery(sql);

					if (rs.next()) {
						if (rs.getString("pwdusuario").equals(hashedpasswd.toString())) {
							System.out.println("User found: " + rs.getString("usuario"));
							session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_User", rs.getString("usuario"));
							session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserID", rs.getString("id_usuario"));
							session.setAttribute("Panel_" + Text.ProyectoID00 + "_status_UserLevel", new Integer(rs.getInt("id_perfil")));

							sql = "INSERT INTO cs_usuarios_log (idusuario) VALUES (" + rs.getString("id_usuario") + ")";
							stmt.executeUpdate(sql);

							rs.close();

							// sql =
							// "SELECT count(cliente_respuesta.id_cliente_respuesta) as Max_Reg_Rpta FROM cliente_respuesta";

							sql = "SELECT Max(cliente_respuesta.id_cliente_respuesta) as Max_Reg_Rpta FROM cliente_respuesta";
//							sql = "SELECT Max(clientes_respuesta.id_cliente_respuesta) as Max_Reg_Rpta FROM clientes_respuesta";
							rs = stmt.executeQuery(sql);
							if (rs.next()) {
								System.out.println("Max answers: " + rs.getString("Max_Reg_Rpta"));
								session.setAttribute("Panel_" + Text.ProyectoID00 + "_Max_Reg_Rpta", rs.getString("Max_Reg_Rpta"));
							}

							validpwd = true;
						}
					}
					rs.close();
					rs = null;
				
				}

				if (validpwd) {
					request.setAttribute("validpwd", validpwd);
					if (request.getParameter("remember") != null && ((String) request.getParameter("remember")).length() > 0) {
						Cookie cookie = new Cookie("Panel_" + Text.ProyectoID00 + "_userid", new String(userid));

						cookie.setMaxAge(365 * 24 * 60 * 60);

						response.addCookie(cookie);
					}
					session.setAttribute("Panel_" + Text.ProyectoID00 + "_status", "login");
					request.setAttribute("status_User", session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_User"));

					String context = request.getContextPath();
					response.sendRedirect(context + "PanelServlet");

				} else {
					System.out.println("User not found.");
					request.setAttribute("validpwd", validpwd);
					request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">" + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>"
					        + "Acceso denegado!.." + "</div>" + "</div>" + "</div>");

					request.getRequestDispatcher("login.jsp").forward(request, response);

				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException ignore) {
					}
				}
				dbtools.disconnectDB();
			}
		} else {
			validpwd = false;

			request.setAttribute("validpwd", false);
			request.setAttribute("message", "<div class=\"row\">" + "<div class=\"col-center-block col-md-4\">" + "<div class=\"text-left alert alert-warning\" style=\"text-align: left;\">" + "<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>"
			        + "Ingrese su usuario y clave." + "</div>" + "</div>" + "</div>");

			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}

	/**
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private StringBuffer hashedPassword(String password) {
		try {
			MessageDigest alg = MessageDigest.getInstance("MD5");

			alg.reset();
			alg.update(password.getBytes());

			byte[] digest = alg.digest();

			StringBuffer hashedpasswd = new StringBuffer();
			String hx;
			for (int i = 0; i < digest.length; i++) {
				hx = Integer.toHexString(0xFF & digest[i]);
				if (hx.length() == 1) {
					hx = "0" + hx;
				}
				hashedpasswd.append(hx);
			}
			return hashedpasswd;

		} catch (NoSuchAlgorithmException nse) {
			System.out.println(nse.getMessage());
		}

		return null;

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

}
