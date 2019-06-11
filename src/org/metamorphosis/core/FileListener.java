package org.metamorphosis.core;

public interface FileListener {

	public void onCreate(String fileName);
	public void onDelete(String fileName);
	
}