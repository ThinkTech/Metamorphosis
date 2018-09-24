package org.metamorphosis.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
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
import groovy.util.GroovyScriptEngine;

public class ModuleManager implements DispatcherListener, ModuleParser {

	private Map<String,Module> modules = new HashMap<String,Module>();
	private Logger logger = Logger.getLogger(ModuleManager.class.getName());
	private Configuration configuration;
	private ServletContext servletContext;
	private static ModuleManager instance;
	private static final String MODULE_METADATA = "module.xml";
	
	public ModuleManager(ServletContext servletContext) {
		instance = this;
		this.servletContext = servletContext;
	}

	public void loadModules(File folder) {
		File[] files = folder.listFiles();
		if(files != null) {
		  for(File file : files) {
			 if(file.isDirectory()) {
				 try{
					 loadModule(file);
				 }catch (Exception e) {
					e.printStackTrace();
				 }
			 }
		  }
		  monitorFolder(folder);
		}
	}

	public Module loadModule(File folder) throws Exception {
		File metadata = new File(folder+"/"+MODULE_METADATA);
		Module module = metadata.exists() ? createParser().parse(metadata) : new Module();
		module.setFolder(folder);
		addModule(module);
		return module;
	}
	
	private ModuleParser createParser() {
		ModuleParser parser = this;
		return parser;
	}
	
