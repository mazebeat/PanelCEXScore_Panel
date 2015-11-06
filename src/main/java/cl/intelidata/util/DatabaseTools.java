package cl.intelidata.util;

import org.apache.log4j.Logger;

import java.sql.*;

public class DatabaseTools {

	private static final Logger logger = Logger.getLogger(DatabaseTools.class);

	private Connection conexion;
	private String bd;
	private String username;
	private String password;
	private String host;
	private String server;

	public DatabaseTools() {
		this.conexion = null;
		this.bd = "";
		this.username = "";
		this.password = "";
		this.host = "";
		this.server = "jdbc:mysql://" + host + "/" + bd;
	}

	public boolean connectDB() {
		String archConf = "";

		try {
			archConf = "/apps/PanelCEXScore/config/PanelCEXScore.properties";

			if (!System.getProperty("file.separator").equals("/")) {
				archConf = System.getProperty("disco", "C:") + archConf;
			}

			return connectDB(archConf);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return false;
	}

	public boolean connectDB(String fileName) {
		boolean response = false;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			PropertiesTools prop = new PropertiesTools(fileName);
			this.setServer(prop.getProperties().getProperty("server"));
			this.setUsername(prop.getProperties().getProperty("username"));
			this.setPassword(prop.getProperties().getProperty("password"));

			Class.forName("com.mysql.jdbc.Driver");
			this.setConexion(DriverManager.getConnection(this.getServer(),
					this.getUsername(), this.getPassword()));
			System.out
					.println("Conexión a base de datos " + server + " --> OK");

			response = true;
		} catch (ClassNotFoundException ex) {
			logger.error("Error cargando el Driver MySQL JDBC --> FAIL");
		} catch (SQLException ex) {
			logger.error("Imposible realizar conexion con " + server
					+ " --> FAIL");
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

		return response;
	}

	public ResultSet exeQuery(String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.getConexion()
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);

			return rs;
		} catch (SQLException ex) {
			System.out.println("Imposible realizar consulta --> FAIL, "
					+ ex.toString());
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
			stmt = this.getConexion()
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			response = stmt.executeUpdate(sql);

			return response;
		} catch (SQLException ex) {
			// System.out.println("Imposible realizar consulta ... FAIL");
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
					System.out.println("Cerrando conexión " + server
							+ " --> OK");
				} catch (SQLException ignore) {
				}
			}
		} catch (SQLException ex) {
			logger.error("Imposible cerrar conexión --> FAIL");
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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