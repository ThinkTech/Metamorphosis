package org.metamorphosis.core;

import java.io.File;

public class Template extends Extension {

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
		return "templates/"+id+"/thumbnail.png";
	}
	
	public String getIndexPage() {
		return "/templates/"+id+"/index.jsp";
	}
	
}