	public Module parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("module", Module.class);
		digester.addBeanPropertySetter("module/id");
		digester.addBeanPropertySetter("module/name");
		digester.addBeanPropertySetter("module/type");
		digester.addBeanPropertySetter("module/url");
		digester.addBeanPropertySetter("module/title");
		digester.addBeanPropertySetter("module/icon");
		digester.addBeanPropertySetter("module/index");
		digester.addBeanPropertySetter("module/script");
		digester.addBeanPropertySetter("module/main");
		digester.addBeanPropertySetter("module/visible");
		digester.addBeanPropertySetter("module/roles");
		digester.addBeanPropertySetter("module/cached");
		digester.addBeanPropertySetter("module/author");
		digester.addBeanPropertySetter("module/authorEmail");
		digester.addBeanPropertySetter("module/authorUrl");
		digester.addBeanPropertySetter("module/description");
		digester.addBeanPropertySetter("module/details");
		digester.addBeanPropertySetter("module/creationDate");
		digester.addBeanPropertySetter("module/copyright");
		digester.addBeanPropertySetter("module/license");
		digester.addBeanPropertySetter("module/version");
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
		digester.addSetNext("module/actions/action/result", "addResult");
		digester.addCallMethod("module/actions/action/result", "setValue", 0);
		digester.addSetNext("module/actions/action", "addAction");
		return (Module) digester.parse(metadata);
	}
	
	private void monitorFolder(final File folder) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			new FileMonitor(folder).addListener(new FileListener() {
				public void onFileCreated(String name) {
					File file = new File(folder+"/"+name);
					if(file.isDirectory()) {
						logger.log(Level.INFO, "adding module from folder  : " + name);
						addModule(new Module(file));
					}
				}
				public void onFileDeleted(String name) {
					Collection<Module> modules = getModules(); 
					for(Module module : modules) {
						if(module.getFolder().getName().equals(name)) {
							logger.log(Level.INFO, "removing module from folder : " + name);
							removeModule(module);
							break;
						}
					}
				}
				
			}).monitor();
		}
	}
	
	public void addModule(Module module) {
		initModule(module);
		monitorModule(module);
		modules.put(module.getId(),module);
	}
	
	private void initModule(Module module) {
		if(module.getUrl() == null) module.setUrl(module.getFolder().getName());
		for(Action action : module.getActions()) {
			if(action.getPage()==null) action.setPage(action.getUrl());
		}
		for(Menu menu : module.getMenus()) {
			for(MenuItem item : menu.getMenuItems()) {
				String url = item.getUrl() != null ? module.getUrl() + "/" + item.getUrl() : module.getUrl();
				item.setUrl(url);
			}
		}
	}
	
	private void monitorModule(final Module module) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			new FileMonitor(module.getFolder()).addListener(new FileListener() {
		    	public void onFileCreated(String name) {
		    		if(name.equals(MODULE_METADATA)) updateModule(module);		
				}
				public void onFileDeleted(String name) {
				}
			}).monitor();
		}
	}
	
	public void removeModule(Module module) {
		modules.remove(module.getId());
		configuration.removePackageConfig(module.getId());
		configuration.rebuildRuntimeConfiguration();
	}
	
	public void updateModule(Module module) {
		try {
			logger.log(Level.INFO, "updating module from folder : " + module.getFolder().getName());
			String id = module.getId();
			File folder = module.getFolder();
			module = parse(new File(folder+"/"+MODULE_METADATA));
			module.setFolder(folder);
			initModule(module);
			registerPages(module);
			rebuildRuntimeConfiguration(id,module);
			modules.remove(id);
			modules.put(module.getId(),module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void registerPages(Module module) throws Exception {
		CachingTilesContainer container = (CachingTilesContainer) TilesAccess.getContainer(servletContext);
		TemplateManager templateManager = TemplateManager.getInstance();
		Template template = module.isBackend() ? templateManager.getBackendTemplate(null) : templateManager.getFrontendTemplate(null);
		Definition definition = createDefinition(module.getUrl(),module.getType(),template.getIndex());
		definition.putAttribute("content", new Attribute(module.getIndex()));
		container.register(definition);
		for(File file : module.getFolder().listFiles()) {
			String name = file.getName();
			if(file.isFile() && name.endsWith(".jsp")) {
				String prefix = name.endsWith(".jsp") ? name.substring(0, name.length() - 4) : name.substring(0, name.length() - 5);
				definition = createDefinition(module.getUrl() + "/" + prefix,module.getUrl(),template.getIndex());
				definition.putAttribute("content", new Attribute(module.getPath(name)));
				container.register(definition);
			}
		}
	}
	
	private Definition createDefinition(String name,String parent,String template) {
		Definition definition = new Definition();
		definition.setName(name);
		definition.setExtends(parent);
		definition.setTemplate(template);
		definition.setPreparer("org.metamorphosis.core.PagePreparer");
		return definition;
	}
	
	private void rebuildRuntimeConfiguration(String id,Module module) {
		configuration.removePackageConfig(id);
		PackageConfig.Builder packageBuilder = new PackageConfig.Builder(module.getId());
		packageBuilder.namespace("/"+module.getUrl());
		packageBuilder.addParent(configuration.getPackageConfig("root"));
		ActionConfig.Builder actionBuilder;
		for(Menu menu : module.getMenus()) {
			for(MenuItem item : menu.getMenuItems()) {
				if(!item.getUrl().equals(module.getUrl())){
					String url = item.getUrl().substring(module.getUrl().length()+1);
					actionBuilder = new ActionConfig.Builder(url,url,null);
					actionBuilder.addResultConfig(createResultBuilder(new Result("success","tiles",item.getUrl())).build());
					actionBuilder.addResultConfig(createResultBuilder(new Result("error","redirect","/")).build());
					packageBuilder.addActionConfig(url,actionBuilder.build());
				}
			}
		}
		actionBuilder = new ActionConfig.Builder("index","index","");
	    actionBuilder.addResultConfig(createResultBuilder(new Result("success","tiles",module.getUrl()+"/index")).build());
		actionBuilder.addResultConfig(createResultBuilder(new Result("error","redirect","/")).build());
		packageBuilder.addActionConfig("index",actionBuilder.build());
		for(Action action : module.getActions()) {
			actionBuilder = new ActionConfig.Builder(action.getUrl(),action.getUrl(),action.getClassName());
			actionBuilder.methodName(action.getMethod());
			for(Result result : action.getResults()) {
				if(!result.getValue().equals("") && !result.getValue().startsWith("/")) {
					result.setValue(module.getUrl()+"/"+result.getValue());
				}
				actionBuilder.addResultConfig(createResultBuilder(result).build());
				actionBuilder.addResultConfig(createResultBuilder(new Result("error","redirect","/")).build());
			}
			packageBuilder.addActionConfig(action.getUrl(),actionBuilder.build());
		}
		configuration.addPackageConfig(module.getId(),packageBuilder.build());
		configuration.rebuildRuntimeConfiguration();
	}
	
	private ResultConfig.Builder createResultBuilder(Result result){
		ResultConfig.Builder builder = null;
		String type = result.getType();
		if(type.equals("tiles")) {
			builder = new ResultConfig.Builder(result.getName(),"org.apache.struts2.views.tiles.TilesResult");
		} else if(type.equals("redirect")) {
			builder = new ResultConfig.Builder(result.getName(),"org.apache.struts2.dispatcher.ServletRedirectResult");
		} else if(type.equals("redirectAction")) {
			builder = new ResultConfig.Builder(result.getName(),"org.apache.struts2.dispatcher.ServletActionRedirectResult");
		} else if(type.equals("dispatcher")) {
			builder = new ResultConfig.Builder(result.getName(),"org.apache.struts2.dispatcher.ServletDispatcherResult");
		}
		return builder.addParam("location",result.getValue());
	}
	
	public Object buildAction(Module module,String url) throws Exception {
		if(module != null) {
			Action action = module.getAction(url);
			File file = action!= null && action.getScript()!= null ? new File(module.getFolder()+"/scripts/"+action.getScript())
			 : 	new File(module.getFolder()+"/scripts/"+module.getScript());
			return loadScript(file);
		}
		return null;
	}
	
	private Object loadScript(File script) throws Exception {
		return script.exists() ? getScriptEngine(script.getParentFile()).loadScriptByName(script.getName()).newInstance() : new ActionSupport();
	}
	
	public synchronized Object buildAndCacheAction(Module module,String url) throws Exception {
		String key = url;
		if(module!=null){
			Action action = module.getAction(url);
			key = action != null && action.getScript() != null ? module.getUrl()+"/"+action.getScript() : module.getUrl()+"/"+module.getScript();
		}
		Object object = servletContext.getAttribute(key);
		if(object==null) {
		  object = ModuleManager.getInstance().buildAction(module,url);
		  if(object!=null) servletContext.setAttribute(key,object);  
        }
        return object;
	}
	
	private GroovyScriptEngine getScriptEngine(File folder) throws MalformedURLException {
		URL[] urls = {folder.toURI().toURL(), new File(servletContext.getRealPath("/")+"/scripts").toURI().toURL()};
		GroovyScriptEngine engine = new GroovyScriptEngine(urls);
		CompilerConfiguration configuration = new CompilerConfiguration();
		configuration.addCompilationCustomizers(createCompilationCustomizer());
		engine.setConfig(configuration);
		return engine;
	}
	
	private ImportCustomizer createCompilationCustomizer() {
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addImports("java.text.SimpleDateFormat");
		importCustomizer.addStarImports("org.metamorphosis.core","groovy.json");
		String imports = servletContext.getInitParameter("groovy.imports");
		if(imports!=null && imports.indexOf(",")!=-1){
			StringTokenizer st = new StringTokenizer(imports,",");
			while(st.hasMoreTokens()) importCustomizer.addImports(st.nextToken());
		}else if(imports!=null){
			importCustomizer.addImports(imports);
		}
		String starImports = servletContext.getInitParameter("groovy.starImports");
		if(starImports!=null && starImports.indexOf(",")!=-1){
			StringTokenizer st = new StringTokenizer(starImports,",");
			while(st.hasMoreTokens()) importCustomizer.addStarImports(st.nextToken());
			
		}else if(starImports!=null) {
			importCustomizer.addStarImports(starImports);
		}
		return importCustomizer;
	}
	
	public Module getCurrentModule() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String uri = request.getRequestURI();
		String url = uri.substring(request.getContextPath().length() + 1, uri.length());
		url = url.indexOf("/") != -1 ? url.substring(0, url.indexOf("/")) : url;
		Module module = getModuleByUrl(url);
		return module != null ? module : getModuleByUrl("/");
	}
	
	public Collection<Module> getModules() {
		return modules.values();
	}

	public Collection<Module> getVisibleModules(String type) {
		Collection<Module> visibles = new ArrayList<Module>();
		Collection<Module> modules = getModules(); 
		for(Module module : modules) if(module.isVisible() && module.getType().equals(type)) visibles.add(module);
		return visibles;
	}
	
	public Collection<Module> getFrontendModules() {
		List<Module> list = new ArrayList<Module>();
		Collection<Module> modules = getModules(); 
		for(Module module : modules) if(module.isFrontend()) list.add(module);
		Collections.sort(list);
		return list;
	}
	
	public Collection<Module> getBackendModules() {
		List<Module> list = new ArrayList<Module>();
		Collection<Module> modules = getModules();
		for(Module module : modules) if(module.isBackend()) list.add(module);
		Collections.sort(list);
		return list;
	}
	
	public Module getModuleByUrl(String url) {
		Collection<Module> modules = getModules(); 
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
		Collection<Module> modules = getModules(); 
		for(Module module : modules) if(module.getName().toLowerCase().equals(name.toLowerCase())) return module;
		return null;
	}
	
	public Module getModuleById(String id) {
		Collection<Module> modules = getModules(); 
		for(Module module : modules) if(module.getId().equals(id)) return module;
		return null;
	}
	
	public Module getMainModule(String type) {
		Collection<Module> modules = getModules();
		for(Module module : modules) if(module.isMain() && module.getType().equals(type)) return module;
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