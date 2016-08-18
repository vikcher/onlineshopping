package com.app.user;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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

@Path("/cart")
public class Cart {
	
	
	/**
	 * Validate the input parameters passed to 'AddToCart'. Checks if the product ID is valid and if passed color and size are valid strings
	 * @param productID
	 * @param quantity
	 * @param color
	 * @param size
	 * @return Error string if input is invalid, empty string is input is valid
	 */
	private String validateProductCartInput (String productID, String quantity, String color, String size)
	{
		try {
			if (!Product.checkIfProductExists(Integer.parseInt(productID)))
			{
				return Util.generateJSONString("Error", "702", "The specified product does not exist");	
			}
		} catch (NumberFormatException | URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured");
		}
		
		if (color.equals(""))
		{
			return Util.generateJSONString("Error", "704", "Please specify the color");
		}
		if (quantity.equals(""))
		{
			return Util.generateJSONString("Error", "704", "Please specify the quantity");
		}
		if (!StringUtils.isNumeric(quantity))
		{
			return Util.generateJSONString("Error", "703", "Wrong format of parameter quantity");
		}
		if (size.equals(""))
		{
			return Util.generateJSONString("Error", "704", "Please specify the size");
		}
		
		return "";
	}
	
	/**
	 * Get the Cart ID associated with a user.
	 * @param user_id
	 * @return Cart ID if it exists, -1 if it does not.
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
	
	private String generateConfirmationNumber(long seed) {
		Random random = new Random(seed);
		StringBuilder confirmation = new StringBuilder();
		for (int i = 0; i < 10; i++){
			int digit = random.nextInt(9);
			confirmation.append(digit);
		}
	    
		return confirmation.toString();
	}
	
	/**
	 * Given a state code, return the sales tax rate.
	 * @param state - state code
	 * @return Tax rate for the given state
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
	
	
	/**
	 * Check if a specified product with the same options exists in the cart. This is used to decide whether to update the same record in the cart or add a new one for insert/delete.
	 * @param cart_id
	 * @param product_id
	 * @param color
	 * @param size
	 * @return ID in the cart_products table if an entry exists, -1 otherwise
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
	
	
	/**
	 * Get the quantity associated with a particular product in the cart
	 * @param cart_product_id
	 * @return Quantity of items present in the particular cart for a particular product
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
	
	
	/**
	 * Check if cart is empty
	 * @param cart_id
	 * @return true if cart is empty
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
	public boolean isCartEmpty(int cart_id) throws URISyntaxException, SQLException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement("SELECT COUNT(*) AS count from cart_products where cart_id = ?");
			stmt.setInt(1, cart_id);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				if (rs.getInt("count") > 0)
				{
					return false;
				}
			}
		} finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
		}
		
		return true;
	}
	
	
	/**
	 * Returns the total discount for a particular product. The discount is the maximum of product or category.
	 * @param price - product price
	 * @param productID - product ID
	 * @param categoryID - category ID
	 * @return Total discount applicable for a particular product in the cart
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	public static double getTotalDiscount(double price, int productID, int categoryID) throws SQLException, URISyntaxException
	{
	    double productDiscount = Product.getProductDiscount(productID);
	    double categoryDiscount = Category.getCategoryDiscount(categoryID);
	    return (price*(double)(Math.max(productDiscount, categoryDiscount)/100));
	}
	
	
	/**
	 * Build all the items of a particular user's cart. This is used in both viewing the cart and in returning the checkout receipt.
	 * @param cart_id
	 * @return CartResult object with user's cart contents and price calculations.
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
				newObject.put("Product price per qty", rs.getDouble("product_price"));
				newObject.put("Quantity", rs.getInt("quantity"));
				newObject.put("Color", rs.getString("color"));
				newObject.put("Image URL", rs.getString("img_url"));
				newObject.put("Size", rs.getString("size"));
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
	
	
	/**
	 * Return the discount percentage associated with a promo code
	 * @param promo_code
	 * @return discount percentage associated with a promo code
	 * @throws URISyntaxException
	 * @throws SQLException
	 */
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
	
	
	/**
	 * @param sc - Injected securityContext variable to get user in the session.
	 * @return JSON object containing all the cart items and other information
	 */
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
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		CartResult cr = null;
		try {
			cr = buildCartItems(cart_id);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		ret.put("Type", "Success");
		ret.put("Items", cr.getItems());
		ret.put("Total number of items", cr.getTotal_items());
		ret.put("Total price before discount", cr.getTotal_price());
		ret.put("Total savings", cr.getTotal_discount());
		ret.put("Total price after discount", cr.getTotal_price() - cr.getTotal_discount());
		return ret.toJSONString();
		
	}
	
	
	/**
	 * Clear the user's cart
	 * @param sc
	 * @return Success/failure JSON message
	 */
	@DELETE
	@Secured
	@Produces("application/json")
	public String processCart(@Context SecurityContext sc)
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
			return Util.generateJSONString("Error", "800" ,"An internal server error occured ");
		}
		
		/*
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("DELETE from cart_products where cart_id = ?");
			stmt.setInt(1, cart_id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e){
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "800", "An internal server error occured ");
			}
		}
		*/
		
		String confirmationNumber = generateConfirmationNumber(System.currentTimeMillis());
		try {
			conn = DbConn.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT into orders (conf, user_id) values (?,?)");
			stmt.setString(1, confirmationNumber);
			stmt.setInt(2, user_id);
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e)
			{
				return Util.generateJSONString("Error", "800", "An internal server error occured ");
			}
		}
		
		JSONObject ret = new JSONObject();
		ret.put("confirmation", confirmationNumber);
		ret.put("Type", "Success");
		ret.put("Response Code", "600");
		ret.put("Message", "Your cart has been processed");		
	    return ret.toJSONString();	
	}
	
	/**
	 * Remove specific item from cart. If the quantity specified is less than quantity in the cart, the quantity is merely decrememnted. Otherwise, the cart item is removed.
	 * @param productID
	 * @param color
	 * @param quantity
	 * @param size
	 * @param sc
	 * @return Success or failure JSON string.
	 */
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
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		if (cart_id == -1)
		{
			return Util.generateJSONString("Error", "800", "An internal server error occured");
		}
		
		int cart_product_id = 0;
		try {
			cart_product_id = checkIfDuplicateItemExistsInCart(cart_id, Integer.parseInt(productID), color, size);
		} catch (NumberFormatException | URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		int cartQuantity = 0;
		String query = null;
		if (cart_product_id == -1)
		{
			return Util.generateJSONString("Success", "601", "Nothing to remove from cart");
		} else {
			try {
				cartQuantity = getQuantity(cart_product_id);
                conn = DbConn.getConnection();
                conn.setAutoCommit(false);
				if (cartQuantity < Integer.parseInt(quantity)) {
					return Util.generateJSONString("Error", "703", "Given quantity to delete is greater than quantity available in cart");
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
				return Util.generateJSONString("Error", "800", "An internal server error occured ");
			} finally {
				try {
					if (stmt != null) stmt.close();
					conn.setAutoCommit(true);
					if (conn != null) conn.close();
				} catch (SQLException e) {
					return Util.generateJSONString("Error", "800", "An internal server error occured ");
				}
			}
		}
		
		return Util.generateJSONString("Success", "600", "Quantity " + Integer.parseInt(quantity) + " of product " + Integer.parseInt(productID) + " removed from cart");
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
				return Util.generateJSONString("Error", "702", "The specified color/size not found in the given product");
			}
		} catch (NumberFormatException | SQLException | URISyntaxException | JSONException e1) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		if (cart_id == -1)
		{
			return Util.generateJSONString("Error", "800", "An internal server error occured got cart id -1");
		}
		
		int cart_product_id = 0;
		try {
			cart_product_id = checkIfDuplicateItemExistsInCart(cart_id, Integer.valueOf(productID), color, size);
		} catch (SQLException | URISyntaxException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
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
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		} finally {
			try {
				if (stmt != null) stmt.close();
				conn.setAutoCommit(true);
				if (conn != null) conn.close();
			} catch (SQLException e) {
				return Util.generateJSONString("Error", "800", "An internal server error occured ");
			}
		}
		
		return Util.generateJSONString("Success", "600", "Item successfully added to cart");
	}
	
	/**
	 * @param shipping_addr
	 * @param state_code
	 * @param promo_code
	 * @param sc
	 * @return JSON string containing the checkout details and final price.
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
		
		int cart_id = 0;
		try {
			cart_id = getCartID(user_id);
		} catch (SQLException | URISyntaxException e)
		{
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		
		try {
			if (isCartEmpty(cart_id))
			{
				return Util.generateJSONString("Success", "601", "No items in cart to checkout");
			}
		} catch (URISyntaxException | SQLException e1) {
			return Util.generateJSONString("Error", "800", "An internal server error occured " + e1.getMessage());	
		}
		
		if (shipping_addr == "")
		{
			return Util.generateJSONString("Error", "704", "Shipping address is required");
		}
		
		if (state_code == "")
		{
			return Util.generateJSONString("Error", "704", "Shipping state is required");
		}
		
		try {
			sales_tax_percentage = getTaxForState(state_code);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		if (sales_tax_percentage == -1.0)
		{
			return Util.generateJSONString("Error", "703", "The state code you entered is invalid. Please try again");
		}
		
		if(!promo_code.equals(""))
		{
		    try {
				promo_code_discount_percentage = getPromoCodeDiscount(promo_code);
			} catch (URISyntaxException | SQLException e) {
				return Util.generateJSONString("Error", "800", "An internal server error occured ");
			}
		    if (promo_code_discount_percentage == -1.0)
		    {
		    	return Util.generateJSONString("Error", "703", "The promo code you entered is invalid. Please try again");	
		    }
		}
		
		CartResult cr = null;
		try {
			cr = buildCartItems(cart_id);
		} catch (URISyntaxException | SQLException e) {
			return Util.generateJSONString("Error", "800", "An internal server error occured ");
		}
		ret.put("Type", "Success");
		ret.put("Shipping address", shipping_addr);
		ret.put("Tax state", state_code);
		ret.put("Items", cr.getItems());
		ret.put("Total number of items", cr.getTotal_items());
		ret.put("Total price before discount", cr.getTotal_price());
		ret.put("Total savings", cr.getTotal_discount());
		ret.put("Total price after discount", cr.getTotal_price() - cr.getTotal_discount());
		ret.put("Promo code discount percentage", promo_code_discount_percentage);
		promo_code_discount = (cr.getTotal_price() - cr.getTotal_discount())*(promo_code_discount_percentage/100);
		ret.put("Promo code discount", promo_code_discount);
		after_promo_code_discount = cr.getTotal_price() - cr.getTotal_discount() - promo_code_discount;
		ret.put("Total after promo code discount", after_promo_code_discount);
		ret.put("Sales tax percentage", sales_tax_percentage);
		sales_tax_amount = after_promo_code_discount*(sales_tax_percentage/100);
		ret.put("Sales tax amount", sales_tax_amount);
		ret.put("Total after sales tax", sales_tax_amount + after_promo_code_discount);
		//emptyCart(sc);
		return ret.toJSONString();
	}
	
}
