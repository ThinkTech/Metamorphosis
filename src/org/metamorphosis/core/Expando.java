package org.metamorphosis.core;

import java.util.Iterator;
import java.util.Map;

public class Expando extends groovy.util.Expando{

	public Expando(){
		super();
	}
	
	@SuppressWarnings("rawtypes")
	public Expando(Map row){
		Iterator it = row.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        setProperty(pair.getKey().toString(),pair.getValue());
	    }
	}
	
}
