package org.metamorphosis.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestListener implements ServletRequestListener {

	protected ServletRequestEvent event;
	
	@Override
	public void requestInitialized(ServletRequestEvent event) {
		this.event = event;
		execute("onInit");
	}
	
	@Override
	public void requestDestroyed(ServletRequestEvent event) {
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
	
	public HttpServletRequest getRequest() {
		HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
		HttpServletRequest wrapper = (HttpServletRequest) request.getAttribute("requestWrapper");
		if(wrapper == null) {
		  wrapper = new RequestWrapper(request);
		  request.setAttribute("requestWrapper",wrapper);
		}
		return wrapper;
	}
	
	public HttpSession getSession() {
		HttpSession session = getRequest().getSession(true);
		HttpSession wrapper = (HttpSession) session.getAttribute("sessionWrapper");
		if(wrapper == null) {
		  wrapper = new SessionWrapper(session);
		  session.setAttribute("sessionWrapper",wrapper);
		}
		return wrapper;
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
	
}