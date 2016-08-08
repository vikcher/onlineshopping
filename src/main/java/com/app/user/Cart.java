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
	
	private int checkIfUserCartExists(int user_id) throws URISyntaxException, SQLException
	{
		Connection conn = DbConn.getConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT * from cart where user_id = " + user_id;
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next())
		{
			return rs.getInt("cart_id");
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
		if (color.equals(""))
		{
			return Util.generateJSONString("Error", "Please specify the color");
		}
		if (quantity.equals(""))
		{
			return Util.generateJSONString("Error", "Please specify the quantity");
		}
		if (size.equals(""))
		{
			return Util.generateJSONString("Error", "Please specify the size");
		}
		
		int cart_id = -1;
		try {
			cart_id = checkIfUserCartExists(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured");
		}
		
		return String.valueOf(cart_id);
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
