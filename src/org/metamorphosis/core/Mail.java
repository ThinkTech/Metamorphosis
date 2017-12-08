package org.metamorphosis.core;

public class Mail {

	private String author;
	private String address;
	private String subject;
	private String content;
	
	public Mail() {
		
	}
	
	public Mail(String author, String address, String subject, String content) {
		this.author = author;
		this.address = address;
		this.subject = subject;
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}