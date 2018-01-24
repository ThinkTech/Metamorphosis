package org.metamorphosis.core;

import java.util.Map;
import javax.servlet.ServletContext;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.factory.DefaultActionFactory;

public class ActionFactory extends DefaultActionFactory {

	@Override
	public Object buildAction(String url, String namespace, ActionConfig config,Map<String, Object> extraContext) throws Exception {
		String reload = System.getenv("metamorphosis.reload");
		Object object = "true".equals(reload) ? ModuleManager.getInstance().buildAction(url) : buildAction(url);
		return object!=null ? object : super.buildAction(url, namespace, config, extraContext);
	}
	
	private synchronized Object buildAction(String url) throws Exception {
		ModuleManager moduleManager = ModuleManager.getInstance();
		Module module = moduleManager.getCurrentModule();
		String key = url;
		if(module!=null){
			Action action = module.getAction(url);
			if(action != null && action.getScript() != null) {
				key = module.getUrl()+"/"+action.getScript();
			}else{
				key = module.getUrl()+"/"+module.getScript();
			}
		}
		ServletContext context = moduleManager.getServletContext();
		Object object = context.getAttribute(key);
		 if(object==null) {
          object = ModuleManager.getInstance().buildAction(url);
          if(object!=null) context.setAttribute(key,object);  
        }
        return object;
	}

}