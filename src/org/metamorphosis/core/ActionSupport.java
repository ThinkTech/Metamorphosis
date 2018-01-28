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

import groovy.json.JsonSlurper;

@SuppressWarnings("serial")
public class ActionSupport extends com.opensymphony.xwork2.ActionSupport {

	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
	
	public HttpSession getSession() {
		return getRequest().getSession(true);
	}
	
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
	
	@SuppressWarnings("rawtypes")
	public Map getApplication() {
		return (Map) ActionContext.getContext().get("application");
	}
	
	public ServletContext getContext() {
		return getModuleManager().getServletContext();
	}
	
	public ModuleManager getModuleManager() {
		return ModuleManager.getInstance();
	}
	public Module getCurrentModule() {
		return getModuleManager().getCurrentModule();
	}
	public TemplateManager getTemplateManager() {
		return TemplateManager.getInstance();
	}
	
	public Object getUser() {
		return getSession().getAttribute("user");
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
	
	public void write(String content) throws IOException {
		getResponse().getWriter().write(content);
	}
	
	public String json(Object object){
		return groovy.json.JsonOutput.toJson(object);
	}
	
	public Object parse(HttpServletRequest request) throws IOException{
		return new JsonSlurper().parse(request.getInputStream());
	}
	
	public Object getDataSource(){
		return getContext().getAttribute("datasource");
	}

	public String getReferer() {
		return getRequest().getHeader("referer");
	}
	
	public String getLanguage() {
		return getRequest().getLocale().getLanguage();
	}
	
	public String navigate() throws ServletException, IOException {
		HttpServletRequest request = getRequest();
		String actionURL = (String) request.getAttribute("actionURL");
		if(actionURL!=null) {
			ModuleManager moduleManager = ModuleManager.getInstance();
			List<Module> modules = moduleManager.getVisibleModules("front-end");
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