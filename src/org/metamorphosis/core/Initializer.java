package org.metamorphosis.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.metamorphosis.core.annotation.Controller;
import org.metamorphosis.core.annotation.Get;
import org.metamorphosis.core.annotation.Post;
import org.metamorphosis.core.annotation.Put;

import groovy.util.GroovyScriptEngine;

public class Initializer {
	
	private final File folder;
	private final ServletContext context;
	
	public Initializer(ServletContext context,File folder) {
		this.context = context;
		this.folder = folder;
	}
	
    public void init() {
    	if(folder.exists()) {
    		try {
				File[] files = folder.listFiles();
				if(files!=null) for(File file : files) register(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void init(Module module) {
    	if(folder.exists()) {
    		try {
				File[] files = folder.listFiles();
				if(files!=null) {
					for(File file : files) {
					  Object object = register(file);
					  Annotation[] annotations = object.getClass().getAnnotations();
					  for(Annotation annotation : annotations) {
					   if(annotation instanceof Controller) addController(module, file, (Controller) annotation, object);
					  }
				   }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    private Object register(File script) throws Exception {
    	Object object = loadScript(script);
		Annotation[] annotations = object.getClass().getAnnotations();
		for(Annotation annotation : annotations) {
		   if(annotation instanceof WebServlet) addServlet(context, (WebServlet) annotation, object);
		   if(annotation instanceof WebFilter) addFilter(context, (WebFilter) annotation, object);
		}
		return object;
    }
    
	private void addServlet(ServletContext context,WebServlet webServlet,Object object) {
		String name = object.getClass().getName();
		ServletRegistration registration = context.getServletRegistration(name);
		if(registration==null) {
			registration = context.addServlet(name, (Servlet) object);
			if(webServlet.value().length>0)registration.addMapping(webServlet.value());
			if(webServlet.urlPatterns().length>0)registration.addMapping(webServlet.urlPatterns());
		}else {
			String message = "The servlet with the name " + name+" has already been registered. Please use a different name or package";
			throw new RuntimeException(message);
		}
	}
	
	private void addFilter(ServletContext context,WebFilter webFilter,Object object) {
		String name = object.getClass().getName();
		FilterRegistration registration = context.getFilterRegistration(name);
		if(registration==null) {
			registration = context.addFilter(name, (Filter) object);
			if(webFilter.value().length>0) registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.value());
			if(webFilter.urlPatterns().length>0)registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.urlPatterns());
		}else {
			String message = "The filter with the name " + name+" has already been registered. Please use a different name or package";
			throw new RuntimeException(message);
		}
	}
	
	private void addController(Module module,File script,Controller controller,Object object) {
		String url = controller.value();
		if(!url.trim().equals("")) module.setUrl(url);
		Method[] methods = object.getClass().getDeclaredMethods();
		for(Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations) {
				if(annotation instanceof Get) {
					Get get = (Get) annotation;
					Action action = new Action();
					url = get.value().trim().equals("") ? get.url() : get.value();
					if(url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName()+" of the class "+object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("GET");
					if(!get.page().trim().equals(""))action.setPage(get.page());
					module.addAction(action);
				} else if(annotation instanceof Post) {
					Post post = (Post) annotation;
					Action action = new Action();
					url = post.value().trim().equals("") ? post.url() : post.value();
					if(url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName()+" of the class "+object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("POST");
					if(!post.page().trim().equals(""))action.setPage(post.page());
					module.addAction(action);
				} else if(annotation instanceof Put) {
					Put put = (Put) annotation;
					Action action = new Action();
					url = put.value().trim().equals("") ? put.url() : put.value();
					if(url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName()+" of the class "+object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("PUT");
					if(!put.page().trim().equals(""))action.setPage(put.page());
					module.addAction(action);
				}
				
			}
		}
	}
	
	public File getFolder() {
		return folder;
	}
	
	private Object loadScript(File script) throws Exception {
		return createScriptEngine(script.getParentFile()).loadScriptByName(script.getName()).newInstance();
	}
	
	private GroovyScriptEngine createScriptEngine(File folder) throws Exception {
		URL[] urls = {folder.toURI().toURL()};
		GroovyScriptEngine engine = new GroovyScriptEngine(urls);
		engine.setConfig(new CompilerConfiguration().addCompilationCustomizers(createCompilationCustomizer()));
		return engine;
	}
	
	private ImportCustomizer createCompilationCustomizer() {
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addStarImports("org.metamorphosis.core","org.metamorphosis.core.annotation","groovy.json","javax.servlet","javax.servlet.annotation","javax.servlet.http");
		return importCustomizer;
	}
	
}