package org.metamorphosis.core;

import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Filter implements javax.servlet.Filter {
	
	protected FilterConfig config;
	protected FilterChain chain;
	protected ServletRequest request;
	protected ServletResponse response;

	@Override
	public void init(FilterConfig config) throws ServletException {	
		this.config = config;
		try {
			Method method = this.getClass().getDeclaredMethod("init");
			if(method!=null) method.invoke(this);
		} catch (NoSuchMethodException e) {
		}
        catch (Exception e) {
        	e.printStackTrace();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.request = request;
		this.response = response;
		this.chain = chain;
		try {
			this.getClass().getDeclaredMethod("filter").invoke(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
	}
	
	public HttpServletRequest getRequest() {
		HttpServletRequest wrapper = (HttpServletRequest) request.getAttribute("requestWrapper");
		if(wrapper == null) {
		  wrapper = new RequestWrapper((HttpServletRequest) request);
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
		ServletContext context = getRequest().getServletContext();
		ServletContext wrapper = (ServletContext) context.getAttribute("contextWrapper");
		if(wrapper == null) {
		  wrapper = new ContextWrapper(context);
		  context.setAttribute("contextWrapper",wrapper);
		}
		return wrapper;
	}
	
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) response;
	}

	public FilterConfig getConfig() {
		return config;
	}

	public FilterChain getChain() {
		return chain;
	}

}