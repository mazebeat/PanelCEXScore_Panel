package cl.intelidata.servlet;

import cl.intelidata.util.DatabaseTools;
import cl.intelidata.util.Text;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PanelServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(PanelServlet.class);
	public static int ID_CLIENTE;
	public static int ID_SECTOR;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public PanelServlet() {
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
	 * The doDelete method of the servlet. <br>
	 * <p/>
	 * This method is called when a HTTP delete request is received.
	 * <p/>
	 *
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 *
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * <p/>
	 * This method is called when a form has its tag value method equals to get.
	 * <p/>
	 *
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 *
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String      action  = request.getParameter("action");
		HttpSession session = request.getSession();

		// BEGIN VALIDATE USER LOGIN
		if (ApiServlet.ValidateUserLogin(request, response, session)) {
			return;
		}
		// END VALIDATE USER LOGIN

		ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());

		try {
			if (action != null) {
				if(action.toLowerCase().equalsIgnoreCase("visitbyaccount")) {
					visitsByAccount(request, response);
				}
			} else {
				doInit(request, response);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}


	}

	public void doInit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println("INIT PANEL");
		HttpSession session = request.getSession();

		// BEGIN VALIDATE USER LOGIN
		if (ApiServlet.ValidateUserLogin(request, response, session)) {
			return;
		}
		// END VALIDATE USER LOGIN

		ID_CLIENTE = Integer.parseInt(session.getAttribute("Panel_" + Text.ProyectoID00 + "_status_IdCliente").toString());

		// BEGIN SECURITY ------------------
		ApiServlet.ValidateUserSecurity(session);
		// END SECURITY ------------------

		// BEGIN VARS ------------------
		String[] periodos         = new String[100];
		String[] Array_Facultades = new String[200];
		String[] Array_Campus     = new String[200];
		String   ultimo_periodo   = "";
		String   ultimo_periodo_f = "";
		int      count            = 0;
		int      Fila_Facultad    = 0;
		int      Fila_Campus      = 0;
		// END VARS ------------------

		DatabaseTools dbtools = new DatabaseTools();
		dbtools.connectDB();

		ResultSet rs   = null;
		Statement stmt = null;

		try {
			// BEGIN GET PERIODS
			stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String sql = "SELECT DISTINCT periodo, anio, mes FROM cs_periodos ORDER BY periodo DESC, anio DESC, mes DESC";
			rs = stmt.executeQuery(sql);

			if (rs.last()) {
				rs.beforeFirst();

				while (rs.next()) {
					// long length = rs.getClob("periodo").length();

					if (ultimo_periodo_f.equals("")) {
						ultimo_periodo_f = rs.getString("periodo");
					}

					ultimo_periodo = rs.getString("Periodo");
					periodos[count] = ultimo_periodo;
					count++;
				}
			} else {
				periodos[0] = "2000-01";
			}
			// END GET PERIODS

			// BEGIN MOMENTS
			sql = "SELECT descripcion_momento FROM momento GROUP BY descripcion_momento order by id_momento";
			rs = stmt.executeQuery(sql);

			if (rs.last()) {
				rs.beforeFirst();

				while (rs.next()) {
					Array_Facultades[Fila_Facultad] = rs.getString("descripcion_momento");
					Fila_Facultad++;
				}
			} else {
				Array_Facultades[0] = "NN";
			}
			// END MOMENTS

			// BEGIN VISITS COUNT
			String visit_sql = "";
			visit_sql = "SELECT COUNT(id_visita) as total FROM visita WHERE id_cliente=" + ID_CLIENTE;

			rs = stmt.executeQuery(visit_sql);

			if (rs.last()) {
				rs.beforeFirst();

				while (rs.next()) {
					request.setAttribute("visitCount", rs.getInt("total"));
				}
			} else {
				request.setAttribute("visitCount", 0);
			}
			// END VISITS COUNT
		} catch (Exception ex) {
			System.out.println(ex.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {
					System.out.println(ignore.toString());
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {
					System.out.println(ignore.toString());
				}
			}
			dbtools.disconnectDB();
		}

		request.setAttribute("Periodos", periodos);
		request.setAttribute("Periodos2", periodos);
		request.setAttribute("Array_Facultades", Array_Facultades);
		request.setAttribute("Array_Campus", Array_Campus);
		request.setAttribute("ultimo_periodo", ultimo_periodo);
		request.setAttribute("ultimo_periodo_f", ultimo_periodo_f);
		request.setAttribute("Fila_Facultad", Fila_Facultad);
		request.setAttribute("Fila_Campus", Fila_Campus);

		System.gc();
		request.getRequestDispatcher("panel.jsp").forward(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * <p/>
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * <p/>
	 *
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 *
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPut method of the servlet. <br>
	 * <p/>
	 * This method is called when a HTTP put request is received.
	 * <p/>
	 *
	 * @param request  the request send by the client to the server
	 * @param response the response send by the server to the client
	 *
	 * @throws ServletException if an error occurred
	 * @throws IOException      if an error occurred
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Put your code here
	}

	/**
	 * Returns information about the servlet, such as author, version, and
	 * copyright.
	 * <p/>
	 *
	 * @return String information about this servlet
	 */
	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
	}

	/**
	 * Initialization of the servlet. <br>
	 * <p/>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

	public int visitsByAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int    idMomento = 0;
		String moment    = request.getParameter("moment");
		if (moment == null || moment == "") {
			idMomento = 0;
		} else {
			idMomento = Integer.parseInt(moment);
		}


		int           visit   = 0;
		DatabaseTools dbtools = new DatabaseTools();
		dbtools.connectDB();

		ResultSet rs   = null;
		Statement stmt = null;

		try {
			// BEGIN GET PERIODS
			stmt = dbtools.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// BEGIN VISITS COUNT
			String visit_sql = "";
			if (idMomento != 0) {
				visit_sql = "SELECT COUNT(id_visita) as total FROM visita WHERE id_cliente=" + ID_CLIENTE;
			} else {
				visit_sql = "SELECT COUNT(id_visita) as total FROM visita WHERE id_cliente=" + ID_CLIENTE + " AND id_momento=" + idMomento;
			}

			rs = stmt.executeQuery(visit_sql);

			if (rs.last()) {
				rs.beforeFirst();

				while (rs.next()) {
					visit = rs.getInt("total");
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {
					System.out.println(ignore.toString());
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {
					System.out.println(ignore.toString());
				}
			}
			dbtools.disconnectDB();
		}

		return visit;
		// END VISITS COUNT
	}

}
