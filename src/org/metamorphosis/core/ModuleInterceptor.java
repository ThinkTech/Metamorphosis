package org.metamorphosis.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.ValueStack;

@SuppressWarnings("serial")
public class ModuleInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation)  {
		try {
			ModuleManager moduleManager = ModuleManager.getInstance();
		    Module module = moduleManager.getCurrentModule();
			if(module!=null) {
				HttpServletRequest request = ServletActionContext.getRequest();
				String uri = request.getRequestURI();
				String actionURL = uri.substring(request.getContextPath().length()+1,uri.length());
				HttpServletResponse response = ServletActionContext.getResponse();
				if(module.isCached() && !module.isBackend()) {
					response.setHeader("Cache-control", "private, max-age=7200");
				}else if(module.isBackend() && !actionURL.endsWith("users/login") & !actionURL.endsWith("users/logout")) {
					response.setHeader("Cache-control","no-cache, no-store, must-revalidate");
					HttpSession session = request.getSession();
					User user = (User) session.getAttribute("user");
					if(user==null) return "error";
				}
				ValueStack stack = ActionContext.getContext().getValueStack();
				stack.set("modules",moduleManager.getVisibleModules(module.getType()));
				request.setAttribute("module",module);
				request.setAttribute("title",actionURL);
				request.setAttribute("js","modules/"+module.getId()+"/js");
				request.setAttribute("css","modules/"+module.getId()+"/css");
				request.setAttribute("images","modules/"+module.getId()+"/images");
				for(Menu menu : module.getMenus()) {
					for(MenuItem item : menu.getMenuItems()) {
						if(item.getName()!=null) {
							request.setAttribute(item.getName(),item.getUrl());
						}
						if(item.getUrl().equals(actionURL) && item.getTitle()!=null) {
							request.setAttribute("title",item.getTitle());
							break;
						}
					}
				}
				for(Action action : module.getActions()) {
					String url = module.getUrl()+"/"+action.getUrl();
					if(action.getName()!=null && !action.isGlobal()) {
					  request.setAttribute(action.getName(),url);
					}
					if(url.equals(actionURL) && action.getTitle()!=null) {
						request.setAttribute("title",action.getTitle());
					}
				}
			}
			return invocation.invoke();
		}catch(Exception e) {

		}
		return "error";
	}
}