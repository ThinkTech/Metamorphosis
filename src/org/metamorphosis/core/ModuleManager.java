package org.metamorphosis.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.digester.Digester;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherListener;
import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.access.TilesAccess;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

public class ModuleManager implements DispatcherListener {

	private List<Module> modules = new ArrayList<Module>();
	private Logger logger = Logger.getLogger(ModuleManager.class.getName());
	private Configuration configuration;
	private ServletContext servletContext;
	private static ModuleManager instance;
	private static final String MODULE_METADATA = "module.xml";
	
	public ModuleManager() {
		instance = this;
	}

	public ModuleManager(ServletContext servletContext) {
		instance = this;
		this.servletContext = servletContext;
	}

	public void loadModules(final File root) {
		File[] files = root.listFiles();
		if(files != null) {
		  for(File folder : files) {
			if(folder.isDirectory()) loadModule(folder);
		  }
		  monitorRoot(root);
		}
	}

	public Module loadModule(File folder) {
		File metadata = new File(folder + "/"+MODULE_METADATA);
		if(metadata.exists()) {
			try {
				Module module = parse(metadata);
				module.setFolder(folder);
				initModule(module);
				addModule(module);
				monitorModule(module);
				return module;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Module module = new Module();
			module.setName(folder.getName());
			module.setType("front-end");
			module.setFolder(folder);
			initModule(module);
			addModule(module);
			monitorModule(module);
			return module;
		}
		return null;
	}
	
	private Module parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("module", Module.class);
		digester.addBeanPropertySetter("module/id");
		digester.addBeanPropertySetter("module/name");
		digester.addBeanPropertySetter("module/type");
		digester.addBeanPropertySetter("module/url");
		digester.addBeanPropertySetter("module/title");
		digester.addBeanPropertySetter("module/icon");
		digester.addBeanPropertySetter("module/index","indexPage");
		digester.addBeanPropertySetter("module/script");
		digester.addBeanPropertySetter("module/template");
		digester.addBeanPropertySetter("module/main");
		digester.addBeanPropertySetter("module/visible");
		digester.addBeanPropertySetter("module/administrable");
		digester.addBeanPropertySetter("module/roles");
		digester.addBeanPropertySetter("module/cached");
		digester.addBeanPropertySetter("module/mandatory");
		digester.addBeanPropertySetter("module/author");
		digester.addBeanPropertySetter("module/authorEmail");
		digester.addBeanPropertySetter("module/authorUrl");
		digester.addBeanPropertySetter("module/description");
		digester.addBeanPropertySetter("module/details");
		digester.addBeanPropertySetter("module/creationDate");
		digester.addBeanPropertySetter("module/copyright");
		digester.addBeanPropertySetter("module/license");
		digester.addBeanPropertySetter("module/version");
		digester.addBeanPropertySetter("module/price");
		digester.addObjectCreate("module/menus/menu", Menu.class);
		digester.addSetProperties("module/menus/menu");
		digester.addObjectCreate("module/menus/menu/menuItem", MenuItem.class);
		digester.addSetProperties("module/menus/menu/menuItem");
		digester.addSetNext("module/menus/menu/menuItem", "addMenuItem");
		digester.addSetNext("module/menus/menu", "addMenu");
		digester.addObjectCreate("module/actions/action", Action.class);
		digester.addSetProperties("module/actions/action");
		digester.addSetProperties("module/actions/action", "class", "className");
		digester.addObjectCreate("module/actions/action/result", Result.class);
		digester.addSetProperties("module/actions/action/result");
		digester.addObjectCreate("module/actions/action/result/param", Parameter.class);
		digester.addSetProperties("module/actions/action/result/param");
		digester.addSetNext("module/actions/action/result/param", "addParameter");
		digester.addCallMethod("module/actions/action/result/param", "setValue", 0);
		digester.addSetNext("module/actions/action/result", "addResult");
		digester.addCallMethod("module/actions/action/result", "setValue", 0);
		digester.addSetNext("module/actions/action", "addAction");
		return (Module) digester.parse(metadata);
	}
	
	private void initModule(Module module) {
		if(module.getUrl() == null) module.setUrl(module.getFolder().getName());
		for(Action action : module.getActions()) {
			if(action.getPage()==null) {
				action.setPage(action.getUrl());
			}
		}
		for(Menu menu : module.getMenus()) {
			for(MenuItem item : menu.getMenuItems()) {
				String url = item.getUrl() != null ? module.getUrl() + "/" + item.getUrl() : module.getUrl();
				item.setUrl(url);
			}
		}
	}
	
