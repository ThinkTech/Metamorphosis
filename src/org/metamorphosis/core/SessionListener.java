package org.metamorphosis.core;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
	
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
	
	protected void execute(String method) {		
        try {
			this.getClass().getDeclaredMethod(method).invoke(this);
		} catch (NoSuchMethodException e) {
		}
        catch (Exception e) {
        	e.printStackTrace();
		}
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

}
