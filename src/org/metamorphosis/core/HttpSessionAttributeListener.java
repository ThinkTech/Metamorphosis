package org.metamorphosis.core;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

public class HttpSessionAttributeListener extends AbstractListener implements javax.servlet.http.HttpSessionAttributeListener {

	protected HttpSessionBindingEvent event;
	
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		this.event = event;
		execute("onAdd");
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		this.event = event;
		execute("onRemove");
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {	
		this.event = event;
		execute("onReplace");
	}

	public HttpSessionBindingEvent getEvent() {
		return event;
	}
	
	public String getName() {
		return event.getName();
	}
	
	public HttpSession getSession() {
		HttpSession session = event.getSession();
		HttpSession wrapper = (HttpSession) session.getAttribute("sessionWrapper");
		if(wrapper == null) {
		  wrapper = new SessionWrapper(session);
		  session.setAttribute("sessionWrapper",wrapper);
		}
		return wrapper;
	}
	
	public Object getValue() {
		return event.getValue();
	}
	
}