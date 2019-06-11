package org.metamorphosis.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.lang.annotation.Annotation;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.junit.Test;
import org.metamorphosis.core.annotation.Controller;

public class InitializerTest {

	@Test
	public void register() throws Exception {
		ScriptManager scriptManager = new ScriptManager(mock(ServletContext.class));
		File folder = new File("test/resources/scripts");
		File[] files = folder.listFiles();
		if(files!=null) {
			for(File file : files) {
				Object object = scriptManager.loadScript(file);
				Annotation[] annotations = object.getClass().getAnnotations();
				for(Annotation annotation : annotations) {
				   if(annotation instanceof Controller) {
					   assertEquals("TestAction",object.getClass().getName());
					   assertEquals("org.metamorphosis.core.ActionSupport", object.getClass().getSuperclass().getName());
				   }
				   else if(annotation instanceof WebServlet) {
					   assertEquals("TestServlet",object.getClass().getName());
					   WebServlet webServlet = (WebServlet) annotation;
					   assertEquals("/test.html", webServlet.value()[0]);
					   assertEquals("org.metamorphosis.core.Servlet", object.getClass().getSuperclass().getName());
				   }
				   else if(annotation instanceof WebFilter) {
					   assertEquals("TestFilter",object.getClass().getName());
					   WebFilter webFilter = (WebFilter) annotation;
					   assertEquals("/*", webFilter.value()[0]);
					   assertEquals("org.metamorphosis.core.Filter", object.getClass().getSuperclass().getName());
				   }
				   else if(annotation instanceof WebListener) {
					   if(object instanceof ServletRequestListener) {
					     assertEquals("TestRequestListener",object.getClass().getName());
					     assertEquals(object.getClass().getSuperclass(),ServletRequestListener.class);
					   }
					   else if(object instanceof HttpSessionListener) {
						 assertEquals("TestSessionListener",object.getClass().getName());
						 assertEquals(object.getClass().getSuperclass(),HttpSessionListener.class);    
					   }
					   else if(object instanceof ServletContextAttributeListener) {
						 assertEquals("TestContextAttributeListener",object.getClass().getName());
						 assertEquals(object.getClass().getSuperclass(),ServletContextAttributeListener.class);    
					   }
					   else if(object instanceof ServletRequestAttributeListener) {
						 assertEquals("TestRequestAttributeListener",object.getClass().getName());
						 assertEquals(object.getClass().getSuperclass(),ServletRequestAttributeListener.class);
					   }
					   else if(object instanceof HttpSessionAttributeListener) {
						 assertEquals("TestSessionAttributeListener",object.getClass().getName());
						 assertEquals(object.getClass().getSuperclass(),HttpSessionAttributeListener.class);
					   }
				   }
				}
		   }
		}
	}
}