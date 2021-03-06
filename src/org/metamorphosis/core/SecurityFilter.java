package org.metamorphosis.core;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class SecurityFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String requestURI = httpServletRequest.getRequestURI();
		if(requestURI.indexOf(".groovy")!=-1 || requestURI.indexOf("module.xml")!=-1 || requestURI.indexOf(".jsp")!=-1) {
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
		}else {
			String forceHttps = System.getenv("metamorphosis.forceHttps");
			if("true".equals(forceHttps)){
				String header = httpServletRequest.getHeader("X-Forwarded-Proto");
				if(header!=null && header.indexOf("https")!=0) {
					httpServletResponse.sendRedirect("https://" + request.getServerName() + requestURI);
					return;
			    }
			}
			chain.doFilter(request,response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
    
	@Override
	public void destroy() {

	}

}