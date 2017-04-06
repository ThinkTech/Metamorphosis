package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private Long id;
	private String firstName;
	private String lastName;
	private List<Account> accounts = new ArrayList<Account>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	public String getFullName() {
		return firstName + " " + lastName;
	}
}