package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String profession;
	private String email;
	private String password;
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
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	public void setCurrentAccount(Account current) {
		for(Account account : accounts) {
			account.setCurrent(false);
		}
		current.setCurrent(true);
	}
	public Account getCurrentAccount() {
		for(Account account : accounts) {
			if(account.isCurrent()) return account;
		}
		return null;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public Subscription getSubscription() {
		Subscription subscription;
		Account account = getCurrentAccount();
		subscription = account!= null && account.getStructure()!= null ? account.getStructure().getSubscription() : null;
		return subscription;
	}
}