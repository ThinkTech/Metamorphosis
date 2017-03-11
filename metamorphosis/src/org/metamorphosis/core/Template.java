package org.metamorphosis.core;

public class Template extends Extension {

	protected boolean selected;
    protected String loginPage = "login.jsp";
    protected String registrationPage = "register.jsp";
    
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	public String getRegistrationPage() {
		return registrationPage;
	}

	public void setRegistrationPage(String registrationPage) {
		this.registrationPage = registrationPage;
	}

	public String getThumbnail() {
		return "templates/"+id+"/thumbnail.png";
	}
	
	public String getIndexPage() {
		return "/templates/"+id+"/index.jsp";
	}
	
}