	private void monitorRoot(final File root) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			FileMonitor monitor = new FileMonitor(root);
			monitor.addListener(new FileListener() {
				
				@Override
				public void onCreated(String file) {
					File folder = new File(root+"/"+file);
					if(folder.isDirectory()) {
						logger.log(Level.INFO, "adding module  : " + folder.getName());
						final Module module = new Module();
						module.setFolder(folder);
						initModule(module);
						addModule(module);
						monitorModule(module);
					}
				}
				
				@Override
				public void onDeleted(String file) {
					Module module = getModuleById(file);
					if(module!=null) {
						logger.log(Level.INFO, "removing module  : " + module.getName());
						removeModule(module);
					}
				}
				
			});
			monitor.watch();
		}
	}

	private void monitorModule(final Module module) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
		    FileMonitor monitor = new FileMonitor(module.getFolder());
		    monitor.addListener(new FileListener() {
		    	
		    	@Override
				public void onCreated(String file) {
		    		if(file.equals(MODULE_METADATA)) {
						updateModule(module);
					}
				}
		    	
				@Override
				public void onDeleted(String file) {
					
				}
				
			});
		    monitor.watch();
		}
	}

	private void registerPages(Module module) throws Exception {
		CachingTilesContainer container = (CachingTilesContainer) TilesAccess.getContainer(servletContext);
		Template template = getCurrentTemplate(module);
		Definition definition = createDefinition(module.getUrl(),module.getType(),template.getIndexPage());
		definition.putAttribute("content", new Attribute("/modules/" + module.getId() + "/" + module.getIndexPage()));
		container.register(definition);
		for(File file : module.getFolder().listFiles()) {
			String name = file.getName();
			if(file.isFile() && name.endsWith(".jsp")) {
				String prefix = name.endsWith(".jsp") ? name.substring(0, name.length() - 4) : name.substring(0, name.length() - 5);
				definition = createDefinition(module.getUrl() + "/" + prefix,module.getUrl(),template.getIndexPage());
				definition.putAttribute("content", new Attribute("/modules/" + module.getId() + "/" + name));
				container.register(definition);
			}
		}
	}
	
	private Template getCurrentTemplate(Module module) {
		TemplateManager templateManager = TemplateManager.getInstance();
		return module.isBackend() ? templateManager.getBackendTemplate(null)
				: templateManager.getFrontendTemplate(null);
	}
	
	private Definition createDefinition(String name,String parent,String template) {
		Definition definition = new Definition();
		definition.setName(name);
		definition.setExtends(parent);
		definition.setTemplate(template);
		definition.setPreparer("org.metamorphosis.core.PagePreparer");
		return definition;
	}

	public Module getCurrentModule() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String uri = request.getRequestURI();
		String url = uri.substring(request.getContextPath().length() + 1, uri.length());
		url = url.indexOf("/") != -1 ? url.substring(0, url.indexOf("/")) : url;
		Module module = getModuleByUrl(url);
		return module != null ? module : getModuleByUrl("/");
	}

	public Module getModuleByUrl(String url) {
		for(Module module : modules) {
			if(url.equals("/") && module.isMain() && module.isFrontend()) {
				return module;
			}
			else if(module.getUrl().equals(url)) {
				return module;
			}
		}
		return null;
	}
	
	public Module getModuleByName(String name) {
		for(Module module : modules) {
			if(module.getName().toLowerCase().equals(name.toLowerCase())) return module;
		}
		return null;
	}

	public Object buildAction(String url) throws Exception {
		Module module = getCurrentModule();
		if(module != null) {
			Action action = module.getAction(url);
			if(action != null && action.getScript() != null) {
				File script = new File(module.getFolder() + "/scripts/" + action.getScript());
				if(script.exists()) {
					String name =  "scripts/" + script.getName();
				    GroovyScriptEngine engine = getScriptEngine(module.getFolder());
					return engine.run(name,new Binding());
				}
			}else{
				File script = new File(module.getFolder() + "/scripts/" + module.getScript());
				if(script.exists()) {
					String name = "scripts/" + script.getName();
					GroovyScriptEngine engine = getScriptEngine(module.getFolder());
					return engine.run(name,new Binding());
				}
			}
			return new ActionSupport();
		}
		return null;
	}
	
	private GroovyScriptEngine getScriptEngine(File folder) throws MalformedURLException {
		URL[] url = {folder.toURI().toURL()};
		GroovyScriptEngine engine = new GroovyScriptEngine(url);
		CompilerConfiguration configuration = new CompilerConfiguration();
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addImports("java.text.SimpleDateFormat");
		importCustomizer.addStarImports("org.metamorphosis.core");
		importCustomizer.addStarImports("groovy.json");
		configuration.addCompilationCustomizers(importCustomizer);
		engine.setConfig(configuration);
		return engine;
	}

	public Module getModuleById(String id) {
		for(Module module : modules) {
			if(module.getId().equals(id)) return module;
		}
		return null;
	}

	public void addModule(Module module) {
		module.setIndex(modules.size());
		modules.add(module);
	}
	
	public void removeModule(Module module) {
		modules.remove(module.getIndex());
		configuration.removePackageConfig(module.getId());
		configuration.rebuildRuntimeConfiguration();
	}
	
	public void updateModule(Module module) {
		try {
			logger.log(Level.INFO, "updating module  : " + module.getName());
			File folder = module.getFolder();
			int index = module.getIndex();
			String id = module.getId();
			module = parse(new File(folder + "/"+MODULE_METADATA));
			module.setFolder(folder);
			module.setIndex(index);
			initModule(module);
			modules.set(index, module);
			configuration.removePackageConfig(id);
			PackageConfig.Builder packageBuilder = new PackageConfig.Builder(module.getId());
			packageBuilder.namespace("/" + module.getUrl());
			packageBuilder.addParent(configuration.getPackageConfig("root"));
			for(Menu menu : module.getMenus()) {
				for(MenuItem item : menu.getMenuItems()) {
					if(!item.getUrl().equals(module.getUrl())) {
						String url = item.getUrl().substring(module.getUrl().length() + 1);
						ActionConfig.Builder actionBuilder = new ActionConfig.Builder(url, url, null);
						ResultConfig.Builder resultBuilder = new ResultConfig.Builder("success",
								"org.apache.struts2.views.tiles.TilesResult");
						resultBuilder.addParam("location", item.getUrl());
						actionBuilder.addResultConfig(resultBuilder.build());
						resultBuilder = new ResultConfig.Builder("error",
								"org.apache.struts2.dispatcher.ServletRedirectResult");
						resultBuilder.addParam("location", "/");
						actionBuilder.addResultConfig(resultBuilder.build());
						ActionConfig actionConfig = actionBuilder.build();
						packageBuilder.addActionConfig(url, actionConfig);
					}
				}
			}
			for(Action action : module.getActions()) {
				ActionConfig.Builder actionBuilder = new ActionConfig.Builder(action.getUrl(), action.getUrl(),
						action.getClassName());
				actionBuilder.methodName(action.getMethod());
				for(Result result : action.getResults()) {
					if(!result.getValue().equals("") && !result.getValue().startsWith("/")) {
						result.setValue(module.getUrl() + "/" + result.getValue());
					}
					ResultConfig.Builder resultBuilder = new ResultConfig.Builder("error",
							"org.apache.struts2.dispatcher.ServletRedirectResult");
					resultBuilder.addParam("location", "/");
					actionBuilder.addResultConfig(resultBuilder.build());
					resultBuilder = null;
					String type = result.getType();
					if(type.equals("tiles")) {
						resultBuilder = new ResultConfig.Builder(result.getName(),
								"org.apache.struts2.views.tiles.TilesResult");
					} else if(type.equals("redirect")) {
						resultBuilder = new ResultConfig.Builder(result.getName(),
								"org.apache.struts2.dispatcher.ServletRedirectResult");
					} else if(type.equals("redirectAction")) {
						resultBuilder = new ResultConfig.Builder(result.getName(),
								"org.apache.struts2.dispatcher.ServletActionRedirectResult");
					} else if(type.equals("dispatcher")) {
						resultBuilder = new ResultConfig.Builder(result.getName(),
								"org.apache.struts2.dispatcher.ServletDispatcherResult");
					}
					if(resultBuilder != null) {
						resultBuilder.addParam("location", result.getValue());
						for(Parameter parameter : result.getParameters()) {
							resultBuilder.addParam(parameter.getName(),parameter.getValue());
						}
						actionBuilder.addResultConfig(resultBuilder.build());
					}
				}
				ActionConfig actionConfig = actionBuilder.build();
				packageBuilder.addActionConfig(action.getUrl(), actionConfig);
			}
			PackageConfig packageConfig = packageBuilder.build();
			configuration.addPackageConfig(module.getId(), packageConfig);
			configuration.rebuildRuntimeConfiguration();
			registerPages(module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public List<Module> getVisibleModules(String type) {
		List<Module> visibles = new ArrayList<Module>();
		for(Module module : modules) {
			if(module.isVisible() && module.getType().equals(type)) {
				visibles.add(module);
			}
		}
		return visibles;
	}

	public List<Module> getAdminModules() {
		List<Module> modules = new ArrayList<Module>();
		for(Module module : this.modules) {
			if(module.isAdministrable()) modules.add(module);
		}
		return modules;
	}
	
	public List<Module> getFrontendModules() {
		List<Module> modules = new ArrayList<Module>();
		for(Module module : this.modules) {
			if(module.isFrontend()) modules.add(module);
		}
		Collections.sort(modules);
		return modules;
	}
	
	public List<Module> getBackendModules() {
		List<Module> modules = new ArrayList<Module>();
		for(Module module : this.modules) {
			if(module.isBackend()) modules.add(module);
		}
		Collections.sort(modules);
		return modules;
	}

	public Module getMain() {
		for(Module module : modules) {
			if(module.isMain() && module.isBackend()) {
				return module;
			}
		}
		return null;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@Override
	public void dispatcherInitialized(Dispatcher dispatcher) {
		configuration = dispatcher.getConfigurationManager().getConfiguration();
	}
	
	@Override
	public void dispatcherDestroyed(Dispatcher dispatcher) {
	}

	public static ModuleManager getInstance() {
		return instance;
	}
	
}