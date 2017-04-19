package org.metamorphosis.core;

public class DatabaseInfo {

	private String host;
	private int port;
	private String name;
	private String user;
	private String password;
	
	public DatabaseInfo() {
		
	}
	
	public DatabaseInfo(String host, int port, String name, String user, String password) {
		super();
		this.host = host;
		this.port = port;
		this.name = name;
		this.user = user;
		this.password = password;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
}
