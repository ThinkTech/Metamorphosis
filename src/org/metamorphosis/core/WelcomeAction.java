package org.metamorphosis.core;

import java.util.List;

@SuppressWarnings("serial")
public class WelcomeAction extends ActionSupport {
	
	public List<Module> getModules() {
		return getModuleManager().getVisibleModules("front-end");
	}
	
}
