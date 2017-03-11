package org.metamorphosis.core;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/index.html")
public class WelcomeServlet extends HttpServlet {

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {	
		Module module = getHomeModule();
		if(module!=null)
			request.getRequestDispatcher(module.getUrl()).forward(request, response);
		else
			request.getRequestDispatcher("index").forward(request, response);
	}
	
	public Module getHomeModule() {
		ModuleManager moduleManager = ModuleManager.getInstance();
		List<Module> modules = moduleManager.getVisibleModules("front-end");
		for(Module module : modules) if(module.isMain()) return module;
		return null;	
	}
	
}
