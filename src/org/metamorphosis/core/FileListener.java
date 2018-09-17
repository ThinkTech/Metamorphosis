package org.metamorphosis.core;

public interface FileListener {

	public void onFileCreated(String file);
	public void onFileDeleted(String file);
	
}