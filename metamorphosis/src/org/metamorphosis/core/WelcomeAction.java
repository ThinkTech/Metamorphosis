package org.metamorphosis.core;

import java.util.List;

@SuppressWarnings("serial")
public class WelcomeAction extends ActionSupport {

	public String execute() {
		getRequest().setAttribute("title", "Home");
		return SUCCESS;
	}
	
	public List<Module> getModules() {
		return getModuleManager().getVisibleModules("front-end");
	}
	
}
