package org.metamorphosis.core;

public abstract class AbstractListener {

	protected void execute(String method) {		
        try {
			this.getClass().getDeclaredMethod(method).invoke(this);
		} catch (NoSuchMethodException e) {
		}
        catch (Exception e) {
        	e.printStackTrace();
		}
	}
	
}