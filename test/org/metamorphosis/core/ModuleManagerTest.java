package org.metamorphosis.core;

import static org.junit.Assert.*;
import org.junit.Test;
import org.metamorphosis.core.util.TestModuleParser;

import static org.mockito.Mockito.*;


import java.io.File;
import java.util.List;

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
		testModule(module);
    }
	
	@Test
    public void loadModules() {
		ServletContext servletContext = mock(ServletContext.class);
		ModuleManager moduleManager = new ModuleManager(servletContext);
		File folder = new File("test/resources/modules");
		moduleManager.loadModules(folder);
		assertEquals(1, moduleManager.getModules().size());
		Module module = moduleManager.getMainModule("back-end");
		testModule(module);
    }
	
	private void testModule(Module module) {
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
		assertEquals("this is the details of the module",module.getDetails());
		assertEquals(2,module.getActions().size());
		assertEquals("action1",module.getAction("action1").getUrl());
		assertEquals("method1",module.getAction("action1").getMethod());
		assertEquals("action1.groovy",module.getAction("action1").getScript());
		assertEquals("my Action1",module.getAction("action1").getTitle());
		assertEquals("page1", module.getAction("action1").getPage());
		assertEquals(1, module.getAction("action1").getResults().size());
		Result result =  module.getAction("action1").getResults().get(0);
		assertEquals("success", result.getName());
		assertEquals("tiles", result.getType());
		assertEquals(2, module.getMenus().size());
		List<Menu> menus = module.getMenus("left");
		assertEquals(1, menus.size());
		Menu menu = module.getMenu("left");
		assertEquals(true, menu.isVisible());
		assertEquals(2, menu.getMenuItems().size());
		MenuItem menuItem = menu.getMenuItems().get(1);
		assertEquals("item4",menuItem.getLabel());
		assertEquals("module1/url4",menuItem.getUrl());
		assertEquals("item 4",menuItem.getTitle());
	}
	
}
