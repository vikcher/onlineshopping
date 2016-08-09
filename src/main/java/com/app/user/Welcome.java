package com.app.user;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Welcome {

	@GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
       return "<html> " + "<title>" + "Welcome page" + "</title>"
          + "<body><h1>" + "Welcome to shopping cart application! Please send REST API requests and get JSON responses" + "</body></h1>" + "</html> ";
	}
}
