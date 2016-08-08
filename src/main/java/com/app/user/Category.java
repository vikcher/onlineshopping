package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

@Path("categories")
public class Category {
	
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

	public static String getCategoryDiscount(int id) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "Select discount from category_discount where product_id = "  + id;
	    try {
	    	conn = DbConn.getConnection();
	    	stmt = conn.createStatement();
	        rs = stmt.executeQuery(query);
		    while (rs.next())
		    {
			    return String.valueOf(rs.getFloat("discount"));	
		    }
	    } finally {
	    	if (rs != null) rs.close();
	    	if (stmt != null) stmt.close();
	    	if (conn != null) conn.close();
	    }
	    return "No discount on this category";	
	}
	
	/*
	@GET
	@Produces("application/json")
	public static String viewProductCategories()
	{
		Connection conn = null;
		Statement stmt = null;
		String query = "SELECT * from categories";
		int count = 0;
		JSONArray arr = new JSONArray();
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("ID", rs.getInt("category_id"));
				obj.put("Category_name", rs.getString("category_name"));
				obj.put("Category_description", rs.getString("category_description"));
				obj.put("Category discount",getCategoryDiscount(rs.getInt("category_id")));
				arr.add(obj);
				count++;
			}
		} catch (SQLException | URISyntaxException e) {
			Util.generateJSONString("Error", "An internal error occured");
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				Util.generateJSONString("Error", "An internal error occured");
			}
		}
		
		JSONObject finalJSON = new JSONObject();
		finalJSON.put("Type", "Success");
		finalJSON.put("Category Count", count);
		finalJSON.put("Category list", arr);
		return finalJSON.toJSONString();
	}
	*/
}
