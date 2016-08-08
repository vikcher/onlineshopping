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

import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

@Path("cart")
public class Cart {

	public static int checkIfUserCartExists(int userID) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "SELECT * from cart where user_id = " + userID;
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
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
	
	public static int checkDuplicateProductInCart(int cart_id, int productID, String color, String size) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT cart_product_id from category_products where cart_id = ? and product_id = ? and color = ? and size = ?");
			stmt.setInt(1, cart_id);
			stmt.setInt(2, productID);
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
			                @QueryParam("color") String color,
			                @DefaultValue("3") @QueryParam("quantity") String quantity,
			                @QueryParam("size") String size,
			                @Context SecurityContext sc)
	{
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		int product_id = Integer.valueOf(productID);
		int qty = Integer.parseInt(quantity);
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String query = null;
		int cart_id = 0;
		int cart_product_id = 0;
		try {
			cart_id = checkIfUserCartExists(user_id);
			if (cart_id == -1)
			{
			    query = "INSERT INTO cart (\"user_id\") VALUES (" + user_id + ")";
			    conn = DbConn.getConnection();
			    stmt = conn.createStatement();
			    stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			    rs = stmt.getGeneratedKeys();
			    while (rs.next())
			    {
			    	cart_id = rs.getInt(1);
			    }
			}
			cart_product_id = checkDuplicateProductInCart(cart_id, product_id, color, size);
			if (cart_product_id == -1)
			{
				psmt = conn.prepareStatement("INSERT into cart_products (cart_id, product_id, quantity, color, size) VALUES (?,?,?,?,?)");
				psmt.setInt(1, cart_id);
				psmt.setInt(product_id, product_id);
				psmt.setInt(3, qty);
				psmt.setString(4, color);
				psmt.setString(5, size);
			    psmt.executeUpdate();
			} else {
			    psmt = conn.prepareStatement("UPDATE cart_products SET quantity = quantity + ? where cart_product_id = " + cart_product_id);
			    psmt.setInt(1, qty);
			    psmt.executeUpdate();
			}
		} catch (SQLException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
			if (rs != null) rs.close();
 			if (stmt != null) stmt.close();
			if (psmt != null) psmt.close();
			if (conn != null) conn.close();
			} catch (SQLException e) {
				return Util.generateJSONString("Error", "An unknown server error occured " + e.getMessage());
			}
		}
		return "";
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
