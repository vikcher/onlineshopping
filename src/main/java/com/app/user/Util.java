package com.app.user;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

public class Util {
	
	public static final String SALT = "ramdom-salt-string";
	public static Connection conn;
	public static Statement stmt;
	
	/*
	public static ResultSet executeQuery(String query) throws SQLException, URISyntaxException
	{
		ResultSet rs = null;
		try {
		    conn = DbConn.getConnection();
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery(query);
		} finally{
			if (conn != null) conn.close();
		}
		return rs;
	}
	
	public static void executeUpdate(String query) throws SQLException, URISyntaxException 
	{
		try {
		 conn = DbConn.getConnection();
	     stmt = conn.createStatement();
		 stmt.executeUpdate(query);
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	
	public static String getCategoryNameFromID(int id) throws SQLException, URISyntaxException
	{
	       String ret = null;
	       ResultSet rs = null;
	       String query = "Select category_name from categories where category_id = " + id;
	       Connection conn = null;
	       Statement stmt = null;
	       try {
	    	   conn = DbConn.getConnection();
	    	   stmt = conn.createStatement();
	           rs = stmt.executeQuery(query);
	           while (rs.next())
	           {
	               ret = rs.getString("category_name");
	           }
	       } finally {
	    	   if (rs != null) rs.close();
	    	   if (stmt != null) stmt.close();
	    	   if (conn != null) conn.close();
	       }
	       return ret;
	}
	*/
	/*
	public static String getProductDiscount(int id) throws SQLException, URISyntaxException
	{
		String query = "Select discount from product_discount where product_id = "  + id;
	    ResultSet rs = null;
	    try {
	        rs = executeQuery(query);
		    while (rs.next())
		    {
			    return String.valueOf(rs.getFloat("discount"));	
		    }
	    } finally {
	    	if (rs != null) rs.close();
	    }
	    return "No discount on this product";	
	}
	
	/*
	public static String getCategoryDiscount(int id) throws SQLException, URISyntaxException
	{
		String query = "Select discount from category_discount where product_id = "  + id;
	    ResultSet rs = null;
	    try {
	        rs = executeQuery(query);
		    while (rs.next())
		    {
			    return String.valueOf(rs.getFloat("discount"));	
		    }
	    } finally {
	    	if (rs != null) rs.close();
	    }
	    return "No discount on this category";	
	}
	*/
	
	/* 
	 * Function to generate a Hash for a password to store in the Database
	 */
	public static String generateHash(String input) throws NoSuchAlgorithmException {
		StringBuilder hash = new StringBuilder();

		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] hashedBytes = sha.digest(input.getBytes());
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		            		'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < hashedBytes.length; ++idx) {
			byte b = hashedBytes[idx];
			hash.append(digits[(b & 0xf0) >> 4]);
			hash.append(digits[b & 0x0f]);
		}

		return hash.toString();
	}
	
	public static String generateJSONString (String type, String message)
	{
		 JSONObject obj = new JSONObject();
		 obj.put("Type", type);
		 obj.put("Message", message);
		 return obj.toJSONString();
	}
	
	/*
	public static boolean authenticateUser(String uname, String password) throws NoSuchAlgorithmException, SQLException, URISyntaxException
	{
		String query = "SELECT password from users where username = \'"+ uname +"\'";
		String retrievedPassword = null;
    	ResultSet rs = null;
    	try {
    		rs = executeQuery(query);
    		while (rs.next())
    		{
    			retrievedPassword = rs.getString("password");
    		}
    	} finally {
    		if (rs != null) rs.close();
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
	*/
	
	/*
	public static boolean checkIfUserExists (String uname) throws SQLException, URISyntaxException
	{
		String query = "SELECT COUNT(*) AS total from users where username = \'"+ uname +"\'";
    	ResultSet rs = null;
    	try {
    		rs = executeQuery(query);
    		while (rs.next())
    		{
    			if (rs.getInt("total") > 0)
    			{
    				return true;
    			}
    		}
    	} finally {
    		if (rs != null) rs.close();
    	}
		return false;
	}*/
	
	/*
	 * Function to get User ID. Returns -1 if user does not exist.
	 */
	/*
	public static int getUserID(String uname) throws SQLException, URISyntaxException
	{
		int id = -1;
	
		String query = "SELECT id from users where username = \'"+ uname +"\'";
		ResultSet rs = null;
		try {
			rs = executeQuery(query);
			while (rs.next())
			{
				id = rs.getInt("id");

			}
		} finally {
			if (rs != null) rs.close();
		}
		return id;
	}*/

}
