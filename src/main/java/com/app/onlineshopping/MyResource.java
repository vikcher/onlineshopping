package com.app.onlineshopping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.app.dbconn.DbConn;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        StringBuilder ret = new StringBuilder();
        
        Statement stmt = null;
    	String query = "select * from users";
    	try {
    		Connection conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ret.append(id);
                ret.append(name);
            }
    	} catch (Exception e)
    	{
    	     e.printStackTrace();
    	}
        return ret.toString();
    }
}
