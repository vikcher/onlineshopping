package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.app.dbconn.DbConn;

@Path("cart")
public class Cart {
	
	private String validateProductCartInput (String productID, String quantity, String color, String size)
	{
		try {
			if (!Product.checkIfProductExists(Integer.parseInt(productID)))
			{
				return Util.generateJSONString("Error", "The specified product does not exist");	
			}
		} catch (NumberFormatException | URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
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
		
		return "";
	}
	
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
	
	private double getTaxForState (String state) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT * from sales_tax where state_code = ?");
			stmt.setString(1, state);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				return rs.getDouble("sales_tax_percentage");
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		return -1.0;
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
	
	private int getQuantity (int cart_product_id) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT quantity from cart_products where cart_product_id = ?");
			stmt.setInt(1, cart_product_id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				return rs.getInt("quantity");	
			}
		} finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
		}
		
		return -1;
	}
	
	public static double getTotalDiscount(double price, int productID, int categoryID) throws SQLException, URISyntaxException
	{
	    double productDiscount = Product.getProductDiscount(productID);
	    double categoryDiscount = Category.getCategoryDiscount(categoryID);
	    return (price*(double)(Math.max(productDiscount, categoryDiscount)/100));
	}
	
	public CartResult buildCartItems(int cart_id) throws URISyntaxException, SQLException
	{
		CartResult ret = new CartResult();
		JSONArray objects = new JSONArray();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT size, color, quantity, products.product_id AS pid, product_description, product_name, product_price, category_name, products.category_id as cid , img_url from products, cart_products, categories where cart_products.product_id = products.product_id and categories.category_id = products.category_id and cart_id = ?");
			stmt.setInt(1, cart_id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				JSONObject newObject = new JSONObject();
				newObject.put("Product ID", rs.getInt("pid"));
				newObject.put("Product name", rs.getString("product_name"));
				newObject.put("Product description", rs.getString("product_description"));
				newObject.put("Product price", (double)rs.getDouble("product_price")*rs.getInt("quantity"));
				newObject.put("Color", rs.getString("color"));
				newObject.put("Image URL", rs.getString("img_url"));
				ret.setTotal_price(ret.getTotal_price() + (double)(rs.getDouble("product_price")*rs.getInt("quantity"))) ;
				double discount = getTotalDiscount((double)(rs.getDouble("product_price")*rs.getInt("quantity")), rs.getInt("pid"), rs.getInt("cid"));
				newObject.put("Discount", discount);
				ret.setTotal_discount(ret.getTotal_discount() + discount);
				//total_num_items += rs.getInt("quantity");
				ret.setTotal_items(ret.getTotal_items() + rs.getInt("quantity"));
				objects.add(newObject);
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		ret.setItems(objects);
		return ret;
	}
	
	public double getPromoCodeDiscount(String promo_code) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT * from promo_codes where promo_code = ?");
			stmt.setString(1, promo_code);
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
		
		return -1.0;	
	}
	
	@GET
	@Secured
	@Produces("application/json")
	public String viewCart(@Context SecurityContext sc)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		JSONObject ret = new JSONObject();
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		CartResult cr = null;
		try {
			cr = buildCartItems(cart_id);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		ret.put("Type", "Success");
		ret.put("Items", cr.getItems());
		ret.put("Total number of items", cr.getTotal_items());
		ret.put("Total price before discount", cr.getTotal_price());
		ret.put("Total savings", cr.getTotal_discount());
		ret.put("Total price after discount", cr.getTotal_price() - cr.getTotal_discount());
		return ret.toJSONString();
		
	}
	
	@DELETE
	@Secured
	@Produces("application/json")
	public String emptyCart(@Context SecurityContext sc)
	{
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		Connection conn = null;
		PreparedStatement stmt = null;
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("DELETE from cart_products where cart_id = ?");
			stmt.setInt(1, cart_id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e){
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
			}
		}
	    return Util.generateJSONString("Success", "Your cart is now empty");	
	}
	
	@DELETE
	@Secured
	@Produces("application/json")
	@Path("{product_id}")
	public String removeFromCart(@PathParam("product_id") String productID,
            					@DefaultValue ("") @QueryParam("color") String color,
            					@DefaultValue("") @QueryParam("quantity") String quantity,
            					@DefaultValue("") @QueryParam("size") String size,
            					@Context SecurityContext sc)
	{
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		Connection conn = null;
		PreparedStatement stmt = null;
		String validation = validateProductCartInput(productID, quantity, color,size);
		if (!validation.equals(""))
		{
			return validation;
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
			cart_product_id = checkIfDuplicateItemExistsInCart(cart_id, Integer.parseInt(productID), color, size);
		} catch (NumberFormatException | URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		int cartQuantity = 0;
		String query = null;
		if (cart_product_id == -1)
		{
			return Util.generateJSONString("Success", "Nothing to remove from cart");
		} else {
			try {
				cartQuantity = getQuantity(cart_product_id);
                conn = DbConn.getConnection();
                conn.setAutoCommit(false);
				if (cartQuantity < Integer.parseInt(quantity)) {
					return Util.generateJSONString("Error", "Given quantity to delete is greater than quantity available in cart");
				} else if (cartQuantity == Integer.parseInt(quantity))
				{
					query = "DELETE from cart_products where cart_product_id = ?";
					stmt = conn.prepareStatement(query);
					stmt.setInt(1, cart_product_id);
				} else if (cartQuantity > Integer.parseInt(quantity))
				{
					query = "UPDATE cart_products SET quantity = quantity - ? where cart_product_id = ?";
					stmt = conn.prepareStatement(query);
					stmt.setInt(1, Integer.parseInt(quantity));
					stmt.setInt(2, cart_product_id);
				}
				stmt.executeUpdate();
				conn.commit();
			} catch (URISyntaxException | SQLException e) {
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
		}
		
		return Util.generateJSONString("Success", "Quantity " + Integer.parseInt(quantity) + " of product " + Integer.parseInt(productID) + " removed from cart");
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
		
		String validation = validateProductCartInput(productID, quantity, color,size);
		if (!validation.equals(""))
		{
			return validation;
		}
		
		try {
			if (!Product.validateProductColorAndSize(Integer.parseInt(productID), color, size))
			{
				return Util.generateJSONString("Error", "The specified color/size not found in the given product");
			}
			//return Product.validateProductColorAndSize(Integer.parseInt(productID), color, size);
		} catch (NumberFormatException | SQLException | URISyntaxException | JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
				query = "UPDATE cart_products set quantity = quantity + ? where cart_product_id = ?";
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, Integer.parseInt(quantity));
				stmt.setInt(2, cart_product_id);
			} else {
				query = "INSERT INTO cart_products (cart_id, product_id, quantity, color, size) VALUES (?,?,?,?,?)";
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
		
		return Util.generateJSONString("Success", "Item successfully added to cart");
	}
	
	/**
	 * @param shipping_addr
	 * @param state_code
	 * @param promo_code
	 * @param sc
	 * @return
	 */
	@POST
	@Secured
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public String checkOut(@DefaultValue("") @FormParam("shipping_addr") String shipping_addr,
			               @DefaultValue("") @FormParam("state") String state_code,
			               @DefaultValue ("") @FormParam("promo_code") String promo_code,
			               @Context SecurityContext sc)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		UserPrincipal user = (UserPrincipal) sc.getUserPrincipal();
		int user_id = user.getID();
		JSONObject ret = new JSONObject();
		double sales_tax_percentage = 0.0;
		double promo_code_discount_percentage = 0.0;
		double promo_code_discount = 0.0;
		double after_promo_code_discount = 0.0;
		double sales_tax_amount = 0.0;
		double after_tax_total = 0.0;
		
		if (shipping_addr == "")
		{
			return Util.generateJSONString("Error", "Shipping address is required");
		}
		
		if (state_code == "")
		{
			return Util.generateJSONString("Error", "Shipping state is required");
		}
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		
		CartResult cr = null;
		try {
			cr = buildCartItems(cart_id);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		ret.put("Type", "Success");
		ret.put("Shipping address", shipping_addr);
		ret.put("Tax state", state_code);
		ret.put("Items", cr.getItems());
		ret.put("Total number of items", cr.getTotal_items());
		ret.put("Total price before discount", cr.getTotal_price());
		ret.put("Total savings", cr.getTotal_discount());
		ret.put("Total price after discount", cr.getTotal_price() - cr.getTotal_discount());
		if(!promo_code.equals(""))
		{
		    try {
				promo_code_discount_percentage = getPromoCodeDiscount(promo_code);
			} catch (URISyntaxException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if (promo_code_discount_percentage == -1.0)
		    {
		    	return Util.generateJSONString("Error", "The promo code you entered is invalid. Please try again");	
		    }
		}
		ret.put("Promo code discount percentage", promo_code_discount_percentage + "%");
		promo_code_discount = (cr.getTotal_price() - cr.getTotal_discount())*(promo_code_discount_percentage/100);
		ret.put("Promo code discount", promo_code_discount);
		after_promo_code_discount = cr.getTotal_price() - cr.getTotal_discount() - promo_code_discount;
		ret.put("Total after promo code discount", after_promo_code_discount);
		try {
			sales_tax_percentage = getTaxForState(state_code);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "An internal server error occured " + e.getMessage());
		}
		if (sales_tax_percentage == -1.0)
		{
			return Util.generateJSONString("Error", "The state code you entered is invalid. Please try again");
		}
		ret.put("Sales tax percentage", sales_tax_percentage + "%");
		sales_tax_amount = after_promo_code_discount*(sales_tax_percentage/100);
		ret.put("Sales tax amount", sales_tax_amount);
		ret.put("Total after sales tax", sales_tax_amount + after_promo_code_discount);
		emptyCart(sc);
		return ret.toJSONString();
	}
	
}
