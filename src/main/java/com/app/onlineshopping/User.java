package com.app.onlineshopping;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
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
	public static String generateHash(String input) throws NoSuchAlgorithmException {
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
	
	private static String generateJSONString (String type, String message)
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
			                   @FormParam("lastname") String lastName)
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
    				return generateJSONString("Error", "Username \"" + uname + "\" already exists. Please choose another username");
    			}
    		}
		} catch (Exception e) {
	        return generateJSONString ("Error", "An internal error occured");
		}
		
		String hashedPassword;
		try{
			hashedPassword = generateHash(SALT + password);
		} catch (NoSuchAlgorithmException e)
		{
			return generateJSONString("Error", "An internal error occured");
		}
		
		query = "INSERT INTO users (\"username\", \"password\", \"first_name\", \"last_name\") VALUES(\'" + uname + "\',\'" + hashedPassword + "\', \'" + firstName + "\', \'" + lastName +"\')";
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
		return generateJSONString("Success", "Successfully added user" + uname);
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
		} catch (Exception e) {
	        return generateJSONString ("Error", "An internal error occured");
		}
		
		String saltedPassword = SALT + password;
		String hashedPassword;
		try {
			hashedPassword = generateHash(saltedPassword);
		} catch (NoSuchAlgorithmException e) {
			return generateJSONString ("Error", "An internal error occured");
		}
		
		if (!hashedPassword.equals(retrievedPassword))
		{
			return generateJSONString("Error", "The password does not match for user \"" + uname + "\". Cannot delete user");
		}
		
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
		return generateJSONString("Success", "Successfully removed user" + uname);
	}
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	/*
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        StringBuilder ret = new StringBuilder();
        
        Statement stmt = null;
    	String query = "select * from users";
    	try {
    		Connection conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ret.append(id);
                ret.append(name);
            }
    	} catch (Exception e)
    	{
    	     e.printStackTrace();
    	}
        return ret.toString();
    }
    */
}
