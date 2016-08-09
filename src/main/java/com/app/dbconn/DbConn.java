package com.app.dbconn;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author vikcher
 * Class used to generate Connection to the database.
 */
public class DbConn {
	
	/**
	 * @return Connection object for connection to the Database
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws URISyntaxException, SQLException {
	    String dbUrl = System.getenv("JDBC_DATABASE_URL");
	    return DriverManager.getConnection(dbUrl);
	}
}
