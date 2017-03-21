package org.metamorphosis.core;

import javax.mail.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender {
	 
    private String user ="info@thinktech.sn";
    private String password ="California2003";
    private String me = "ThinkTech <info@thinktech.sn>";
    
    public void sendMail(String subject,String textBody,String sender,boolean cc) throws AddressException, MessagingException {
 
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
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(user));
        if(cc) {
        	msg.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(sender+","+me));
        } else {
        	msg.setRecipients(Message.RecipientType.TO,
        			InternetAddress.parse(sender));
        }
        msg.setSubject(subject);
        msg.setContent(textBody,"text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
         
    }
    
}