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
	
	public static String generateJSONString (String type, String responseCode, String message)
	{
		 JSONObject obj = new JSONObject();
		 obj.put("Type", type);
		 obj.put("Response Code", responseCode);
		 obj.put("Message", message);
		 return obj.toJSONString();
	}
}
