package org.metamorphosis.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Module extends Extension {
	
	protected String url;
	protected String title;
	protected boolean main;
	protected boolean cached;
	protected String script = "module.groovy";
	protected final List<Menu> menus = new ArrayList<Menu>();
	protected final List<Action> actions = new ArrayList<Action>();
	
	public Module() {
	}
	
	public Module(File folder) {
		setFolder(folder);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public boolean isCached() {
		return cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		if(!script.endsWith(".groovy")) script+=".groovy";
		this.script = script;
	}
	public List<Menu> getMenus() {
		return menus;
	}
	
	public void addMenu(Menu menu) {
		if(menu.getLabel()==null) menu.setLabel(name);
		menus.add(menu);
	}
	
	public List<Menu> getMenus(String position) {
		List<Menu> menus = new ArrayList<Menu>();
		for(Menu menu : this.menus) if(menu.getPosition().equals(position)) menus.add(menu);
		return menus;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void addAction(Action action) {
		actions.add(action);
	}
	
	public Action getAction(String url) {
		for(Action action : actions) if(action.getUrl()!=null && action.getUrl().equals(url)) return action;
		return null;
	}
	
}