package com.app.user;

import org.json.simple.JSONArray;

/**
 * @author vikcher
 * This is a convenience object in order to store the results of a user's cart.
 * Enables re-use for both view cart and check-out functions.
 */
public class CartResult {

	private double total_price;
	private double total_discount;
	private double total_price_after_discount;
	private double total_items;
	private JSONArray items;
	public double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}
	public double getTotal_discount() {
		return total_discount;
	}
	public void setTotal_discount(double total_discount) {
		this.total_discount = total_discount;
	}
	public double getTotal_price_after_discount() {
		return total_price_after_discount;
	}
	public void setTotal_price_after_discount(double total_price_after_discount) {
		this.total_price_after_discount = total_price_after_discount;
	}
	public JSONArray getItems() {
		return items;
	}
	public void setItems(JSONArray items) {
		this.items = items;
	}
	public double getTotal_items() {
		return total_items;
	}
	public void setTotal_items(double total_items) {
		this.total_items = total_items;
	}
	
		
}
