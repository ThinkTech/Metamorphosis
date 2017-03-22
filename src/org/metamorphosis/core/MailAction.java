package org.metamorphosis.core;

@SuppressWarnings("serial")
public class MailAction extends ActionSupport {

	private Mail mail;
	
	public void sendMail() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					MailSender mailSender = new MailSender();
					mailSender.sendMail(mail,true);
				} catch(Exception e){
					mail = null;
				}	
			}
		});	
		thread.start();
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

}