package org.metamorphosis.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("serial")
public class ContactAction extends ActionSupport {

	private Mail mail;
	
	public void sendMail() {
		MailSender mailSender = new MailSender();
		mailSender.sendMail(mail,true);		
	}
	
	public void subscribe() {
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("select * from newsletters where email = ?;");
			stmt.setString(1, mail.getAddress());
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()) {
				stmt = connection.prepareStatement("insert into newsletters(email,structure_id) values(?,?);");
				stmt.setString(1, mail.getAddress());
				stmt.setInt(2, 1);
				stmt.executeUpdate();
			}
			stmt.close();
			connection.close();
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
	 }
	
	private Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");  
		return DriverManager.getConnection("jdbc:mysql://localhost/general","root","thinktech");
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

}