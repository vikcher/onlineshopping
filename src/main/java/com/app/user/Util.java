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

	Util() throws SQLException, URISyntaxException
	{
	    conn = DbConn.getConnection();
		stmt = conn.createStatement();	
	}
	
	public static ResultSet executeQuery(String query) throws SQLException
	{
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	public static void executeUpdate(String query) throws SQLException 
	{
		stmt.executeUpdate(query);
	}
	
	public static String getCategoryNameFromID(int id) throws SQLException, URISyntaxException
	{
	       String ret = null;
	       String query = "Select category_name from categories where category_id = " + id;
	       ResultSet rs = executeQuery(query);
	       while (rs.next())
	       {
	           ret = rs.getString("category_name");
	       }
	       return ret;
	}
	
	public static String getProductDiscount(int id) throws SQLException, URISyntaxException
	{
		String query = "Select discount from product_discount where product_id = "  + id;
	    ResultSet rs = executeQuery(query);
		while (rs.next())
		{
			return String.valueOf(rs.getFloat("discount"));	
		}
	    return "No discount on this product";	
	}
	
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
	
	public static String generateJSONString (String type, String message)
	{
		 JSONObject obj = new JSONObject();
		 obj.put("Type", type);
		 obj.put("Message", message);
		 return obj.toJSONString();
	}
	
	public static boolean authenticateUser(String uname, String password) throws NoSuchAlgorithmException, SQLException, URISyntaxException
	{
		String query = "SELECT password from users where username = \'"+ uname +"\'";
		String retrievedPassword = null;
    	ResultSet rs = executeQuery(query);
    	while (rs.next())
    	{
    			retrievedPassword = rs.getString("password");
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

}
