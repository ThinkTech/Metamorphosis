package org.metamorphosis.core;

import java.util.Map;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.DefaultActionFactory;

public class ActionFactory extends DefaultActionFactory {

	@Override
	public Object buildAction(String url, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
		ModuleManager moduleManager = ModuleManager.getInstance();
		Module module = moduleManager.getCurrentModule();
		String reload = System.getenv("metamorphosis.reload");
		Object object = "true".equals(reload) ? moduleManager.buildAction(module,url) : moduleManager.buildAndCacheAction(module,url);
		return object!=null ? object : super.buildAction(url, namespace, config, extraContext);
	}

}