package org.metamorphosis.core;

public class BaseListener {

	public void execute(String method) {		
        try {
			this.getClass().getDeclaredMethod(method).invoke(this);
		} catch (NoSuchMethodException e) {
		}
        catch (Exception e) {
        	e.printStackTrace();
		}
	}
	
}