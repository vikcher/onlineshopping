package com.app.user;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.app.dbconn.DbConn;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
	    
		String authorizationHeader = 
	            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("No authorization header provided");
        }
		
		String token = authorizationHeader.substring("Bearer".length()).trim();
		
        try {
			if (!validateToken(token))
			{
			    requestContext.abortWith(
			        Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} catch (SQLException | URISyntaxException e) {
			requestContext.abortWith(
			        Response.status(Response.Status.UNAUTHORIZED).build());
		}
    }
	
	
	private boolean validateToken(String token) throws SQLException, URISyntaxException
	{
	    Connection conn = null;
	    Statement stmt = null;
	    int count = 0;
		/* Retrieve User ID */
		String query = "SELECT COUNT(*) AS total from sessions where token = \'"+ token +"\'";
		try {
			conn = DbConn.getConnection();
			stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next())
    		{
    			count = rs.getInt("total");
    			if (count > 0)
    			{
    				return true;
    			}
    		}
		} catch (SQLException e) {
	        throw new SQLException();
		} catch (URISyntaxException e)
		{
			throw new URISyntaxException("", "");
		}
		
		return false;
	}

}
