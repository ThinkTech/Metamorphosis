package org.metamorphosis.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.metamorphosis.core.util.TestTemplateParser;

public class TemplateManagerTest {

	@Test
	public void createParser() {
		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getInitParameter("metamorphosis.template_parser"))
		.thenReturn("org.metamorphosis.core.util.TestTemplateParser");
		TemplateManager templateManager = new TemplateManager(servletContext);
		assertEquals(TestTemplateParser.class,templateManager.getParser().getClass());
	}
	
	
	@Test
    public void loadTemplate() throws Exception {
		ServletContext servletContext = mock(ServletContext.class);
		TemplateManager templateManager = new TemplateManager(servletContext);
		File folder = new File("test/resources/templates/template1");
		Template template = templateManager.loadTemplate(folder);
		testTemplate(template);
    }
	
	@Test
    public void loadTemplates() {
		ServletContext servletContext = mock(ServletContext.class);
		TemplateManager templateManager = new TemplateManager(servletContext);
		File folder = new File("test/resources/templates");
		templateManager.loadTemplates(folder);
		assertEquals(1, templateManager.getTemplates().size());
		assertEquals(0, templateManager.getFrontendTemplates().size());
		assertEquals(1, templateManager.getBackendTemplates().size());
		Template template = templateManager.getBackend();
		testTemplate(template);
    }
	
	private void testTemplate(Template template) {
		assertEquals("template1", template.getId());
		assertEquals("template1", template.getName());
		assertEquals("back-end", template.getType());
		assertEquals("Mamadou Lamine Ba", template.getAuthor());
		assertEquals("lmamdou@s2m.com", template.getAuthorEmail());
		assertEquals("description of template1", template.getDescription());
		assertEquals("04/06/2019", template.getCreationDate());
		assertEquals("©2019", template.getCopyright());
		assertEquals("GNU/GPL", template.getLicense());
		assertEquals("1.0", template.getVersion());
		assertEquals("this is the details of the template",template.getDetails());
	}
	
}