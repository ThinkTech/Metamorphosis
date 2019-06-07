package org.metamorphosis.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.File;
import javax.servlet.ServletContext;
import org.junit.Test;

public class TemplateManagerTest {

	@Test
	public void createParser() {
		ServletContext servletContext = mock(ServletContext.class);
		TemplateManager templateManager = new TemplateManager(servletContext);
		assertEquals(templateManager,templateManager.getParser());
	}
	
	
	@Test
    public void loadTemplate() throws Exception {
		ServletContext servletContext = mock(ServletContext.class);
		TemplateManager templateManager = new TemplateManager(servletContext);
		File folder = new File("test/resources/templates/template1");
		Template template = templateManager.loadTemplate(folder);
		assertEquals("template1", template.getName());
		assertEquals("back-end", template.getType());
		assertEquals("Mamadou Lamine Ba", template.getAuthor());
		assertEquals("lmamdou@s2m.com", template.getAuthorEmail());
		assertEquals("description of template1", template.getDescription());
		assertEquals("04/06/2019", template.getCreationDate());
		assertEquals("©2019", template.getCopyright());
		assertEquals("GNU/GPL", template.getLicense());
		assertEquals("1.0", template.getVersion());
    }
	
	@Test
    public void loadTemplates() {
		ServletContext servletContext = mock(ServletContext.class);
		TemplateManager templateManager = new TemplateManager(servletContext);
		File folder = new File("test/resources/templates");
		templateManager.loadTemplates(folder);
		assertEquals(1, templateManager.getTemplates().size());
    }
	
}