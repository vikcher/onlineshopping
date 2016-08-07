package com.app.user;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("users")
public class User {
	
	public static final String SALT = "ramdom-salt-string";
	
	/* 
	 * Function to generate a Hash for a password to store in the Database
	 */
	private static String generateHash(String input) throws NoSuchAlgorithmException {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; ++idx) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException();
		}

		return hash.toString();
	}
	
	public static boolean authenticateUser(String uname, String password) throws NoSuchAlgorithmException, SQLException, URISyntaxException
	{
		String query = null;
		Connection conn = null;
		Statement stmt = null;
		query = "SELECT password from users where username = \'"+ uname +"\'";
		String retrievedPassword = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			retrievedPassword = rs.getString("password");
    		}
		} catch (SQLException e) {
	        throw new SQLException();
		} catch (URISyntaxException e) {
			throw new URISyntaxException("","");
		}
		
		String saltedPassword = SALT + password;
		String hashedPassword;
		try {
			hashedPassword = generateHash(saltedPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException();
		}
		
		if (hashedPassword.equals(retrievedPassword))
		{
			return true;
		}	
		
	    return false;
	}
	
	/*
	 * Function to check if user exists.
	 */
	public static boolean checkIfUserExists (String uname) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		Statement stmt = null;
		/* Find if username already exists */
		String query = "SELECT COUNT(*) AS total from users where username = \'"+ uname +"\'";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			/* If a user is already found, return a JSON error string*/
    			if (rs.getInt("total") > 0)
    			{
    				return true;
    			}
    		}
		} catch (SQLException e) {
	        throw new SQLException();
		} catch (URISyntaxException e)
		{
			throw new URISyntaxException("", "");
		}
		
		return false;
	}
	
	/*
	 * Function to get User ID. Returns -1 if user does not exist.
	 */
	public static int getUserID(String uname) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		Statement stmt = null;
		int id = -1;
		/* Retrieve User ID */
		String query = "SELECT id from users where username = \'"+ uname +"\'";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			id = rs.getInt("id");
    			
    		}
		} catch (SQLException e) {
	        throw new SQLException();
		} catch (URISyntaxException e)
		{
			throw new URISyntaxException("", "");
		}
		
		return id;
	}
	
	public static String generateJSONString (String type, String message)
	{
		 JSONObject obj = new JSONObject();
		 obj.put("Type", type);
		 obj.put("Message", message);
		 return obj.toJSONString();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String registerUser(@FormParam("username") String uname, 
			                   @FormParam("password") String password,
			                   @FormParam("firstname") String firstName,
			                   @FormParam("lastname") String lastName,
			                   @FormParam("email") String email)
	{
	 
		Connection conn = null;
		Statement stmt = null;
		String query = null;
		
		/* Find if username already exists */
		try {
			if(checkIfUserExists(uname))
			{
				return generateJSONString("Error", "Username \"" + uname + "\" already exists. Please choose another username");
			}
		} catch (SQLException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			return generateJSONString ("Error", "An internal error occured");
		}
		
		String hashedPassword;
		try{
			hashedPassword = generateHash(SALT + password);
		} catch (NoSuchAlgorithmException e)
		{
			return generateJSONString("Error", "An internal error occured");
		}
		
		query = "INSERT INTO users (\"username\", \"password\", \"first_name\", \"last_name\", \"email\") VALUES(\'" + uname + "\',\'" + hashedPassword + "\', \'" + firstName + "\', \'" + lastName +"\',\'" + email + "\')";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		int ret = stmt.executeUpdate(query);
    		if (ret != 1)
    		{
    			return generateJSONString("Error", "Internal database error");
    		}
		} catch (Exception e)
		{
			return generateJSONString("Error", "An internal error occured");
		}
		
		/* Success! Added the user to users database */
		return generateJSONString("Success", "Successfully added user " + uname);
	}
	
	/*
	 * Method handling HTTP DELETE requests for users.
	 * The purpose is to delete user accounts. This operation will succeed only if the correct
	 * password is provided.
	 * 
	 * @return JSON with response type (error/success) and message 
	 */
	@DELETE
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String deleteUser(@FormParam("username") String uname, 
                             @FormParam("password") String password)
	{
	
		Connection conn = null;
		Statement stmt = null;
		/* Find if the username exists*/
		String query = "SELECT COUNT(*) AS total from users where username = \'"+ uname +"\'";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			/* If a user is already found, return a JSON error string*/
    			if (rs.getInt("total") == 0)
    			{
    				return generateJSONString("Error", "Username \"" + uname + "\" does not exist. Cannot delete");
    			}
    		}
		} catch (Exception e) {
	        return generateJSONString ("Error", "An internal error occured");
		}
		
		Boolean authenticated = false;
		try {
			authenticated = authenticateUser(uname, password);
		} catch (SQLException | NoSuchAlgorithmException  | URISyntaxException e)
		{
		    return generateJSONString("Error", "An internal error occured");	
		}
		
		
		if (!authenticated)
		{
			return generateJSONString("Error", "The password does not match for user \"" + uname + "\". Cannot delete user");
		}
		
		/* 
		 * Password is correct, proceed to delete
		 */
		query = "DELETE FROM users where username = \'" + uname + "\'";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		int ret = stmt.executeUpdate(query);
    		if (ret != 1)
    		{
    			return generateJSONString("Error", "Internal database error");
    		}
		} catch (Exception e)
		{
			return generateJSONString("Error", "An internal error occured");
		}	
		
		/* Success! Deleted the user from users database */
		return generateJSONString("Success", "Successfully removed user " + uname);
	}
}
