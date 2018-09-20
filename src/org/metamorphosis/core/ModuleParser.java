package org.metamorphosis.core;

import java.io.File;

public interface ModuleParser {

	public Module parse(File metadata) throws Exception;
	
}
