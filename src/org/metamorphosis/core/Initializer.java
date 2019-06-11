package org.metamorphosis.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;

public class Initializer {
	
	protected File folder;
	protected final ServletContext context;
	protected final Map<String,DynamicInvocationHandler> handlers;
	
	public Initializer(ServletContext context) {
		this.context = context;
		this.handlers = new HashMap<String,DynamicInvocationHandler>();
		new ScriptManager(context);
		context.setAttribute("path",context.getContextPath()+"/");
	}
	
	public Initializer(ServletContext context,File folder) {
		this(context);
		this.folder = folder;
	}
	
    public void init() {
    	if(folder!=null && folder.exists()) {
    		try {
				File[] files = folder.listFiles();
				if(files!=null) for(File file : files) register(file);
				monitor(folder);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    public Object register(File script) throws Exception {
    	Object object = ScriptManager.getInstance().loadScript(script);
    	register(object);
    	return object;
    }
    
    public void register(Object object) throws Exception {
        Annotation[] annotations = object.getClass().getAnnotations();
		for(Annotation annotation : annotations) {
		   if(annotation instanceof WebServlet) addServlet(context, (WebServlet) annotation, object);
		   if(annotation instanceof WebFilter)  addFilter(context, (WebFilter) annotation, object);
		   if(annotation instanceof WebListener)  addListener(context, (WebListener) annotation, object);
		}
    }
    
	protected void addServlet(ServletContext context,WebServlet webServlet,Object object) {
		String name = webServlet.name().trim().equals("")?object.getClass().getName():webServlet.name();
		ServletRegistration registration = context.getServletRegistration(name);
		if(registration==null) {
			DynamicInvocationHandler handler = new DynamicInvocationHandler(object);
			Servlet servlet = (Servlet) Proxy.newProxyInstance(Servlet.class.getClassLoader(),new Class[] {Servlet.class},handler);
			handlers.put(name, handler);
			registration = context.addServlet(name,servlet);
			if(webServlet.value().length>0)registration.addMapping(webServlet.value());
			if(webServlet.urlPatterns().length>0)registration.addMapping(webServlet.urlPatterns());
		}else {
			String message = "The servlet with the name " + name+" has already been registered. Please use a different name or package";
			throw new RuntimeException(message);
		}
	}
	
	protected void addFilter(ServletContext context,WebFilter webFilter,Object object) {
		String name = object.getClass().getName();
		FilterRegistration registration = context.getFilterRegistration(name);
		if(registration==null) {
			DynamicInvocationHandler handler = new DynamicInvocationHandler(object);
			Filter filter = (Filter) Proxy.newProxyInstance(Filter.class.getClassLoader(),new Class[] {Filter.class},handler);
			handlers.put(name, handler);
			registration = context.addFilter(name,filter);
			if(webFilter.value().length>0) registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.value());
			if(webFilter.urlPatterns().length>0)registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.urlPatterns());
		}else {
			String message = "The filter with the name " + name+" has already been registered. Please use a different name or package";
			throw new RuntimeException(message);
		}
	}
	
	protected void addListener(ServletContext context,WebListener webListener,Object object) {
		DynamicInvocationHandler handler = new DynamicInvocationHandler(object);
		handlers.put(object.getClass().getName(), handler);
		EventListener listener=null;
		if(object instanceof ServletContextAttributeListener) {
			listener = (EventListener) Proxy.newProxyInstance(ServletContextAttributeListener.class.getClassLoader(),new Class[] {ServletContextAttributeListener.class},handler);
		}
		else if(object instanceof ServletRequestListener) {
			listener = (EventListener) Proxy.newProxyInstance(ServletRequestListener.class.getClassLoader(),new Class[] {ServletRequestListener.class},handler);
		}
		else if(object instanceof ServletRequestAttributeListener) {
			listener = (EventListener) Proxy.newProxyInstance(ServletRequestAttributeListener.class.getClassLoader(),new Class[] {ServletRequestAttributeListener.class},handler);
		}
		else if(object instanceof HttpSessionListener) {
			listener = (EventListener) Proxy.newProxyInstance(HttpSessionListener.class.getClassLoader(),new Class[] {HttpSessionListener.class},handler);
		}
		else if(object instanceof HttpSessionAttributeListener) {
			listener = (EventListener) Proxy.newProxyInstance(HttpSessionAttributeListener.class.getClassLoader(),new Class[] {HttpSessionAttributeListener.class},handler);
		}
		if(listener!=null)context.addListener(listener);
	}
	
	protected void monitor(final File folder) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)) {
			new FileMonitor(folder).addListener(new FileAdapter() {
				public void onCreate(String fileName) {
				  File script = new File(folder+"/"+fileName);
				   try {
					   Object object = ScriptManager.getInstance().loadScript(script);
					   Annotation[] annotations = object.getClass().getAnnotations();
						for(Annotation annotation : annotations) {
						   if(annotation instanceof WebServlet) {
							   WebServlet webServlet = (WebServlet) annotation;
							   String name = webServlet.name().trim().equals("")?object.getClass().getName():webServlet.name();
							   DynamicInvocationHandler handler = handlers.get(name);
							   if(handler!=null) handler.setTarget(object);
								
						   }
						   else if(annotation instanceof WebFilter || annotation instanceof WebListener) {
							   String name = object.getClass().getName();
							   DynamicInvocationHandler handler = handlers.get(name);
							   if(handler!=null) handler.setTarget(object);
						   }
						}
				   } catch (Exception e) {
					e.printStackTrace();
				   }
			    	
				}
				
			}).monitor();
		}
	}
	
	public File getFolder() {
		return folder;
	}
	
}