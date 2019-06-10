package org.metamorphosis.core;

import static groovy.json.JsonOutput.toJson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.struts2.ServletActionContext;
import groovy.json.JsonSlurper;

@SuppressWarnings("serial")
public class HttpServlet extends javax.servlet.http.HttpServlet {

	public void doGet(HttpServletRequest request,HttpServletResponse response) {		
		serve("get");
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response) {		
      	serve("post");
	}
	
	public void doPut(HttpServletRequest request,HttpServletResponse response) {		
        serve("put");
		
	}
	
	public void doDelete(HttpServletRequest request,HttpServletResponse response) {		
        serve("delete");
	}
	
	public void doHead(HttpServletRequest request,HttpServletResponse response) {		
        serve("head");
	}
	
	public void doTrace(HttpServletRequest request,HttpServletResponse response) {		
        serve("trace");
	}
	
	public void doOptions(HttpServletRequest request,HttpServletResponse response) {		
        serve("options");
	}
	
	private void serve(String method) {		
        try {
			this.getClass().getDeclaredMethod(method).invoke(this);
		} catch (Exception e) {
			getRequest().setAttribute("message",e.getCause().getMessage());
			forward("/500.jsp");
			e.printStackTrace();
		}
	}
	
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletRequest wrapper = (HttpServletRequest) request.getAttribute("requestWrapper");
		if(wrapper == null) {
		  wrapper = new RequestWrapper(request);
		  request.setAttribute("requestWrapper",wrapper);
		}
		return wrapper;
	}
	
	public HttpSession getSession() {
		HttpSession session = ServletActionContext.getRequest().getSession(true);
		HttpSession wrapper = (HttpSession) session.getAttribute("sessionWrapper");
		if(wrapper == null) {
		  wrapper = new SessionWrapper(session);
		  session.setAttribute("sessionWrapper",wrapper);
		}
		return wrapper;
	}
	
	public ServletContext getContext() {
		ServletContext context = ServletActionContext.getServletContext();
		ServletContext wrapper = (ServletContext) context.getAttribute("contextWrapper");
		if(wrapper == null) {
		  wrapper = new ContextWrapper(context);
		  context.setAttribute("contextWrapper",wrapper);
		}
		return wrapper;
	}
	
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
	
	public Object getAction(Module module,String url) throws Exception {
		return  getModuleManager().getAction(module,url);
	}
	
	public Object getAction(Module module) throws Exception {
		return getAction(module,null);
	}
	
	public Object getAction(String url) throws Exception {
		return getAction(getModule(),url);
	}
	
	public Object getService(Module module) throws Exception {
		return getAction(module,null);
	}
	
	public Object getService(String name) throws Exception {
		Module module = getModuleManager().getModuleByName(name);
		return module != null ? getAction(module,null) : null;
	}
	
	public void sendMail(String email,String subject,String message) throws Exception {
		 final HtmlEmail mail = new HtmlEmail();
		 mail.setHostName(getInitParameter("smtp.host"));
		 mail.setSmtpPort(Integer.parseInt(getInitParameter("smtp.port")));
		 mail.setAuthenticator(new DefaultAuthenticator(getInitParameter("smtp.email"),getInitParameter("smtp.password")));
		 mail.setSSLOnConnect(true);
		 mail.addTo(email);
		 mail.setFrom(getInitParameter("smtp.email"));
		 mail.setSubject(StringEscapeUtils.unescapeHtml4(subject));
		 mail.setHtmlMsg(message);
		 new Thread(new Runnable() {
			public void run() {
			  try {
				mail.send();
			  } catch (EmailException e) {
				e.printStackTrace();
			  }
			}
		}).start();
	}
	
	public ModuleManager getModuleManager() {
		return ModuleManager.getInstance();
	}
	
	public Module getModule() {
		return getModuleManager().getCurrentModule();
	}
	
	public Module getModule(String name) {
		return getModuleManager().getModuleByName(name);
	}
	
	public TemplateManager getTemplateManager() {
		return TemplateManager.getInstance();
	}
	
	public Object getUser() {
		return ServletActionContext.getRequest().getSession().getAttribute("user");
	}
	
	public String getBaseUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String forceHttps = System.getenv("metamorphosis.forceHttps");
	    String scheme = "true".equals(forceHttps) ? "https://" : "http://";
	    String serverName = request.getServerName();
	    String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();
	    String contextPath = request.getContextPath();
	    return scheme + serverName + serverPort + contextPath;
    }
	
	public String getInitParameter(String name) {
		return ServletActionContext.getServletContext().getInitParameter(name);
	}
	
	public String getContextPath() {
		return ServletActionContext.getRequest().getContextPath();
	}
	
	public void forward(String location) {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			request.getRequestDispatcher(location).forward(request, getResponse());
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void redirect(String location) throws IOException {
		getResponse().sendRedirect(location);
	}
	
	public void write(String content) throws IOException {
		getResponse().getWriter().write(content);
	}
	
	public String readFile(Module module,String fileName) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File(module.getFolder()+"/"+fileName)),1024);
	    String content = "";
	    StringBuffer buffer = new StringBuffer();
	    while((content = reader.readLine()) != null) buffer.append(content);
	    reader.close();
	    return buffer.toString();
	}
	
	public String readFile(String fileName) throws Exception {
		return readFile(getModule(),fileName);
	} 
	
	public void json(Object object) throws IOException {
		getResponse().setHeader("Content-Type", "application/json");
		write(toJson(object));
	}
	
	public String stringify(Object object) throws IOException {
		return toJson(object);
	}
	
	public Object parse(InputStream inputStream) throws IOException {
		return new JsonSlurper().parse(inputStream);
	}
	
	public DataSource getDataSource() {
		return (DataSource) ServletActionContext.getServletContext().getAttribute("datasource");
	}
	
	public Object getConnection()  {
		 return ServletActionContext.getRequest().getAttribute("connection");
    }

	public String getReferer() {
		return ServletActionContext.getRequest().getHeader("referer");
	}
	
	public String getLanguage() {
		return ServletActionContext.getRequest().getLocale().getLanguage();
	}
}