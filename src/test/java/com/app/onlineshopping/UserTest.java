package com.app.onlineshopping;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;

import com.app.user.User;

import static org.junit.Assert.assertEquals;

public class UserTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(User.class);
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        final String responseMsg = target().path("myresource").request().get(String.class);

        assertEquals("", responseMsg);
    }
}
