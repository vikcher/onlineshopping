package com.app.user;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

/**
 * Root resource (exposed at "myresource" path)
 */
/**
 * @author vikcher
 *
 */
@Path("users")
public class User {
	
	
	public static final String SALT = "ramdom-salt-string";
	
	
	/**
	 * @param uname
	 * @param password
	 * @return True if user credentials are correct. False if user credentials provided are not valid.
	 * @throws NoSuchAlgorithmException
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	public static boolean authenticateUser(String uname, String password) throws NoSuchAlgorithmException, SQLException, URISyntaxException
	{
		String query = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		query = "SELECT password from users where username = ?";
		String retrievedPassword = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, uname);
    		rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			retrievedPassword = rs.getString("password");
    		}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		String saltedPassword = Util.SALT + password;
		String hashedPassword;
		hashedPassword = Util.generateHash(saltedPassword);
		
		if (hashedPassword.equals(retrievedPassword))
		{
			return true;
		}	
	    return false;
	}
	
	
	/**
	 * @param uname
	 * @return True if user already exists, False if user does not exist
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	public static boolean checkIfUserExists (String uname) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "SELECT COUNT(*) AS total from users where username = ?";
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, uname);
    		rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			if (rs.getInt("total") > 0)
    			{
    				return true;
    			}
    		}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		return false;
	}
	
	
	/**
	 * @param uname
	 * @return Returns User ID corresponding to the Username
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	public static int getUserID(String uname) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		int id = -1;
		ResultSet rs = null;
		String query = "SELECT id from users where username = ?";
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, uname);
    		rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			id = rs.getInt("id");
    			
    		}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		return id;
	}

	/**
	 * Function to register a new User. The URL for this is POST /users
	 * @param uname
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return Error JSON string or Success JSON string if user is successfully added.
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String registerUser(@FormParam("username") String uname, 
			                   @FormParam("password") String password,
			                   @FormParam("firstname") String firstName,
			                   @FormParam("lastname") String lastName,
			                   @FormParam("email") String email)
	{
	 
		String query = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int user_id = 0;
		/* Find if username already exists */
		try {
			if(checkIfUserExists(uname))
			{
				return Util.generateJSONString("Error", "701", "Username \"" + uname + "\" already exists. Please choose another username");
			}
		} catch (SQLException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			return Util.generateJSONString ("Error", "800", "An internal error occured");
		}
		
		String hashedPassword;
		try{
			hashedPassword = Util.generateHash(Util.SALT + password);
		} catch (NoSuchAlgorithmException e)
		{
			return Util.generateJSONString("Error", "800", "An internal error occured");
		}
		
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO users (username, password, first_name, last_name, email) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, uname);
			stmt.setString(2, hashedPassword);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setString(5, email);
    		stmt.executeUpdate();
    		rs = stmt.getGeneratedKeys();
    		rs.next();
    		user_id = rs.getInt(1);
    		conn.commit();
		} catch (Exception e)
		{
			return Util.generateJSONString("Error", "800", "An internal error occured " + e.getMessage() + String.valueOf(user_id));
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "800", "An internal error occured " + e.getMessage() + String.valueOf(user_id));
			}
		}
		
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT into cart (user_id) VALUES (?)");
			stmt.setInt(1, user_id);
			stmt.executeUpdate();
			conn.commit();
		} catch (URISyntaxException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e) {
				return Util.generateJSONString("Error", "800", "An internal error occured " + e.getMessage() + String.valueOf(user_id));
			}
		}
		
		
		/* Success! Added the user to users database */
		return Util.generateJSONString("Success", "600", "Successfully added user " + uname);
	}
	

	/**
	 * This is the DELETE request for users. Path is DELETE /users
	 * The logged-in user's account is deleted and the corresponding cart is also deleted.
	 * @param sc
	 * @return Success or Error JSON string depending on the result.
	 */
	@DELETE
	@Produces("application/json")
	@Secured
	@Consumes("application/x-www-form-urlencoded")
	public String deleteUser(@Context SecurityContext sc)
	{
	
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int id = user.getID();
		String uname = user.getUserName();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS total from users where id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				if (rs.getInt("total") == 0)
				{
					return Util.generateJSONString("Error", "702", "Username \"" + uname + "\" does not exist. Cannot delete");
				}
			}
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString ("Error", "800", "An internal error occured");
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString ("Error", "800", "An internal error occured" + e.getMessage());
			}
		}
		
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("DELETE FROM users where id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "800", "An internal error occured");
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
		
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("DELETE FROM cart where user_id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "800", "An internal error occured");
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
		
		/* Success! Deleted the user from users database */
		return Util.generateJSONString("Success", "600", "Successfully removed user " + uname);
	}
}
