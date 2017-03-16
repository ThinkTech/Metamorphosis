package org.metamorphosis.core;

@SuppressWarnings("serial")
public class MailAction extends ActionSupport {

	private Mail mail;
	
	public void sendMail() {
		try {
			MailSender sender = new MailSender();
			sender.sendTextMessage(mail.getSubject(), mail.getMessage(),
				mail.getAuthor()+"<"+mail.getEmail()+">",true);
		} catch(Exception e){
			mail = null;
		}		
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

}