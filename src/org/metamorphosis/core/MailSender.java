package org.metamorphosis.core;

import javax.mail.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender {
	 
    private String user ="info@thinktech.sn";
    private String password ="California2003";
    private String me = "ThinkTech <info@thinktech.sn>";
    
    public void sendMail(Mail mail,boolean cc)  {
 
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.thinktech.sn");
		props.put("mail.smtp.port", "25");
 
        // Get the Session object
        Session session = Session.getInstance(props,
      		  new Authenticator() {
      			protected PasswordAuthentication getPasswordAuthentication() {
      				return new PasswordAuthentication(user, password);
      			}
      		  });
 
        // Construct the message and send it.
        try {
	        final Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(user));
	        if(cc) {
	        	message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(mail.getAuthor()+"<"+mail.getAddress()+">"+","+me));
	        } else {
	        	message.setRecipients(Message.RecipientType.TO,
	        			InternetAddress.parse(mail.getAuthor()+"<"+mail.getAddress()+">"));
	        }
	        message.setSubject(mail.getSubject());
	        message.setContent(mail.getContent(),"text/html");
	        message.setSentDate(new Date());
	        Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						Transport.send(message);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}	
			});	
			thread.start();
        } catch (MessagingException e) {
        	e.printStackTrace();
        }
    }
}