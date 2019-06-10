package org.metamorphosis.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.lang.annotation.Annotation;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSessionListener;

import org.junit.Test;

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
				   if(annotation instanceof WebServlet) {
					   assertEquals("TestServlet",object.getClass().getName());
					   WebServlet webServlet = (WebServlet) annotation;
					   assertEquals("/test.html", webServlet.value()[0]);
				   }
				   if(annotation instanceof WebFilter) {
					   assertEquals("TestFilter",object.getClass().getName());
					   WebFilter webFilter = (WebFilter) annotation;
					   assertEquals("/*", webFilter.value()[0]);
				   }
				   if(annotation instanceof WebListener) {
					   if(object instanceof ServletRequestListener) {
					     assertEquals("TestRequestListener",object.getClass().getName());
					   }
					   else if(object instanceof HttpSessionListener) {
						     assertEquals("TestSessionListener",object.getClass().getName());
					    }
				   }
				}
		   }
		}
	}
}