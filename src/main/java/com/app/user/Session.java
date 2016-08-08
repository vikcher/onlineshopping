package com.app.user;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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

	private static String generateToken(String uname) throws SQLException, URISyntaxException
	{
		Random random = new SecureRandom();
	    String token = new BigInteger(130, random).toString(32);
	    int user_id = User.getUserID(uname);
	    Connection conn = null;
	    Statement stmt = null;
	    String query = "INSERT INTO sessions (\"user_id\", \"token\") VALUES(" + user_id + ",\'" + token + "\')";
	    try {
	    	conn = DbConn.getConnection();
	    	stmt = conn.createStatement();
	    	stmt.executeUpdate(query);	
	    } finally {
	    	if (stmt != null) stmt.close();
	    	if (conn != null) conn.close();
	    }
    	
		return token;
	}
	
	/*
	 * The login function first authenticates the user against the database.
	 * Then, it generates a unique token and stores the token in the DB along with 
	 * the corresponding UserID
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String userLogin(@FormParam("username") String uname, 
            				@FormParam("password") String password)
	{
		String token = null;
		try {
			if (!User.checkIfUserExists(uname))
			{
				return Util.generateJSONString("Error", "User does not exist");
			} 
			
			if (User.authenticateUser(uname,password))
			{
				token = generateToken(uname);
			} else {
				return Util.generateJSONString("Error", "The password entered is invalid. Please try again");
			}
		} catch (SQLException | URISyntaxException | NoSuchAlgorithmException e) {
			return Util.generateJSONString("Error", "An internal error occured");
		}
		
		JSONObject obj = new JSONObject();
		obj.put("Type", "Success");
		obj.put("Message", "User " + uname + " successfully logged in");
		obj.put("Token", token);
		return obj.toJSONString();
	}
	
	@DELETE
	@Secured
	@Produces("application/json")
	public String userLogout(@Context SecurityContext sc) 
	{
		JSONObject obj;
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int id = user.getID();
		String name = user.getUserName();
		String query = "DELETE from sessions where user_id = " + id;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString("Error", "You are not logged in");
		} finally {
			try {
			    if (stmt != null) stmt.close();
			    if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "An internal error occured");
			}
			
		}
		
		return Util.generateJSONString("Success", "User " + name + " successfully logged out");
	}
}
