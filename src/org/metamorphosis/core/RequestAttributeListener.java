package org.metamorphosis.core;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

public class RequestAttributeListener extends AbstractListener implements ServletRequestAttributeListener {

	protected ServletRequestAttributeEvent event;
	
	@Override
	public void attributeAdded(ServletRequestAttributeEvent event) {
		this.event = event;
		execute("onAdd");
	}

	@Override
	public void attributeRemoved(ServletRequestAttributeEvent event) {
		this.event = event;
		execute("onRemove");
	}

	@Override
	public void attributeReplaced(ServletRequestAttributeEvent event) {	
		this.event = event;
		execute("onReplace");
	}

	public ServletRequestAttributeEvent getEvent() {
		return event;
	}
	
	public String getName() {
		return event.getName();
	}
	
	public Object getValue() {
		return event.getValue();
	}
	
}