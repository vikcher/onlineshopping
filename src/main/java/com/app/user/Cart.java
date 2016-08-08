package com.app.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("cart")
public class Cart {

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
			                @QueryParam("quantity") String quantity,
			                @QueryParam("size") String size)
	{
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
