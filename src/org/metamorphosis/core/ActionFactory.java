package org.metamorphosis.core;

import java.util.Map;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.DefaultActionFactory;

public class ActionFactory extends DefaultActionFactory {

	@Override
	public Object buildAction(String url, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
		return ModuleManager.getInstance().getAction(url);
	}

}