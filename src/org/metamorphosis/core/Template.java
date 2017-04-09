package org.metamorphosis.core;

public class Template extends Extension {

	protected boolean selected;
    
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
