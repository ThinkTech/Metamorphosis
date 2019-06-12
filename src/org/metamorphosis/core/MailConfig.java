package org.metamorphosis.core;

public class MailConfig {
	
	private String host;
	private int port;
	private String email;
	private String password;
	
	public MailConfig() {
		
	}

	public MailConfig(String host, String email, String password) {
		this.host = host;
		this.port = 25;
		this.email = email;
		this.password = password;
	}
	
	public MailConfig(String host, int port, String email, String password) {
		this(host,email,password);
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
	
}