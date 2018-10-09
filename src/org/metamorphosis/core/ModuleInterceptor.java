package org.metamorphosis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

@SuppressWarnings("serial")
public class ModuleInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation)  {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			String uri = request.getRequestURI();
			String actionURL = uri.substring(request.getContextPath().length()+1,uri.length());
			ModuleManager moduleManager = ModuleManager.getInstance();
		    Module module = moduleManager.getCurrentModule();
			if(module!=null) {
				HttpServletResponse response = ServletActionContext.getResponse();
				if(module.isCached() && !module.isBackend()) {
					response.setHeader("Cache-control", "private, max-age=7200");
				}else if(module.isBackend() && !actionURL.startsWith("users/")) {
					HttpSession session = request.getSession();
					Object user = (Object) session.getAttribute("user");
					if(user==null) return "error";
					response.setHeader("Cache-control","no-cache, no-store, must-revalidate");
				}
				request.setAttribute("modules",moduleManager.getVisibleModules(module.getType()));
				request.setAttribute("module",module);
				request.setAttribute("url",module.getUrl());
				request.setAttribute("js",module.getPath("js"));
				request.setAttribute("css",module.getPath("css"));
				request.setAttribute("images",module.getPath("images").substring(1));
				for(Menu menu : module.getMenus()) {
					for(MenuItem item : menu.getMenuItems()) {
						if(item.getUrl().equals(actionURL)) {
							request.setAttribute("activeItem",item);
							request.setAttribute("title",item.getTitle());
							break;
						}
					}
				}
				if(module.getUrl().equals(actionURL)) {
					String title = module.getTitle()!=null ? module.getTitle() : actionURL;
					request.setAttribute("title",title);
				}else {
					request.setAttribute("title",actionURL.substring(0, 1).toUpperCase() + actionURL.substring(1));
					for(Action action : module.getActions()) {
						String url = module.getUrl()+"/"+action.getUrl();
						if(url.equals(actionURL) && action.getTitle()!=null) {
							request.setAttribute("title",action.getTitle());
						}
					}
				}
				if(actionURL.equals(module.getUrl())){
					request.getRequestDispatcher(module.getUrl()+"/index").forward(request, response);
				}
			}else {
				request.setAttribute("title",actionURL.substring(0, 1).toUpperCase() + actionURL.substring(1));
				request.setAttribute("actionURL", actionURL);
			}
			return invocation.invoke();
		}catch(Exception e) {
		}
		return "error";
	}
}