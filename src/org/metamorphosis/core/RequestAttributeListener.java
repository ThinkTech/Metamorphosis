package org.metamorphosis.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.http.HttpServletRequest;

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
	
	public ServletContext getContext() {
		ServletContext context = event.getServletContext();
		ServletContext wrapper = (ServletContext) context.getAttribute("contextWrapper");
		if(wrapper == null) {
		  wrapper = new ContextWrapper(context);
		  context.setAttribute("contextWrapper",wrapper);
		}
		return wrapper;
	}
	
	public HttpServletRequest getRequest() {
		HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
		HttpServletRequest wrapper = (HttpServletRequest) request.getAttribute("requestWrapper");
		if(wrapper == null) {
		  wrapper = new RequestWrapper(request);
		  request.setAttribute("requestWrapper",wrapper);
		}
		return wrapper;
	}
	
	public Object getValue() {
		return event.getValue();
	}
	
}