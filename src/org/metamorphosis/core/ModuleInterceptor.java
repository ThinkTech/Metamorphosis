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
			HttpServletRequest request = ServletActionContext.getRequest();
			String uri = request.getRequestURI();
			String actionURL = uri.substring(request.getContextPath().length()+1,uri.length());
			ModuleManager moduleManager = ModuleManager.getInstance();
		    Module module = moduleManager.getCurrentModule();
			if(module!=null) {
				HttpServletResponse response = ServletActionContext.getResponse();
				HttpSession session = request.getSession();
				User user = (User) session.getAttribute("user");
				if(module.isCached() && !module.isBackend()) {
					response.setHeader("Cache-control", "private, max-age=7200");
				}else if(module.isBackend() && !actionURL.endsWith("users/login") & !actionURL.endsWith("users/logout")
						& !actionURL.endsWith("users/register")) {
					response.setHeader("Cache-control","no-cache, no-store, must-revalidate");	
					if(user==null) return "error";
					String roles = module.getRoles();
					boolean found = false;
					for(Account account : user.getAccounts()) {
						if(roles.toLowerCase().indexOf(account.getRole()) !=-1 || roles.equals("all")) {
							found = true;
							user.setCurrentAccount(account);
							break;
						}
					}
					if(!found) return "error";
				}
				ValueStack stack = ActionContext.getContext().getValueStack();
				stack.set("modules",moduleManager.getVisibleModules(module.getType(),user));
				request.setAttribute("module",module);
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
				if(module.getUrl().equals(actionURL)) {
					String title = module.getTitle()!=null ? module.getTitle() : actionURL;
					request.setAttribute("title",title);
				}else {
					request.setAttribute("title",actionURL.substring(0, 1).toUpperCase() + actionURL.substring(1));
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
			}else {
				request.setAttribute("title",actionURL.substring(0, 1).toUpperCase() + actionURL.substring(1));
			}
			return invocation.invoke();
		}catch(Exception e) {
		}
		return "error";
	}
}