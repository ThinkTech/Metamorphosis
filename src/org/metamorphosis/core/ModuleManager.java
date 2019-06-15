package org.metamorphosis.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.metamorphosis.core.annotation.Controller;
import org.metamorphosis.core.annotation.DELETE;
import org.metamorphosis.core.annotation.GET;
import org.metamorphosis.core.annotation.POST;
import org.metamorphosis.core.annotation.PUT;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

public class ModuleManager implements DispatcherListener, ModuleParser {

	protected final Map<String, Module> modules = new HashMap<String, Module>();
	protected final Logger logger = Logger.getLogger(this.getClass().getName());
	protected Configuration configuration;
	protected final ServletContext servletContext;
	protected static ModuleManager instance;
	protected ModuleParser parser;
	protected static final String MODULE_METADATA = "module.xml";

	public ModuleManager(ServletContext servletContext) {
		instance = this;
		this.servletContext = servletContext;
		try {
			parser = createParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ModuleManager(ServletContext servletContext, File folder) {
		this(servletContext);
		loadModules(folder);
	}

	public void loadModules(File folder) {
		File[] files = folder.listFiles();
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					try {
						loadModule(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			monitor(folder);
		}
	}

	public Module loadModule(File folder) throws Exception {
		File metadata = new File(folder + "/" + MODULE_METADATA);
		Module module = metadata.exists() ? parser.parse(metadata) : new Module();
		module.setFolder(folder);
		addModule(module);
		return module;
	}

	protected ModuleParser createParser() throws Exception {
		String parserClass = servletContext.getInitParameter("metamorphosis.module_parser");
		return parserClass != null ? (ModuleParser) Class.forName(parserClass).newInstance() : this;
	}

	@Override
	public Module parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("module", Module.class);
		digester.addBeanPropertySetter("module/id");
		digester.addBeanPropertySetter("module/name");
		digester.addBeanPropertySetter("module/type");
		digester.addBeanPropertySetter("module/url");
		digester.addBeanPropertySetter("module/title");
		digester.addBeanPropertySetter("module/index");
		digester.addBeanPropertySetter("module/script");
		digester.addBeanPropertySetter("module/main");
		digester.addBeanPropertySetter("module/visible");
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
		digester.addObjectCreate("module/actions/action/result", Result.class);
		digester.addSetProperties("module/actions/action/result");
		digester.addSetNext("module/actions/action/result", "addResult");
		digester.addCallMethod("module/actions/action/result", "setValue", 0);
		digester.addSetNext("module/actions/action", "addAction");
		return (Module) digester.parse(metadata);
	}

	protected void monitor(final File folder) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)) {
			new FileMonitor(folder).addListener(new FileListener() {
				public void onCreate(String fileName) {
					File file = new File(folder + "/" + fileName);
					if (file.isDirectory()) {
						logger.log(Level.INFO, "adding module from folder  : " + fileName);
						addModule(new Module(file));
					}
				}

				public void onDelete(String fileName) {
					for (Module module : getModules()) {
						if (module.getFolder().getName().equals(fileName)) {
							logger.log(Level.INFO, "removing module from folder : " + fileName);
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
		modules.put(module.getId(), module);
	}

	protected void initModule(Module module) {
		try {
			File[] files = module.getScriptFolder().listFiles();
			if(files != null) {
				for(File file : files) {
					Object object = ScriptManager.getInstance().loadScript(file);
					Annotation[] annotations = object.getClass().getAnnotations();
					for(Annotation annotation : annotations) {
						if(annotation instanceof Controller) {
							addController(module, file, (Controller) annotation, object);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(module.getUrl() == null) {
			module.setUrl(module.getFolder().getName());
		}
		for(Action action : module.getActions()) {
			if (action.getPage() == null) {
				action.setPage(action.getUrl());
			}
		}
		for(Menu menu : module.getMenus()) {
			for (MenuItem item : menu.getMenuItems()) {
				String url = item.getUrl() != null ? module.getUrl() + "/" + item.getUrl() : module.getUrl();
				item.setUrl(url);
			}
		}
	}

	protected void addController(Module module, File script, Controller controller, Object object) {
		String url = !controller.url().trim().equals("") ? controller.url() : controller.value();
		if(!url.trim().equals(""))
			module.setUrl(url);
		Method[] methods = object.getClass().getDeclaredMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof GET) {
					GET get = (GET) annotation;
					Action action = new Action();
					url = get.value().trim().equals("") ? get.url() : get.value();
					if (url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName() + " of the class "
								+ object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("GET");
					if (!get.page().trim().equals(""))
						action.setPage(get.page());
					module.addAction(action);
				} else if (annotation instanceof POST) {
					POST post = (POST) annotation;
					Action action = new Action();
					url = post.value().trim().equals("") ? post.url() : post.value();
					if (url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName() + " of the class "
								+ object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("POST");
					if (!post.page().trim().equals(""))
						action.setPage(post.page());
					module.addAction(action);
				} else if (annotation instanceof PUT) {
					PUT put = (PUT) annotation;
					Action action = new Action();
					url = put.value().trim().equals("") ? put.url() : put.value();
					if (url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName() + " of the class "
								+ object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("PUT");
					if (!put.page().trim().equals(""))
						action.setPage(put.page());
					module.addAction(action);
				} else if (annotation instanceof DELETE) {
					DELETE delete = (DELETE) annotation;
					Action action = new Action();
					url = delete.value().trim().equals("") ? delete.url() : delete.value();
					if (url.trim().equals("")) {
						String message = "You must define the url for the method " + method.getName() + " of the class "
								+ object.getClass().getName();
						throw new RuntimeException(message);
					}
					action.setUrl(url);
					action.setMethod(method.getName());
					action.setScript(script.getName());
					action.setHttpMethod("PUT");
					if (!delete.page().trim().equals(""))
						action.setPage(delete.page());
					module.addAction(action);
				}

			}
		}
	}

	protected void monitorModule(final Module module) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)) {
			new FileMonitor(module.getFolder()).addListener(new FileAdapter() {
				public void onCreate(String name) {
					if (name.equals(MODULE_METADATA))
						updateModule(module);
				}
			}).monitor();

			new FileMonitor(module.getScriptFolder()).addListener(new FileAdapter() {
				public void onCreate(String name) {
					updateModule(module);
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
			module = parse(new File(folder + "/" + MODULE_METADATA));
			module.setFolder(folder);
			initModule(module);
			registerPages(module);
			rebuildRuntimeConfiguration(id, module);
			modules.remove(id);
			modules.put(module.getId(), module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void registerPages(Module module) throws Exception {
		CachingTilesContainer container = (CachingTilesContainer) TilesAccess.getContainer(servletContext);
		TemplateManager templateManager = TemplateManager.getInstance();
		Template template = module.isBackend() ? templateManager.getBackend() : templateManager.getFrontend();
		Definition definition = createDefinition(module.getUrl(), module.getType(), template.getIndex());
		definition.putAttribute("content", new Attribute(module.getIndex()));
		container.register(definition);
		for (File file : module.getFolder().listFiles()) {
			String name = file.getName();
			if (file.isFile() && name.endsWith(".jsp")) {
				String prefix = name.endsWith(".jsp") ? name.substring(0, name.length() - 4)
						: name.substring(0, name.length() - 5);
				definition = createDefinition(module.getUrl() + "/" + prefix, module.getUrl(), template.getIndex());
				definition.putAttribute("content", new Attribute(module.getPath(name)));
				container.register(definition);
			}
		}
	}

	protected Definition createDefinition(String name, String parent, String template) {
		Definition definition = new Definition();
		definition.setName(name);
		definition.setExtends(parent);
		definition.setTemplate(template);
		definition.setPreparer("org.metamorphosis.core.PagePreparer");
		return definition;
	}

	protected void rebuildRuntimeConfiguration(String id, Module module) {
		configuration.removePackageConfig(id);
		PackageConfig.Builder packageBuilder = new PackageConfig.Builder(module.getId());
		packageBuilder.namespace("/" + module.getUrl());
		packageBuilder.addParent(configuration.getPackageConfig("root"));
		ActionConfig.Builder actionBuilder;
		for(Menu menu : module.getMenus()) {
			for(MenuItem item : menu.getMenuItems()) {
				if(!item.getUrl().equals(module.getUrl())) {
					String url = item.getUrl().substring(module.getUrl().length() + 1);
					actionBuilder = new ActionConfig.Builder(url, url, null);
					actionBuilder.addResultConfig(
							createResultBuilder(new Result("success", "tiles", item.getUrl())).build());
					actionBuilder.addResultConfig(createResultBuilder(new Result("error", "redirect", "/")).build());
					packageBuilder.addActionConfig(url, actionBuilder.build());
				}
			}
		}
		actionBuilder = new ActionConfig.Builder("index", "index", "");
		actionBuilder.addResultConfig(
				createResultBuilder(new Result("success", "tiles", module.getUrl() + "/index")).build());
		actionBuilder.addResultConfig(createResultBuilder(new Result("error", "redirect", "/")).build());
		actionBuilder.addResultConfig(createResultBuilder(new Result("500", "dispatcher", "/500.jsp")).build());
		packageBuilder.addActionConfig("index", actionBuilder.build());
		for(Action action : module.getActions()) {
			actionBuilder = new ActionConfig.Builder(action.getUrl(), action.getUrl(), "");
			actionBuilder.methodName(action.getMethod());
			for(Result result : action.getResults()) {
				if(!result.getValue().equals("") && !result.getValue().startsWith("/")) {
					result.setValue(module.getUrl() + "/" + result.getValue());
				}
				actionBuilder.addResultConfig(createResultBuilder(result).build());
				actionBuilder.addResultConfig(createResultBuilder(new Result("error", "redirect", "/")).build());
				actionBuilder.addResultConfig(createResultBuilder(new Result("500", "dispatcher", "/500.jsp")).build());
			}
			packageBuilder.addActionConfig(action.getUrl(), actionBuilder.build());
		}
		configuration.addPackageConfig(module.getId(), packageBuilder.build());
		configuration.rebuildRuntimeConfiguration();
	}

	protected ResultConfig.Builder createResultBuilder(Result result) {
		ResultConfig.Builder builder = null;
		String type = result.getType();
		if (type.equals("tiles")) {
			builder = new ResultConfig.Builder(result.getName(), "org.apache.struts2.views.tiles.TilesResult");
		} else if (type.equals("redirect")) {
			builder = new ResultConfig.Builder(result.getName(), "org.apache.struts2.dispatcher.ServletRedirectResult");
		} else if (type.equals("dispatcher")) {
			builder = new ResultConfig.Builder(result.getName(),"org.apache.struts2.dispatcher.ServletDispatcherResult");
		}
		return builder.addParam("location", result.getValue());
	}

	public Object buildAction(Module module, String url) throws Exception {
		if(module != null) {
			Action action = module.getAction(url);
			String scripts_folder = ScriptManager.SCRIPTS_FOLDER;
			File file = action != null && action.getScript() != null
					? new File(module.getFolder() + "/" + scripts_folder + "/" + action.getScript())
					: new File(module.getFolder() + "/" + scripts_folder + "/" + module.getScript());
			if (file.exists()) {
				return ScriptManager.getInstance().loadScript(file);
			}
		}
		return new ActionSupport();
	}

	public synchronized Object buildAndCacheAction(Module module, String url) throws Exception {
		String key = url;
		if(module != null) {
			Action action = module.getAction(url);
			key = action != null && action.getScript() != null ? module.getUrl() + "/" + action.getScript()
					: module.getUrl() + "/" + module.getScript();
		}
		Object object = servletContext.getAttribute(key);
		if(object == null) {
			object = buildAction(module, url);
			servletContext.setAttribute(key, object);
		}
		return object;
	}

	public Object getAction(Module module, String url) throws Exception {
		String reload = System.getenv("metamorphosis.reload");
		return "true".equals(reload) ? buildAction(module, url) : buildAndCacheAction(module, url);
	}

	public Object getAction(String url) throws Exception {
		return getAction(getCurrentModule(), url);
	}

	public Module getCurrentModule() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String url = request.getRequestURI().substring(request.getContextPath().length() + 1);
		url = url.indexOf("/") != -1 ? url.substring(0, url.indexOf("/")) : url;
		return getModuleByUrl(url);
	}

	public Collection<Module> getModules() {
		return modules.values();
	}

	public Collection<Module> getVisibleModules(String type) {
		Collection<Module> modules = new ArrayList<Module>();
		for(Module module : getModules()) {
			if(module.isVisible() && module.getType().equals(type)) {
				modules.add(module);
			}
		}
		return modules;
	}

	public Collection<Module> getFrontendModules() {
		List<Module> modules = new ArrayList<Module>();
		for(Module module : getModules()) {
			if(module.isFrontend()) {
				modules.add(module);
			}
		}
		Collections.sort(modules);
		return modules;
	}

	public Collection<Module> getBackendModules() {
		List<Module> modules = new ArrayList<Module>();
		for(Module module : getModules()) {
			if(module.isBackend()) {
				modules.add(module);
			}
		}
		Collections.sort(modules);
		return modules;
	}

	public Module getModuleByUrl(String url) {
		for(Module module : getModules()) {
			if(module.getUrl().equals(url)) {
				return module;
			}
		}
		return null;
	}

	public Module getModuleByName(String name) {
		for(Module module : getModules())
			if(module.getName().toLowerCase().equals(name.toLowerCase())) {
				return module;
			}
		return null;
	}

	public Module getModuleById(String id) {
		for(Module module : getModules()) {
			if(module.getId().equals(id)) {
				return module;
			}
		}
		return null;
	}

	public Module getMainModule(String type) {
		for(Module module : getModules()) {
			if(module.isMain() && module.getType().equals(type)) {
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

	public ModuleParser getParser() {
		return parser;
	}

	public void setParser(ModuleParser parser) {
		this.parser = parser;
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