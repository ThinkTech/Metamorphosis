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
    
    private void register(File script) throws Exception {
    	Object object = loadScript(script);
		Annotation[] annotations = object.getClass().getAnnotations();
		for(Annotation annotation : annotations) {
		   if(annotation instanceof WebServlet) addServlet(context, (WebServlet) annotation, object);
		   if(annotation instanceof WebFilter) addFilter(context, (WebFilter) annotation, object);
		}
    }
    
	private void addServlet(ServletContext context,WebServlet webServlet,Object object) {
		ServletRegistration registration = context.addServlet(object.getClass().getName(), (Servlet) object);
		if(webServlet.value().length>0)registration.addMapping(webServlet.value());
		if(webServlet.urlPatterns().length>0)registration.addMapping(webServlet.urlPatterns());
	}
	
	private void addFilter(ServletContext context,WebFilter webFilter,Object object) {
		FilterRegistration registration = context.addFilter(object.getClass().getName(), (Filter) object);
		if(webFilter.value().length>0)registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.value());
		if(webFilter.urlPatterns().length>0)registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,webFilter.urlPatterns());
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
		importCustomizer.addStarImports("org.metamorphosis.core","groovy.json","javax.servlet","javax.servlet.annotation","javax.servlet.http");
		return importCustomizer;
	}
	
}
