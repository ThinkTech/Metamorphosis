package org.metamorphosis.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.lang.annotation.Annotation;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import org.junit.Test;
import org.metamorphosis.core.annotation.ContextAttributeListener;
import org.metamorphosis.core.annotation.Controller;
import org.metamorphosis.core.annotation.RequestAttributeListener;
import org.metamorphosis.core.annotation.RequestListener;
import org.metamorphosis.core.annotation.SessionAttributeListener;
import org.metamorphosis.core.annotation.SessionListener;

public class ScriptManagerTest {

	@Test
	public void loadScripts() throws Exception {
		ScriptManager scriptManager = new ScriptManager(mock(ServletContext.class));
		File folder = new File("test/resources/scripts");
		File[] files = folder.listFiles();
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
				   assertEquals("org.metamorphosis.core.HttpServlet", object.getClass().getSuperclass().getName());
			   }
			   else if(annotation instanceof WebFilter) {
				   assertEquals("TestFilter",object.getClass().getName());
				   WebFilter webFilter = (WebFilter) annotation;
				   assertEquals("/*", webFilter.value()[0]);
				   assertEquals("org.metamorphosis.core.Filter", object.getClass().getSuperclass().getName());
			   }
			   else if(annotation instanceof RequestListener) {
				   assertEquals("TestRequestListener",object.getClass().getName());
				   assertEquals(object.getClass().getSuperclass(),ServletRequestListener.class);
			   }
			   else if(annotation instanceof SessionListener) {
				   assertEquals("TestSessionListener",object.getClass().getName());
				   assertEquals(object.getClass().getSuperclass(),HttpSessionListener.class);    
			   }
			   else if(annotation instanceof ContextAttributeListener) {
				   assertEquals("TestContextAttributeListener",object.getClass().getName());
				   assertEquals(object.getClass().getSuperclass(),ServletContextAttributeListener.class);
			   }
			   else if(annotation instanceof RequestAttributeListener) {
				   assertEquals("TestRequestAttributeListener",object.getClass().getName());
				   assertEquals(object.getClass().getSuperclass(),ServletRequestAttributeListener.class);
			   }
			   else if(annotation instanceof SessionAttributeListener) {
				   assertEquals("TestSessionAttributeListener",object.getClass().getName());
				   assertEquals(object.getClass().getSuperclass(),HttpSessionAttributeListener.class);
			   }
			  
			}
	   }
	}
}