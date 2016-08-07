package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;


@Path("products")
public class Product {

	public String getCategoryNameFromID(int id) throws SQLException, URISyntaxException
	{
	       String ret = null;
	       Connection conn = null;
	       Statement stmt = null;
	       String query = "Select category_name from categories where category_id = " + id;
	       try {
	    	   conn = DbConn.getConnection();
	    	   stmt = conn.createStatement();
	    	   ResultSet rs = stmt.executeQuery(query);
	    	   while (rs.next())
	    	   {
	    		   ret = rs.getString("category_name");
	    	   }
	       } catch (SQLException e)
	       {
	    	   throw new SQLException();
	       } catch (URISyntaxException e)
	       {
	    	   throw new URISyntaxException("","");
	       }
	       return ret;
	}
	
	@GET
	@Path("{category_id}")
	@Produces("application/json")
	public String getFullProductList(@DefaultValue("-1") @QueryParam("category_id") String category_id)
	{
		int cat_id = Integer.valueOf(category_id);
		Connection conn = null;
		Statement stmt = null;
		JSONObject ret = new JSONObject();
		JSONArray arr = new JSONArray();
		int count = 0;
		String query = null; 
		if (cat_id == -1)
		    query = "SELECT * from products";
		else
			query = "SELECT * from products where category_id = " + cat_id;
		
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
			{
				JSONObject newObj = new JSONObject();
				newObj.put("Product ID", rs.getInt("product_id"));
				newObj.put("Category", getCategoryNameFromID(rs.getInt("category_id")));
				newObj.put("Product name", rs.getInt("product_name"));
				newObj.put("Product description", rs.getInt("product_description"));
				newObj.put("Price", rs.getFloat("product_price"));
				newObj.put("options", rs.getString("options"));
				newObj.put("File URL", rs.getString("img_url"));
				arr.add(newObj);
				count++;
			}
			
		} catch (URISyntaxException | SQLException e) {
	     	return User.generateJSONString("Error", "An internal server error occured");
		}
		
		ret.put("Type", "Success");
		ret.put("Number of products", count);
		ret.put("Product list", arr);
		return ret.toJSONString();
	}
	
	/*
	@GET
	@Produces("application/json")
	public String getProductsByCategoryID(@PathParam("category") String CategoryID)
	{
		int id = Integer.valueOf(CategoryID);
		Statement stmt = null;
		Connection conn = null;
		JSONObject ret = new JSONObject();
		JSONArray arr = new JSONArray();
		int count = 0;
		String query = "SELECT * from products where category_id = " + id;
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
			{
				JSONObject newObj = new JSONObject();
				newObj.put("Product ID", rs.getInt("product_id"));
				newObj.put("Product name", rs.getInt("product_name"));
				newObj.put("Product description", rs.getInt("product_description"));
				newObj.put("Price", rs.getFloat("product_price"));
				newObj.put("options", rs.getString("options"));
				newObj.put("File URL", rs.getString("img_url"));
				arr.add(newObj);
				count++;
			}
			
		} catch (URISyntaxException | SQLException e) {
	     	return User.generateJSONString("Error", "An internal server error occured");
		}
		
		ret.put("Type", "Success");
		ret.put("Number of products", count);
		ret.put("Product list", arr);
		return ret.toJSONString();		
	}
	*/
}
