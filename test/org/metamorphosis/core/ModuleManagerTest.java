package org.metamorphosis.core;

import static org.junit.Assert.*;
import org.junit.Test;
import org.metamorphosis.core.util.TestModuleParser;

import static org.mockito.Mockito.*;


import java.io.File;

import javax.servlet.ServletContext;

public class ModuleManagerTest {

	@Test
	public void createParser() {
		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getInitParameter("metamorphosis.module_parser"))
		.thenReturn("org.metamorphosis.core.util.TestModuleParser");
		ModuleManager moduleManager = new ModuleManager(servletContext);
		assertEquals(TestModuleParser.class,moduleManager.getParser().getClass());
	}
	
	
	@Test
    public void loadModule() throws Exception {
		ServletContext servletContext = mock(ServletContext.class);
		ModuleManager moduleManager = new ModuleManager(servletContext);
		assertEquals(moduleManager,moduleManager.getParser());
		Module module = moduleManager.loadModule(new File("test/resources/modules/module1"));
		assertEquals("module1", module.getId());
		assertEquals("module1", module.getName());
		assertEquals("module1", module.getUrl());
		assertEquals("back-end", module.getType());
		assertEquals("/modules/module1/index.jsp",module.getIndex());
		assertEquals("module1.groovy",module.getScript());
		assertEquals(true,module.isMain());
		assertEquals(true,module.isVisible());
		assertEquals(true,module.isCached());
		assertEquals("Mamadou Lamine Ba", module.getAuthor());
		assertEquals("lmamdou@s2m.com", module.getAuthorEmail());
		assertEquals("description of module1", module.getDescription());
		assertEquals("04/06/2019", module.getCreationDate());
		assertEquals("©2019", module.getCopyright());
		assertEquals("GNU/GPL", module.getLicense());
		assertEquals("1.0", module.getVersion());
    }
	
	@Test
    public void loadModules() {
		ServletContext servletContext = mock(ServletContext.class);
		ModuleManager moduleManager = new ModuleManager(servletContext);
		File folder = new File("test/resources/modules");
		moduleManager.loadModules(folder);
		assertEquals(1, moduleManager.getModules().size());
    }
	
}
