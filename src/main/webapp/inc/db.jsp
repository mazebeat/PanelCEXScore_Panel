<%@ page import="java.sql.*,java.util.*,java.io.*, cl.intelidata.tools.*"%>
<%

String fileName = "C:\\Users\\Maze\\Google Drive\\Intelidata\\Gemini's\\2015\\1231\\panelExperiencia.properties";

DatabaseTools dbtools = new DatabaseTools();
dbtools.connectDB(fileName); 

// Connection conn = dbtools.getConexion();
// try{
// 	Class.forName("com.mysql.jdbc.Driver").newInstance();
// }catch (Exception ex){
// 	out.println(ex.toString());
// }
// String xDb_Conn_Str = "";
// String xDb_User = "";
// String xDb_Pass = "";
     
// //String fileName = "/apps/Umayor/panelExperiencia.properties";
// //String fullName = application.getRealPath(fileName);
// String fileName = "C:\\Users\\Maze\\Google Drive\\Intelidata\\Gemini's\\2015\\1231\\panelExperiencia.properties";
// String fullName = fileName;

// //System.out.println(fullName);

// try {
//       Properties p = new Properties();
//       p.clear();
//       p.load(new java.io.FileInputStream(fullName)); 
//       xDb_Conn_Str = p.getProperty("xDb_Conn_Str", xDb_Conn_Str);
//       xDb_User = p.getProperty("xDb_User", xDb_User);
// 	  xDb_Pass = p.getProperty("xDb_Pass", xDb_Pass);

// 		System.out.println(xDb_Conn_Str);
// 		System.out.println(xDb_User);
// 		System.out.println(xDb_Pass);
	  
//    } catch (FileNotFoundException e) {
//       System.out.println("File not found: " + fileName );
//    } catch (Exception e) {
//      System.out.println(e);
//   }

// Connection conn = null;
// try{
// 	conn = DriverManager.getConnection(xDb_Conn_Str,xDb_User,xDb_Pass);
// }catch (SQLException ex){
// 	out.println(ex.toString());
// }

final int ewAllowAdd = 1;
final int ewAllowDelete = 2;
final int ewAllowEdit = 4;
final int ewAllowView = 8;
final int ewAllowList = 8;
final int ewAllowSearch = 8;
final int ewAllowAdmin = 16;

%>
