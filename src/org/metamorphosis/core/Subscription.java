package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Subscription {

	private List<Module> modules = new ArrayList<Module>();

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
}