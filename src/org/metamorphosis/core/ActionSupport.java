package org.metamorphosis.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionContext;

@SuppressWarnings("serial")
public class ActionSupport extends com.opensymphony.xwork2.ActionSupport {

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
	
	public String getInitParameter(String name) {
		return getRequest().getServletContext().getInitParameter(name);
	}
	
	public String getParameter(String name) {
		return getRequest().getParameter(name);
	}
	
	public String getContextPath() {
		return getRequest().getContextPath();
	}
	
	public void setAttribute(String key,Object object) {
		getRequest().setAttribute(key,object);
	}
	
	public void redirect(String location) throws IOException {
		getResponse().sendRedirect(location);
	}

	public String getReferer() {
		return getRequest().getHeader("referer");
	}
	
	public String navigate() throws ServletException, IOException {
		HttpServletRequest request = getRequest();
		String actionURL = (String) request.getAttribute("actionURL");
		if(actionURL!=null) {
			ModuleManager moduleManager = ModuleManager.getInstance();
			List<Module> modules = moduleManager.getVisibleModules("front-end",null);
			for(Module module : modules) {
				if(module.isMain()) {
					String url = module.getUrl()+"/"+actionURL;
					Action action = module.getAction(actionURL);
					if(action!=null) { 
					    request.getRequestDispatcher(url).forward(request, getResponse());
					    return null;
					}else {
						for(Menu menu : module.getMenus()) {
							for(MenuItem item : menu.getMenuItems()) {
								if(item.getUrl().equals(actionURL)) {
									request.getRequestDispatcher(url).forward(request, getResponse());
								    return null;
								}
							}
						}
					}
				}
			}
			
		}
		return SUCCESS;
	}
}