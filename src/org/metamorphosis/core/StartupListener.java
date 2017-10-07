package org.metamorphosis.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.tiles.web.startup.TilesListener;

@WebListener
public class StartupListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		context.setAttribute("path",context.getContextPath()+"/");
		context.setAttribute("app_name",context.getInitParameter("app_name"));
		context.setAttribute("app_title",context.getInitParameter("app_title"));
		context.setAttribute("app_description",context.getInitParameter("app_description"));
		String root = new File(context.getRealPath("/")).getAbsolutePath();
		FilterRegistration struts2 = context.addFilter("struts2", org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.class);
		struts2.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true, "/*");
		StringBuffer buffer = new StringBuffer(loadTemplates(context, root));
		struts2.setInitParameter("config",loadModules(context, root, buffer));
		context.setInitParameter("org.apache.tiles.factory.TilesContainerFactory","org.metamorphosis.core.TilesContainerFactory");
		context.setInitParameter("org.apache.tiles.impl.BasicTilesContainer.DEFINITIONS_CONFIG",buffer.toString());
		new TilesListener().contextInitialized(event);
		copyFiles(root);
		
	}
	
	private String loadTemplates(ServletContext context,String root) {
		TemplateManager templateManager = new TemplateManager();
		context.setAttribute("templateManager",templateManager);
		templateManager.loadTemplates(new File(root+"/templates"));
		String tilesDefinitions="";
		Template template = templateManager.getBackendTemplate(null);
		if(template!=null)tilesDefinitions = createTemplateTiles(root,template);
		template = templateManager.getFrontendTemplate(null);
		if(template!=null) tilesDefinitions += ","+ createTemplateTiles(root,template);
		return tilesDefinitions;
	}
	
	private String loadModules(ServletContext context,String root,StringBuffer buffer) {
		String config = "struts-custom.xml,struts-plugin.xml,struts.xml";
		ModuleManager moduleManager = new ModuleManager(context);
		moduleManager.loadModules(new File(root+"/modules"));
		Dispatcher.addDispatcherListener(moduleManager);
		for(Module module : moduleManager.getModules()) {
			buffer.append(","+createModuleTiles(module));
			config +=","+createModuleConfig(module);
			if(module.getId().equals("users")) context.setAttribute("security",true);
		}
		context.setAttribute("moduleManager",moduleManager);
		return config;
	}

	private String createTemplateTiles(String root,Template template) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE tiles-definitions PUBLIC '-//Apache Software Foundation//DTD Tiles Configuration 2.0//EN' "+
				"'http://tiles.apache.org/dtds/tiles-config_2_0.dtd'>"+
				"<tiles-definitions><definition name='"+template.getType()+"' template='"+template.getIndexPage()+"' preparer='org.metamorphosis.core.PagePreparer'/>";
		if(template.isFrontend()) {
			content += "<definition name='index' extends='"+template.getType()+"'>";
			content+="<put-attribute name='content' value='/index.jsp'/>";
			content+="</definition>";
			content += "<definition name='login' extends='"+template.getType()+"'>";
			content+="<put-attribute name='content' value='/templates/"+template.getId()+"/login.jsp'/>";
			content+="</definition>";
			content += "<definition name='password/reset' extends='"+template.getType()+"'>";
			content+="<put-attribute name='content' value='/templates/"+template.getId()+"/reset.jsp'/>";
			content+="</definition>";
			content += "<definition name='password/change' extends='"+template.getType()+"'>";
			content+="<put-attribute name='content' value='/templates/"+template.getId()+"/change.jsp'/>";
			content+="</definition>";
		}
		content +="</tiles-definitions>";
		File temp=null;
		try {
			temp = new File(root+"/templates"+"/tiles-"+template.getType()+".xml");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/templates/"+temp.getName();
	}

	private String createModuleTiles(Module module) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE tiles-definitions PUBLIC '-//Apache Software Foundation//DTD Tiles Configuration 2.0//EN' "+
				"'http://tiles.apache.org/dtds/tiles-config_2_0.dtd'>"+
				"<tiles-definitions><definition name='"+module.getUrl()+"' extends='"+module.getType()+"'>"+
				"<put-attribute name='content' value='/modules/"+module.getId()+"/"+module.getIndexPage()+"'/>"+
				"</definition>";
		for(File file : module.getFolder().listFiles()) {
			String name = file.getName();
			if(file.isFile() && (name.endsWith(".jsp") || name.endsWith(".html"))) {
				String prefix = name.endsWith(".jsp") ? name.substring(0, name.length() - 4) : name.substring(0, name.length() - 5);
				content+="<definition name='"+module.getUrl()+"/"+prefix+"' extends='"+module.getUrl()+"'>";
				content+="<put-attribute name='content' value='/modules/"+module.getId()+"/"+name+"'/>";
				content+="</definition>";
			}
		}
		content +="</tiles-definitions>";
		File temp=null;
		try {
			temp = new File(module.getFolder()+"/tiles-generated.xml");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/modules/"+module.getFolder().getName()+"/"+temp.getName();
	}

	
	private String createModuleConfig(Module module) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE struts PUBLIC '-//Apache Software Foundation//DTD Struts Configuration 2.0//EN' "+
				"'http://struts.apache.org/dtds/struts-2.0.dtd'>"+
				"<struts><package name='"+module.getId()+"' namespace='/"+module.getUrl()+"' extends='root'>";
		content+="<action name='index'>";
		content+="<result name='success' type='tiles'>"+module.getUrl()+"</result>";
		content+="<result name='error' type='redirect'>/</result>";
		content+="</action>";
		for(Menu menu : module.getMenus()) {
			for(MenuItem item : menu.getMenuItems()) {
				if(!item.getUrl().equals(module.getUrl())) {
					String url = item.getUrl().substring(module.getUrl().length()+1);
					content+="<action name='"+url+"'>";
					content+="<result name='success' type='tiles'>"+item.getUrl()+"</result>";
					content+="<result name='error' type='redirect'>/</result>";
					content+="</action>";
				}
			}
		}
		for(Action action : module.getActions()) {
			content+="<action name='"+action.getUrl()+"' class='"+action.getClassName()+"' method='"+action.getMethod()+"'>";
			for(Result result : action.getResults()) {
				if(!result.getValue().equals("") && !result.getValue().startsWith("/")) {
					result.setValue(module.getUrl()+"/"+result.getValue());
				}
				content+="<result name='"+result.getName()+"' type='"+result.getType()+"'>"+result.getValue();
				content+="</result>";
				content+="<result name='error' type='redirect'>/</result>";
			}
			content+="</action>";
		}
		content +="</package></struts>";
		File temp=null;
		try {
			temp = new File(module.getFolder()+"/struts-generated.xml");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp.getAbsolutePath();
	}
	
	private void copyFiles(String root) {
		if(!new File(root+"/css/metamorphosis.css").exists()) copyFile(root,"css","metamorphosis.css");
		if(!new File(root+"/js/metamorphosis.js").exists()) copyFile(root,"js","metamorphosis.js");
		copyFile(root,"js","jquery-3.1.1.min.js");
		copyFile(root,"js","handlebars-v4.0.5.js");
		copyFile(root,"js","dust-full.min.js");
		copyFile(root,"js","pdfmake.min.js");
		copyFile(root,"js","vfs_fonts.js");
		if(!new File(root+"/404.jsp").exists()) copyFile(root,"","404.jsp");
	}
	
	
	private void copyFile(String root,String directory,String file)	{
		InputStream source = this.getClass().getClassLoader().getResourceAsStream("META-INF/"+file);
		if(source!=null) {
			try {
			 File folder = new File(root+"/"+directory);
			 folder.mkdirs();
			 File destination = new File(folder+"/"+file);
			 destination.getParentFile().mkdirs();
			 copyFile(source,destination);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private void copyFile(InputStream source,File destination) throws Exception {
		BufferedInputStream br = new BufferedInputStream(source);
		BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(destination));
		byte[] buffer = new byte[1024];
	    int length;
	    while ((length = br.read(buffer)) > 0) {
	      bw.write(buffer, 0, length);
	    }
		br.close();
		bw.close();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
         FileMonitor.monitor = false;
	}

}