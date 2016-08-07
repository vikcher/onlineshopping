package com.app.onlineshopping;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.Consumes;
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

	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String registerUser(@FormParam("username") String uname, 
			                   @FormParam("password") String password,
			                   @FormParam("firstname") String firstName,
			                   @FormParam("lastname") String lastName)
	{
	   
		StringBuilder json = new StringBuilder();
		Connection conn = null;
		Statement stmt = null;
		JSONObject obj;
		
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
    				obj = new JSONObject();
    				obj.put("Type", "Error");
    				obj.put("Message", "Username " + uname + "already exists. Please choose another username");
    				return obj.toJSONString();
    			}
    		}
		} catch (Exception e) {
	        obj = new JSONObject();
	        obj.put("Type", "Error");
	        obj.put("Message", "An internal error occured");
	        return obj.toJSONString();
		}
		
		String hashedPassword;
		try{
			hashedPassword = generateHash(password);
		} catch (NoSuchAlgorithmException e)
		{
			obj = new JSONObject();
			obj.put("Type", "error");
			obj.put("Message", "Internal error");
			return obj.toJSONString();
		}
		
		query = "INSERT INTO users (\"username\", \"password\", \"first_name\", \"last_name\") VALUES(\'" + uname + "\',\'" + hashedPassword + "\', \'" + firstName + "\', \'" + lastName +"\')";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		int ret = stmt.executeUpdate(query);
    		if (ret != 1)
    		{
    			obj = new JSONObject();
    			obj.put("Type", "error");
    			obj.put("Message", "Internal database error");
    			return obj.toJSONString();
    		}
		} catch (Exception e)
		{
		    obj = new JSONObject();
		    obj.put("Type", "error");
		    obj.put("Message", "Internal error");
		    return obj.toJSONString();
		}
		
		return "";
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
