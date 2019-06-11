package org.metamorphosis.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import org.codehaus.groovy.control.BytecodeProcessor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import groovy.util.GroovyScriptEngine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

public class ScriptManager {

	protected static ScriptManager instance;
	protected final ServletContext context;
	public static final String SCRIPTS_FOLDER = "scripts";
	
	public ScriptManager(ServletContext context) {
		instance = this;
		this.context = context;
	}

	public Object loadScript(File script) throws Exception {
		return createScriptEngine(script.getParentFile()).loadScriptByName(script.getName()).newInstance();
	}
	
	public GroovyScriptEngine createScriptEngine(File folder) throws Exception {
		URL[] urls = {folder.toURI().toURL(), new File(context.getRealPath("/")+"/"+SCRIPTS_FOLDER).toURI().toURL()};
		GroovyScriptEngine engine = new GroovyScriptEngine(urls);
		CompilerConfiguration configuration = new CompilerConfiguration();
		configuration.addCompilationCustomizers(createCompilationCustomizer());
		final ClassPool classPool = ClassPool.getDefault();
		classPool.insertClassPath(new LoaderClassPath(engine.getParentClassLoader()));
		configuration.setBytecodePostprocessor(new BytecodeProcessor() {
			public byte[] processBytecode(String name, byte[] original) {
				ByteArrayInputStream stream = new ByteArrayInputStream(original);
				try {
					 CtClass clazz = classPool.makeClass(stream);
					 clazz.detach();
					 Object[] annotations = clazz.getAnnotations();
					 for(Object annotation : annotations) {
						 String value = annotation.toString();
						 if(value.indexOf("WebServlet")!=-1) {
							 clazz.setSuperclass(classPool.get("org.metamorphosis.core.Servlet"));		
							 return clazz.toBytecode();
						 }
						 else if(value.indexOf("WebFilter")!=-1) {
							 clazz.setSuperclass(classPool.get("org.metamorphosis.core.Filter"));
							 return clazz.toBytecode();
						 }
					 }
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return original;
			}
		});
		engine.setConfig(configuration);
		return engine;
	}
	
	protected ImportCustomizer createCompilationCustomizer() {
		ImportCustomizer importCustomizer = new ImportCustomizer();
		Package[] packages = Package.getPackages();
		for(Package p : packages) {
	        if(p.getName().startsWith("app")) {
	            importCustomizer.addStarImports(p.getName());
	        }
	    }
		String imports = context.getInitParameter("groovy.imports");
		if(imports!=null && imports.indexOf(",")!=-1){
			StringTokenizer st = new StringTokenizer(imports,",");
			while(st.hasMoreTokens()) importCustomizer.addImports(st.nextToken());
		}else if(imports!=null){
			importCustomizer.addImports(imports);
		}
		String starImports = context.getInitParameter("groovy.starImports");
		if(starImports!=null && starImports.indexOf(",")!=-1){
			StringTokenizer st = new StringTokenizer(starImports,",");
			while(st.hasMoreTokens()) importCustomizer.addStarImports(st.nextToken());
		}else if(starImports!=null) {
			importCustomizer.addStarImports(starImports);
		}
		importCustomizer.addImports("java.text.SimpleDateFormat");
		importCustomizer.addStarImports("org.metamorphosis.core","javax.servlet","javax.servlet.annotation","javax.servlet.http","org.metamorphosis.core.annotation","groovy.json");
		return importCustomizer;
	}
	
	public static ScriptManager getInstance() {
		return instance;
	}
	
}