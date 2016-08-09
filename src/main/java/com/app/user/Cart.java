package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

@Path("cart")
public class Cart {
	
	private int getCartID(int user_id) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT * from cart where user_id = ?");
			stmt.setInt(1,user_id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				return rs.getInt("cart_id");
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
	    return -1;	
	}
	
	private int checkIfDuplicateItemExistsInCart (int cart_id, int product_id, String color, String size) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT * from cart_products where cart_id = ? and product_id = ? and color = ? and size = ?");
			stmt.setInt(1, cart_id);
			stmt.setInt(2, product_id);
			stmt.setString(3, color);
			stmt.setString(4, size);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				return rs.getInt("cart_product_id");	
			}
		} finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
		}
		
		return -1;
	}
	
	@GET
	@Secured
	@Produces("application/json")
	public String viewCart()
	{
		return "";
	}
	
	@PUT
	@Secured
	@Produces("application/json")
	@Path("{product_id}")
	public String addToCart(@PathParam("product_id") String productID,
			                @DefaultValue ("") @QueryParam("color") String color,
			                @DefaultValue("") @QueryParam("quantity") String quantity,
			                @DefaultValue("") @QueryParam("size") String size,
			                @Context SecurityContext sc)
	{
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			if (!Product.checkIfProductExists(Integer.parseInt(productID)))
			{
				return Util.generateJSONString("Error", "The specified product does not exist");	
			}
		} catch (NumberFormatException | URISyntaxException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (color.equals(""))
		{
			return Util.generateJSONString("Error", "Please specify the color");
		}
		if (quantity.equals("") || !StringUtils.isNumeric(quantity))
		{
			return Util.generateJSONString("Error", "Wrong format of parameter quantity");
		}
		if (size.equals(""))
		{
			return Util.generateJSONString("Error", "Please specify the size");
		}
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		if (cart_id == -1)
		{
			return Util.generateJSONString("Error", "An internal server error occured got cart id -1");
		}
		
		int cart_product_id = 0;
		try {
			cart_product_id = checkIfDuplicateItemExistsInCart(cart_id, Integer.valueOf(productID), color, size);
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		try {
			String query = null;
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			if (cart_product_id != -1)
			{
				query = "UPDATE cart_product_id set quantity = quantity + ? where cart_product_id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, Integer.parseInt(quantity));
				stmt.setInt(2, cart_product_id);
			} else {
				query = "INSERT INTO cart_product (cart_id, product_id, quantity, color, size) VALUES (?,?,?,?,?)";
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, cart_id);
				stmt.setInt(2, Integer.parseInt(productID));
				stmt.setInt(3, Integer.parseInt(quantity));
				stmt.setString(4, color);
				stmt.setString(5, size);
			}
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e) {
				return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
			}
		}
		
		return String.valueOf(cart_product_id);
	}
	
	@POST
	@Secured
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String checkOut(@FormParam("shipping_addr") String shipping_addr,
			               @FormParam("state") String state_code,
			               @FormParam("promo_code") String promo_code)
	{
		return "";
	}
	
}
