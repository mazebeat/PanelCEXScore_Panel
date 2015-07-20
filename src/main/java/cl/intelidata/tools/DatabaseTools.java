package cl.intelidata.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTools {

	private Connection conexion;
	private String bd;
	private String user;
	private String password;
	private String host;
	private String server;

	public DatabaseTools() {
		this.conexion = null;
		this.bd = "javamysql";
		this.user = "test";
		this.password = "123";
		this.host = "localhost";
		this.server = "jdbc:mysql://" + host + "/" + bd;
	}

	public void connectDB(String fileName) {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

		try {
			PropertiesTools prop = new PropertiesTools(fileName);
			this.setServer(prop.getProperties().getProperty("xDb_Conn_Str"));
			this.setUser(prop.getProperties().getProperty("xDb_User"));
			this.setPassword(prop.getProperties().getProperty("xDb_Pass"));

			Class.forName("com.mysql.jdbc.Driver");
			this.setConexion(DriverManager.getConnection(this.getServer(), this.getUser(), this.getPassword()));

			System.out.println("Conexi�n a base de datos " + server + " ... OK");
		} catch (ClassNotFoundException ex) {
			System.out.println("Error cargando el Driver MySQL JDBC ... FAIL");
		} catch (SQLException ex) {
			System.out.println("Imposible realizar conexion con " + server + " ... FAIL");
		}
	}

	public ResultSet exeQuery(String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);

			return rs;
		} catch (SQLException ex) {
			System.out.println("Imposible realizar consulta ... FAIL");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {
				}
			}
		}

		return rs;
	}
	
	public int exeUpdate(String sql) {
		Statement stmt = null;
		int response = 0;
		try {
			stmt = this.getConexion().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			response = stmt.executeUpdate(sql);

			return response;
		} catch (SQLException ex) {
			System.out.println("Imposible realizar consulta ... FAIL");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {
				}
			}
		}

		return response;
	}

	public void disconnectDB() {
		try {
			if (!this.getConexion().isClosed() || this.getConexion() != null) {
				try {
					this.getConexion().close();
					System.out.println("Cerrar conexion con " + server + " ... OK");
				} catch (SQLException ignore) {
				}
			}
		} catch (SQLException ex) {
			System.out.println("Imposible cerrar conexion ... FAIL");
			// Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
			// ex);
		}
	}

	public Connection getConexion() {
		return conexion;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	public String getBd() {
		return bd;
	}

	public void setBd(String bd) {
		this.bd = bd;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
}