package org.metamorphosis.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
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
	
	public Object getAction(Module module,String url) throws Exception{
		String reload = System.getenv("metamorphosis.reload");
		ModuleManager moduleManager = getModuleManager();
		return "true".equals(reload) ? moduleManager.buildAction(module,url) : moduleManager.buildAndCacheAction(module,url);
	}
	
	public Object getAction(Module module) throws Exception{
		return getAction(module,null);
	}
	
	public Object getService(Module module) throws Exception{
		return getAction(module,null);
	}
	
	public Object getService(String name) throws Exception{
		ModuleManager moduleManager = getModuleManager();
		Collection<Module> modules = moduleManager.getModules();
		for(Module module : modules){
			if(module.getName().equalsIgnoreCase(name)){
				return getAction(module,null);
			}
		}
		return null;
	}
	
	public void sendMail(String name,String email,String subject,String content){
		 MailConfig mailConfig = new MailConfig(getInitParameter("smtp.email"),getInitParameter("smtp.password"),getInitParameter("smtp.host"),getInitParameter("smtp.port"));
		 MailSender mailSender = new MailSender(mailConfig);
		 Mail mail = new Mail(name,email,subject,content);
		 mailSender.sendMail(mail);
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
	
	public Module getModule(String name){
		return getModuleManager().getModuleByName(name);
	}
	
	public TemplateManager getTemplateManager() {
		return TemplateManager.getInstance();
	}
	
	public Object getUser() {
		return getSession().getAttribute("user");
	}
	
	public String getBaseUrl() {
		HttpServletRequest request = getRequest();
		String forceHttps = System.getenv("metamorphosis.forceHttps");
	    String scheme = "true".equals(forceHttps) ? "https://" : "http://";
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
	
	public void forward(String location) throws ServletException, IOException {
		HttpServletRequest request = getRequest();
		request.getRequestDispatcher(location).forward(request, getResponse());
	}
	
	public void redirect(String location) throws IOException {
		getResponse().sendRedirect(location);
	}
	
	public void write(String content) throws IOException {
		getResponse().getWriter().write(content);
	}
	
	public void json(Object object) throws IOException{
		getResponse().setHeader("Content-Type", "application/json");
		write(groovy.json.JsonOutput.toJson(object));
	}
	
	public String stringify(Object object) throws IOException{
		return groovy.json.JsonOutput.toJson(object);
	}
	
	public Object parse(HttpServletRequest request) throws IOException{
		return new JsonSlurper().parse(request.getInputStream());
	}
	
	public Object parse(InputStream inputStream) throws IOException{
		return new JsonSlurper().parse(inputStream);
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
			Collection<Module> modules = moduleManager.getVisibleModules("front-end");
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