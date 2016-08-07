package com.app.user;

import java.security.Principal;

//Security Principal for User
public class UserAuth implements Principal {

	private String firstName, lastName, username, email;
	private int id;
	
	
	public void setFirstName(String fname) {firstName = fname;}
	public void setLastName(String lname) {lastName = lname;}
	public void setUsername(String uname) {username = uname;}
	public void setEmail(String email) {this.email = email;}
	public void setID(int id) {this.id = id;}
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public String getUserName() {return username;}
	public String getEmail() {return email;}
	public int getID() {return id;}
	
	
	@Override
	public String getName() {
		return firstName + " " + lastName;
	}
    
}
