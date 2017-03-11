package org.metamorphosis.core;

public interface FileListener {

	public void onCreated(String file);
	public void onDeleted(String file);
	
}
