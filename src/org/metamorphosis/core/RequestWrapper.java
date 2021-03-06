package org.metamorphosis.core;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import groovy.json.JsonSlurper;

public class RequestWrapper extends HttpServletRequestWrapper {

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public void propertyMissing(String property,Object value) {
		setAttribute(property,value);
	}
	
	public Object propertyMissing(String property) throws IOException {
		if(property.equals("body")) return new JsonSlurper().parse(getInputStream());
		Object value = getAttribute(property);
		return value != null ? value : getParameter(property);
	}
	
	public void remove(String name) {
		removeAttribute(name);
	}

}