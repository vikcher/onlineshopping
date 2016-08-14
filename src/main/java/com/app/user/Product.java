package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.app.dbconn.DbConn;


/**
 * @author vikcher
 *
 */
@Path("/products")
public class Product {
	
	/**
	 * @param id
	 * @param color
	 * @param size
	 * @return true if size and color are found for the particular product. Returns false if either of them are not found.
	 * @throws SQLException
	 * @throws URISyntaxException
	 * @throws JSONException
	 */
	public static boolean validateProductColorAndSize(int id, String color, String size) throws SQLException, URISyntaxException, JSONException
	{
	    Connection conn = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String options = null;
	    boolean colorFound = false;
	    boolean sizeFound = false;
	    try {
	    	conn = DbConn.getConnection();
	    	stmt = conn.prepareStatement("SELECT options from products where product_id = ?");
	    	stmt.setInt(1, id);
	    	rs = stmt.executeQuery();
	    	while (rs.next())
	    	{
	    		options = rs.getString("options");
	    	}
	    } finally {
	    	if (rs != null) rs.close();
	    	if (stmt != null) stmt.close();
	    	if (conn != null) conn.close();
	    }
	    
	    org.json.JSONObject obj = new org.json.JSONObject(options);
	    org.json.JSONArray colors = obj.getJSONArray("colors");
	    org.json.JSONArray sizes = obj.getJSONArray("sizes");
	    for (int i = 0; i < colors.length(); i++)
	    {
	    	if (color.equals(colors.get(i)))
	    		colorFound = true;
	    }
	    
	    for (int i = 0; i < sizes.length(); i++)
	    {
	    	if (size.equals(sizes.get(i)))
	    		sizeFound = true;
	    }
	    
	    if (sizeFound == true && colorFound == true)
	    {
	    	return true;
	    }
	    
	    return false;
	}
	
	/**
	 * Convenience function to check if the product with given ID exists.
	 * @param id
	 * @return true if product exists. False if it doesn't
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	public static boolean checkIfProductExists (int id) throws URISyntaxException, SQLException 
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "Select * from products where product_id = ?";
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
			    return true;	
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
	    return false;		
	}
	
	
	/**
	 * Return the discount if applicable on a specific product by ID
	 * @param id
	 * @return Discount on the product if it is applicable, otherwise return 0.0
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	public static double getProductDiscount(int id) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "Select discount from product_discount where product_id = ?";
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
			    return rs.getDouble("discount");	
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
	    return 0.0;	
	}
	
	
	/**
	 * Get the full product catalog or filter by passing category ID as query parameter
	 * @param category_id
	 * @return Success or failure JSON string if unexpected error occurs
	 */
	@GET
	@Produces("application/json")
	public String getFullProductList(@DefaultValue("-1") @QueryParam("category_id") String category_id)
	{
	
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		JSONObject ret = new JSONObject();
		JSONArray arr = new JSONArray();
		int count = 0;
		String query = null;
		if (category_id.equals("-1"))
		    query = "SELECT * from products";
		else
			query = "SELECT * from products where category_id = " + category_id;
		
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next())
			{
				JSONObject newObj = new JSONObject();
				newObj.put("Product ID", rs.getInt("product_id"));
				newObj.put("Category", Category.getCategoryNameFromID(rs.getInt("category_id")));
				newObj.put("Product name", rs.getString("product_name"));
				newObj.put("Product description", rs.getString("product_description"));
				newObj.put("Price", rs.getFloat("product_price"));
				newObj.put("options", rs.getString("options"));
				newObj.put("File URL", rs.getString("img_url"));
				newObj.put("Discount", getProductDiscount(rs.getInt("product_id")));
				arr.add(newObj);
				count++;
			}
			
		} catch (URISyntaxException | SQLException e) {
	     	return Util.generateJSONString("Error", "800",  "An internal server error occured");
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				return Util.generateJSONString("Error", "800", "An internal server error occured");
			}
		}
		
		ret.put("Type", "Success");
		ret.put("Response code", "600");
		ret.put("Number of products", count);
		ret.put("Product list", arr);
		return ret.toJSONString();
	}
	
	
	/**
	 * Return specific product filtered by product ID
	 * @param productID
	 * @return Product details of product with specified ID
	 */
	@GET
	@Produces("application/json")
	@Path("{productID}")
	public String getProductByID(@PathParam("productID") String productID)
	{
		int id = Integer.valueOf(productID);
		JSONObject ret = new JSONObject();
		JSONObject newObj = null;
		int count = 0;
		String query = "SELECT * from products where product_id = ?";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			if (!checkIfProductExists(Integer.parseInt(productID)))
			{
				return Util.generateJSONString("Error", "702",  "No product with given ID exists");
			}
		} catch (NumberFormatException | URISyntaxException | SQLException e1) {
			return Util.generateJSONString("Error", "800",  "An internal server error occured");
		}
		
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, Integer.parseInt(productID));
			rs = stmt.executeQuery();
			while (rs.next())
			{
				newObj = new JSONObject();
				newObj.put("Product ID", rs.getInt("product_id"));
				newObj.put("Product name", rs.getString("product_name"));
				newObj.put("Category", Category.getCategoryNameFromID(rs.getInt("category_id")));
				newObj.put("Product description", rs.getString("product_description"));
				newObj.put("Price", rs.getFloat("product_price"));
				org.json.JSONObject options = new org.json.JSONObject(rs.getString("options"));
				newObj.put("options", options);
				newObj.put("File URL", rs.getString("img_url"));
				newObj.put("Discount", getProductDiscount(rs.getInt("product_id")));
			}
			
		} catch (URISyntaxException | SQLException | JSONException e) {
	     	return Util.generateJSONString("Error", "800",  "An internal server error occured");
		} finally {
            try {
            	if (rs != null) rs.close();
            	if (stmt != null) stmt.close();
            	if (conn != null) conn.close();
            } catch (SQLException e)
            {
            	return Util.generateJSONString("Error", "800",  "An internal server error occured");
            }
		}
		ret.put("Type", "Success");
		ret.put("Product details", newObj);
		return ret.toJSONString();
	}
}
