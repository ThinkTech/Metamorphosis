package org.metamorphosis.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

public class ContextAttributeListener extends AbstractListener implements ServletContextAttributeListener {

	protected ServletContextAttributeEvent event;
	
	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		this.event = event;
		execute("onAdd");
	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
		this.event = event;
		execute("onRemove");
	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {	
		this.event = event;
		execute("onReplace");
	}

	public ServletContextAttributeEvent getEvent() {
		return event;
	}
	
	public String getName() {
		return event.getName();
	}
	
	public ServletContext getContext() {
		ServletContext context = event.getServletContext();
		ServletContext wrapper = (ServletContext) context.getAttribute("contextWrapper");
		if(wrapper == null) {
		  wrapper = new ContextWrapper(context);
		  context.setAttribute("contextWrapper",wrapper);
		}
		return wrapper;
	}
	
	public Object getValue() {
		return event.getValue();
	}
	
}