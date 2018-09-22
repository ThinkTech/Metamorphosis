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
			TemplateManager templateManager = TemplateManager.getInstance();
			ModuleManager moduleManager = ModuleManager.getInstance();
			Module module = moduleManager.getCurrentModule();
			if(module!=null && module.isBackend()) {
				Template template = templateManager.getBackendTemplate(null);
				tilesContext.dispatch(template.getIndexPage());
			}else {
				Template template = templateManager.getFrontendTemplate(null);
				tilesContext.dispatch(template.getIndexPage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
}