package org.metamorphosis.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/index.html")
public class WelcomeServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {	
		String cache = System.getenv("metamorphosis.cache");
		if(cache!=null) response.setHeader("Cache-control", "private, max-age="+cache);
		forward(request,response);
	}
	
	private void forward(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Module module =  ModuleManager.getInstance().getMainModule("front-end");
		if(module!=null)
			request.getRequestDispatcher(module.getUrl()+"/index").forward(request,response);
		else
			request.getRequestDispatcher("index").forward(request,response);
	}
	
}