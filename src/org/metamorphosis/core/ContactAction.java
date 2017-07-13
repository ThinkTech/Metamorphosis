package org.metamorphosis.core;

@SuppressWarnings("serial")
public class ContactAction extends ActionSupport {

	private Mail mail;
	
	public void sendMail() {
		MailSender mailSender = new MailSender();
		mailSender.sendMail(mail,true);		
	}
	
	public void subscribe() {
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

}