package com.app.user;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;


public class UserSecurityContext implements SecurityContext {

	UserPrincipal user;
	
	public UserSecurityContext(UserPrincipal user)
	{
		this.user = user;
	}
	
	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return this.user;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
