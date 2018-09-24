package org.metamorphosis.core;

import java.io.File;

public class Template extends Extension {

	private String index = "index.jsp";
	protected boolean selected;
	
	public Template(){
	}
	
	public Template(File folder){
		setFolder(folder);
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getThumbnail() {
		return getPath("thumbnail.png");
	}
	
	
	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex() {
		return getPath(index);
	}
	
}