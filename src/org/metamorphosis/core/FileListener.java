package org.metamorphosis.core;

public interface FileListener {

	public void onCreate(String file);
	public void onDelete(String file);
	
}