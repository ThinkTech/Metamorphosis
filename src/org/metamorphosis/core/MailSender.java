package org.metamorphosis.core;

import javax.mail.PasswordAuthentication;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringEscapeUtils;

public class MailSender {
	 
    private MailConfig config;
    
    public MailSender(MailConfig config) {
    	this.config = config;
    }
    
    public void sendMail(Mail mail) {
    	try {
			sendMail(mail,false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public void sendMail(Mail mail,boolean cc) throws UnsupportedEncodingException {
        Session session = Session.getInstance(config.getProperties(),
      		  new Authenticator() {
      			protected PasswordAuthentication getPasswordAuthentication() {
      				return new PasswordAuthentication(config.getUser(), config.getPassword());
      			}
        });
        try {
	        final MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(config.getUser()));
	        if(cc) {
	        	message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(mail.getAuthor()+"<"+mail.getAddress()+">"+","+config.getUser()));
	        } else {
	        	message.setRecipients(Message.RecipientType.TO,
	        			InternetAddress.parse(mail.getAuthor()+"<"+mail.getAddress()+">"));
	        }
	        message.setSubject(StringEscapeUtils.unescapeHtml4(mail.getSubject()));
	        message.setContent(mail.getContent(),"text/html");
	        message.setSentDate(new Date());
	        new Thread(new Runnable() {
				public void run() {
					try {
						Transport.send(message);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}	
			}).start();
        } catch (MessagingException e) {
        	e.printStackTrace();
        }
    }

	public MailConfig getConfig() {
		return config;
	}

	public void setConfig(MailConfig config) {
		this.config = config;
	}
   
}