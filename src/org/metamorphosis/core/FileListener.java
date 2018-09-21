package org.metamorphosis.core;

public interface FileListener {

	public void onFileCreated(String name);
	public void onFileDeleted(String name);
	
}