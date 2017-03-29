package org.metamorphosis.core;

import java.util.Map;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.DefaultActionFactory;

public class ActionFactory extends DefaultActionFactory {

	@Override
	public Object buildAction(String url, String namespace, ActionConfig config,Map<String, Object> extraContext) throws Exception {
		Object object = ModuleManager.getInstance().buildAction(url);
		return object!=null ? object : super.buildAction(url, namespace, config, extraContext);
	}

}