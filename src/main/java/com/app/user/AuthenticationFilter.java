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
		UserPrincipal user = null;
		try {
			user = validateToken(token);
		} catch (SQLException | URISyntaxException e) {
			// TODO Auto-generated catch block
			requestContext.abortWith(
			        Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
		}
		
		if (user == null)
		{
			requestContext.abortWith(
			        Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
		requestContext.setSecurityContext(new UserSecurityContext(user));
    }
	
	
	private UserPrincipal validateToken(String token) throws SQLException, URISyntaxException
	{
		UserPrincipal authenticatedUser = null;
	    int count = 0;
		/* Retrieve User ID count */
		String query = "SELECT COUNT(*) AS total from sessions where token = \'"+ token +"\'";
		try {
    		ResultSet rs = Util.executeQuery(query);
    		while (rs.next())
    		{
    			count = rs.getInt("total");
    			if (count == 0)
    			{
    				return null;
    			}
    		}
		} catch (SQLException e) {
	        throw new SQLException();
		} catch (URISyntaxException e)
		{
			throw new URISyntaxException("", "");
		}
		
		query = "SELECT * from users where id = (SELECT user_id from sessions where token = \'" + token + "\')";
		try{
			ResultSet rs = Util.executeQuery(query);
			while (rs.next())
			{
				authenticatedUser = new UserPrincipal();
				authenticatedUser.setID(rs.getInt("id"));
				authenticatedUser.setUsername(rs.getString("username"));
				authenticatedUser.setFirstName(rs.getString("first_name"));
				authenticatedUser.setLastName(rs.getString("last_name"));
				authenticatedUser.setEmail(rs.getString("email"));
			}
		} catch (SQLException e)
		{
			throw new SQLException();
		} catch (URISyntaxException e)
		{
			throw new URISyntaxException("","");
		}
		
		return authenticatedUser;
	}

}
