package org.metamorphosis.core;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener extends AbstractListener implements HttpSessionListener {
	
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