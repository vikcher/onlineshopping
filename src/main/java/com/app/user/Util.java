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

	/* 
	 * Function to generate a Hash for a password to store in the Database
	 * Uses the SHA1 hashing algorithm to generate a digest.
	 * Reference : https://dzone.com/articles/storing-passwords-java-web
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
	
	
	
	/**
	 * Generate JSON response object from the response type, response code and message
	 * @param type - Response type (Success or error)
	 * @param responseCode - Response code
	 * @param message - Custom error messsage
	 * @return - JSON object formatted with the parameters
	 */
	public static String generateJSONString (String type, String responseCode, String message)
	{
		 JSONObject obj = new JSONObject();
		 obj.put("Type", type);
		 obj.put("Response Code", responseCode);
		 obj.put("Message", message);
		 return obj.toJSONString();
	}
}
