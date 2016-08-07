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

	@GET
	@Produces("application/json")
	public static String viewProductCategories()
	{
		Connection conn = null;
		Statement stmt = null;
		String query = "SELECT * from categories";
		int count = 0;
		JSONArray arr = new JSONArray();
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("ID", rs.getInt("category_id"));
				obj.put("Category_name", rs.getString("category_name"));
				obj.put("Category_description", rs.getString("category_description"));
				obj.put("Category discount","");
				arr.add(obj);
				count++;
			}
		} catch (SQLException | URISyntaxException e) {
			Util.generateJSONString("Error", "An internal error occured");
		}
		
		JSONObject finalJSON = new JSONObject();
		finalJSON.put("Type", "Success");
		finalJSON.put("Category Count", count);
		finalJSON.put("Category list", arr);
		return finalJSON.toJSONString();
	}
}
