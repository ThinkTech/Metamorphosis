package org.metamorphosis.core;

import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.preparer.ViewPreparer;
import java.io.IOException;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;

public class PagePreparer implements ViewPreparer {

	@Override
	public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) throws PreparerException {
		
		try {
			ModuleManager moduleManager = ModuleManager.getInstance();
			TemplateManager templateManager = TemplateManager.getInstance();
			String id = (String) tilesContext.getSessionScope().get("template");
			Template template = templateManager.getTemplate(id);
			Module module = moduleManager.getCurrentModule();
			if(module!=null && module.isBackend()) {
				template = template!=null && template.isBackend() ? template : templateManager.getBackendTemplate(null);
				tilesContext.dispatch(template.getIndexPage());
			}else {
				template = templateManager.getFrontendTemplate(null);
				tilesContext.dispatch(template.getIndexPage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
}