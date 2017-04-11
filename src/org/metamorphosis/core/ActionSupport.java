package org.metamorphosis.core;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionContext;

@SuppressWarnings("serial")
public class ActionSupport extends com.opensymphony.xwork2.ActionSupport {

	private Search search = new Search();
	
	
	public String execute() {
		HttpServletRequest request = getRequest();
		String uri = request.getRequestURI();
		String actionURL = uri.substring(request.getContextPath().length()+1,uri.length());
		if(request.getAttribute("title")!=null) request.setAttribute("title",actionURL);
		return SUCCESS;
	}
	
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
	
	public HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}
	
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
	
	@SuppressWarnings("rawtypes")
	public Map getApplication() {
		return (Map) ActionContext.getContext().get("application");
	}
	
	public ServletContext getServletContext() {
		return getModuleManager().getServletContext();
	}
	
	public ModuleManager getModuleManager() {
		return ModuleManager.getInstance();
	}
	public Module getModule() {
		return getModuleManager().getCurrentModule();
	}
	public TemplateManager getTemplateManager() {
		return TemplateManager.getInstance();
	}
	
	public User getLoggedUser() {
		return (User) getSession().getAttribute("user");
	}
	
	public String getLogo() {
		return "images/logo.png";
	}
	
	public String getBaseUrl() {
		HttpServletRequest request = getRequest();
	    String scheme = request.getScheme() + "://";
	    String serverName = request.getServerName();
	    String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
	    String contextPath = request.getContextPath();
	    return scheme + serverName + serverPort + contextPath;
    }

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}
	
	public String getReferer() {
		return getRequest().getHeader("referer");
	}
}
