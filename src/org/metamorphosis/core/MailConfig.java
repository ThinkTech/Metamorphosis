package org.metamorphosis.core;

import java.util.Properties;

public class MailConfig {
	
	private String user;
    private String password;
    Properties properties = new Properties();
    
    public MailConfig(String user,String password,String host) {
    	this.user = user;
    	this.password = password;
    	properties.put("mail.smtp.auth", "true");
 		properties.put("mail.smtp.starttls.enable", "true");
 		properties.put("mail.smtp.host", host);
 		properties.put("mail.smtp.port", "25");
    }
    
    public MailConfig(String user,String password,String host,String port) {
    	this.user = user;
    	this.password = password;
    	properties.put("mail.smtp.auth", "true");
 		properties.put("mail.smtp.starttls.enable", "true");
 		properties.put("mail.smtp.host", host);
 		properties.put("mail.smtp.port", port);
    }

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
    
}