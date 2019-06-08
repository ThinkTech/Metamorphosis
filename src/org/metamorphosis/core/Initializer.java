package org.metamorphosis.core;

import java.io.File;
import java.lang.annotation.Annotation;
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
    
    public void register(File script) throws Exception {
    	register(loadScript(script));
    }
    
    public void register(Object object) throws Exception {
        Annotation[] annotations = object.getClass().getAnnotations();
		for(Annotation annotation : annotations) {
		   if(annotation instanceof WebServlet) addServlet(context, (WebServlet) annotation, object);
		   if(annotation instanceof WebFilter) addFilter(context, (WebFilter) annotation, object);
		}
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