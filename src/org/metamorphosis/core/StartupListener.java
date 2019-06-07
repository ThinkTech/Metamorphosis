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
		String root = new File(context.getRealPath("/")).getAbsolutePath();
		FilterRegistration struts2 = context.addFilter("struts2",org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.class);
		struts2.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST,DispatcherType.FORWARD),true,"/*");
		StringBuffer buffer = new StringBuffer(loadTemplates(context, root));
		struts2.setInitParameter("config",loadModules(context, root, buffer));
		context.setInitParameter("org.apache.tiles.factory.TilesContainerFactory","org.metamorphosis.core.TilesContainerFactory");
		context.setInitParameter("org.apache.tiles.impl.BasicTilesContainer.DEFINITIONS_CONFIG",buffer.toString());
		new TilesListener().contextInitialized(event);
		new Initializer(context,new File(root+"/scripts")).init();
		copyFiles(root);
	}
	
	private String loadTemplates(ServletContext context,String root) {
		String folder = context.getInitParameter("metamorphosis.templates_folder");
		folder = folder!=null ? folder : "templates";
		TemplateManager templateManager = new TemplateManager(context,new File(root+"/"+folder));
		Template template = templateManager.getFrontend();
		String tilesDefinitions = template!=null ? createTiles(template) : "" ;
		template = templateManager.getBackend();
		tilesDefinitions += template!=null ? ","+ createTiles(template) : tilesDefinitions;
		context.setAttribute("templateManager",templateManager);
		return tilesDefinitions;
	}
	
	private String loadModules(ServletContext context,String root,StringBuffer buffer) {
		String config = "struts-custom.xml,struts-plugin.xml,struts.xml";
		String folder = context.getInitParameter("metamorphosis.modules_folder");
		folder = folder!=null ? folder : "modules";
		ModuleManager moduleManager = new ModuleManager(context,new File(root+"/"+folder));
		for(Module module : moduleManager.getModules()) {
			Initializer initializer = new Initializer(context,module.getScriptFolder());
			initializer.init(module);
			buffer.append(","+createTiles(module));
			config +=","+createConfig(module);
		}
		context.setAttribute("moduleManager",moduleManager);
		Dispatcher.addDispatcherListener(moduleManager);
		return config;
	}

	private String createTiles(Template template) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE tiles-definitions PUBLIC '-//Apache Software Foundation//DTD Tiles Configuration 2.0//EN' "+
				"'http://tiles.apache.org/dtds/tiles-config_2_0.dtd'>"+
				"<tiles-definitions><definition name='"+template.getType()+"' template='"+template.getIndex()+"' preparer='org.metamorphosis.core.PagePreparer'/>";
		if(template.isFrontend()) {
			content += "<definition name='index' extends='"+template.getType()+"'>";
			content+="<put-attribute name='content' value='/index.jsp'/>";
			content+="</definition>";
		}
		content +="</tiles-definitions>";
		File file = new File(template.getFolder()+"/tiles.xml");
		writeFile(file,content);
		return template.getPath(file.getName());
	}

	private String createTiles(Module module) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
				"<!DOCTYPE tiles-definitions PUBLIC '-//Apache Software Foundation//DTD Tiles Configuration 2.0//EN' "+
				"'http://tiles.apache.org/dtds/tiles-config_2_0.dtd'>"+
				"<tiles-definitions><definition name='"+module.getUrl()+"' extends='"+module.getType()+"'>"+
				"<put-attribute name='content' value='"+module.getIndex()+"'/>"+
				"</definition>";
		for(File file : module.getFolder().listFiles()) {
			String name = file.getName();
			if(file.isFile() && (name.endsWith(".jsp") || name.endsWith(".html"))) {
				String prefix = name.endsWith(".jsp") ? name.substring(0, name.length() - 4) : name.substring(0, name.length() - 5);
				content+="<definition name='"+module.getUrl()+"/"+prefix+"' extends='"+module.getUrl()+"'>";
				content+="<put-attribute name='content' value='"+module.getPath(name)+"'/>";
				content+="</definition>";
			}
		}
		content +="</tiles-definitions>";
		File file = new File(module.getFolder()+"/tiles.xml");
		writeFile(file,content);
		return module.getPath(file.getName());
	}

	private String createConfig(Module module) {
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
			content+="<action name='"+action.getUrl()+"' method='"+action.getMethod()+"'>";
			for(Result result : action.getResults()) {
				if(!result.getValue().equals("") && !result.getValue().startsWith("/")) {
					result.setValue(module.getUrl()+"/"+result.getValue());
				}
				content+="<result name='"+result.getName()+"' type='"+result.getType()+"'>"+result.getValue();
				content+="</result>";
			}
			content+="<result name='error' type='redirect'>/</result>";
			content+="</action>";
		}
		content +="</package></struts>";
		File file = new File(module.getFolder()+"/struts.xml");
		writeFile(file,content);
		return file.getAbsolutePath();
	}
	
	private void writeFile(File file,String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void copyFiles(String root) {
		copyFile(root,"css","metamorphosis.min.css");
		copyFile(root,"js","metamorphosis.min.js");
		copyFile(root,"js","jquery-3.1.1.min.js");
		copyFile(root,"js","dust-full.min.js");
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
		BufferedInputStream in = new BufferedInputStream(source);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination));
		byte[] buffer = new byte[1024];
	    int length;
	    while((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
		in.close();
		out.close();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

}