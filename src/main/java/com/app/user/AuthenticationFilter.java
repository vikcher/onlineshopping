package com.app.user;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.app.dbconn.DbConn;

/**
 * @author vikcher
 * AuthenticationFilter is used to filter requests to all the APIs which need the user to be logged in.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	
	/* Filters incoming requests */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
	    
		String authorizationHeader = 
	            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			requestContext.abortWith(
			        Response.ok(Util.generateJSONString("Error", "700", "Not authorized"), MediaType.APPLICATION_JSON).build());
        }
		
		String token = authorizationHeader.substring("Bearer".length()).trim();
		UserPrincipal user = null;
		try {
			user = validateToken(token);
		} catch (SQLException | URISyntaxException e) {
			// TODO Auto-generated catch block
			requestContext.abortWith(
			        Response.ok(Util.generateJSONString("Error", "800", "An internal server error occured"), MediaType.APPLICATION_JSON).build());
		}
		
		if (user == null)
		{
			requestContext.abortWith(
			        Response.ok(Util.generateJSONString("Error", "700", "Not authorized"), MediaType.APPLICATION_JSON).build());
		}
		
		requestContext.setSecurityContext(new UserSecurityContext(user));
    }
	
	
	/**
	 * @param token
	 * @return User corresponding to the token if the token is valid, null otherwise
	 * @throws SQLException
	 * @throws URISyntaxException
	 */
	private UserPrincipal validateToken(String token) throws SQLException, URISyntaxException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		UserPrincipal authenticatedUser = null;
	    int count = 0;
		String query = "SELECT COUNT(*) AS total from sessions where token = ?";
		ResultSet rs = null;
		try {
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, token);
    	    rs = stmt.executeQuery();
     	    while (rs.next())
    	    {
    		    count = rs.getInt("total");
    		    if (count == 0)
    		    {
    			    return null;
    		    }
    	    }
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		
		query = "SELECT * from users where id = (SELECT user_id from sessions where token = ?)";
		try{
			conn = DbConn.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setString(1, token);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				authenticatedUser = new UserPrincipal();
				authenticatedUser.setID(rs.getInt("id"));
				authenticatedUser.setUsername(rs.getString("username"));
				authenticatedUser.setFirstName(rs.getString("first_name"));
				authenticatedUser.setLastName(rs.getString("last_name"));
				authenticatedUser.setEmail(rs.getString("email"));
			}
		} finally {
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
			if (conn != null) conn.close();
		}
		
		return authenticatedUser;
	}

}
