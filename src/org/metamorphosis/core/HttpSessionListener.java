package org.metamorphosis.core;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

public class HttpSessionListener extends AbstractListener implements javax.servlet.http.HttpSessionListener {
	
	protected HttpSessionEvent event;

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		this.event = event;
		execute("onCreate");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		this.event = event;
		execute("onDestroy");
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

	public HttpSessionEvent getEvent() {
		return event;
	}
	

}