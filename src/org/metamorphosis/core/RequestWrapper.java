package org.metamorphosis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public void propertyMissing(String property,Object value) {
		setAttribute(property,value);
	}
	
	public Object propertyMissing(String property) {
		Object value = getAttribute(property);
		return value != null ? value : getParameter(property);
	}

}
