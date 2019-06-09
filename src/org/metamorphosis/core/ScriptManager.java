package org.metamorphosis.core;

import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import groovy.util.GroovyScriptEngine;

public class ScriptManager {

	private static ScriptManager instance;
	private final ServletContext context;
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
		engine.setConfig(new CompilerConfiguration().addCompilationCustomizers(createCompilationCustomizer()));
		return engine;
	}
	
	private ImportCustomizer createCompilationCustomizer() {
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