/*
	Author : Jayant Gupta
	Date : April 29, 2015
	This is a JAVA-MySQL controller to retrieve and insert the data into the MySQL tables.
*/

/*
TODO : Add Function to delete items from the DB.
*/
package auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBController{

	private Connection con = null;
	private String currentKey;
	private	String url = "jdbc:mysql://localhost:3306/2F_Auth_Credentials";
	private	String user = "admin"; 
	private	String password = "jayant123"; // Need to be stored securely.
	
	private void init(){
		System.out.println("Inside init");
		try {
			System.out.println("Loading driver...");
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
			    throw new RuntimeException("Cannot find the driver in the classpath!", e);
		}
		try{
			con = DriverManager.getConnection(url, user, password);
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return;
	}

	private void exit(){
		System.out.println("Exiting the MySQL database");
		try{
			if(con != null){
				con.close();
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}

	private void setCurrentKey(String userID){
		Statement st = null;
		ResultSet rs = null;
		String query = "Select userKey from Credentials where userID='" + userID + "'";
		try{
			st = con.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				this.currentKey=rs.getString(1);
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(rs != null){
					rs.close();
				}
				if(st != null){
					st.close();
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}

	private void addUser(String userID, String userKey){
		Statement st = null;
		int status;
		String query = "Insert into Credentials " +
		      		"Set userID='" + userID + "' ,userKey='" + userKey + "';";
		System.out.println(query);	
		try{
			st = con.createStatement();
			status = st.executeUpdate(query);
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(st != null){
					st.close();
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}

	public static String getCurrentKey(String userID){
		DBController controller = new DBController();
		controller.init();
		controller.setCurrentKey(userID);
		controller.exit();
		return controller.currentKey;
	}

	public static void addNewUser(String userID, String userKey){
		DBController controller = new DBController();
		controller.init();
		controller.addUser(userID, userKey);
		controller.exit();
		return;
	}

	public static void main(String args[]){
		System.out.println(getCurrentKey("jayant27290"));
		addNewUser("aditya","#@1232kjfg");
	}
}
