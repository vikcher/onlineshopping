package com.app.user;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

@Path("sessions")
public class Session {

	/**
	 * Generate a random token for the new user session. This user ID is associated with token in the DB.
	 * @param uname
	 * @return New random token for user session
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	private static String generateToken(String uname) throws SQLException, URISyntaxException
	{
		Random random = new SecureRandom();
	    String token = new BigInteger(130, random).toString(32);
	    int user_id = User.getUserID(uname);
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    String query = "INSERT INTO sessions (user_id, token) VALUES(?,?)";
	    try {
	    	conn = DbConn.getConnection();
	    	conn.setAutoCommit(false);
	    	stmt = conn.prepareStatement(query);
	    	stmt.setInt(1,user_id);
	    	stmt.setString(2, token);
	    	stmt.executeUpdate();
	    	conn.commit();
	    } finally {
	    	if (stmt != null) stmt.close();
	    	conn.setAutoCommit(true);
	    	if (conn != null) conn.close();
	    }
    	
		return token;
	}
	
	/**
	 * User login - returns Authorization token in header.
	 * @param uname
	 * @param password
	 * @param response
	 * @return Success or failure message depending on the outcome.
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String userLogin(@FormParam("username") String uname, 
            				@FormParam("password") String password,
            				@Context HttpServletResponse response)
	{
		String token = null;
		try {
			if (!User.checkIfUserExists(uname))
			{
				return Util.generateJSONString("Error", "702", "User does not exist");
			} 
			
			if (User.authenticateUser(uname,password))
			{
				token = generateToken(uname);
			} else {
				return Util.generateJSONString("Error", "703", "The password entered is invalid. Please try again");
			}
		} catch (SQLException | URISyntaxException | NoSuchAlgorithmException e) {
			return Util.generateJSONString("Error", "800", "An internal error occured ");
		}

		response.setHeader("Authentication Token", token);
		return Util.generateJSONString("Success", "600", "User " + uname + " successfully logged in");
	}
	
	
	/**
	 * Logging out a user. Path DELETE /sessions
	 * @param sc
	 * @return
	 */
	@DELETE
	@Secured
	@Produces("application/json")
	public String userLogout(@Context SecurityContext sc) 
	{
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int id = user.getID();
		String name = user.getUserName();
		String query = "DELETE from sessions where user_id = ?";
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString("Error", "700", "You are not logged in");
		} finally {
			try {
			    if (stmt != null) stmt.close();
			    conn.setAutoCommit(true);
			    if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "800", "An internal error occured");
			}
			
		}
		return Util.generateJSONString("Success", "600", "User " + name + " successfully logged out");
	}
}
