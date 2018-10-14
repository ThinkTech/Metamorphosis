package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Menu {

	private String label;
	private String icon;
	private boolean visible = true;
	private String position;
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	public Menu() {
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

	public Menu(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public void addMenuItem(MenuItem item) {
		menuItems.add(item);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

}