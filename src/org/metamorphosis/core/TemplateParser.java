package org.metamorphosis.core;

import java.io.File;

public interface TemplateParser {

	public Template parse(File metadata) throws Exception;
	
}
