package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Menu {

	protected String label;
	protected String icon;
	protected boolean visible = true;
	protected String position;
	protected final List<MenuItem> menuItems;

	public Menu() {
		menuItems = new ArrayList<MenuItem>();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getPosition() {
		return position != null ? position : "main";
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void addMenuItem(MenuItem item) {
		menuItems.add(item);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

}