package org.metamorphosis.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Expando extends groovy.util.Expando implements Map<Object,Object> {

	@Override
	public int size() {
		return getProperties().size();
	}

	@Override
	public boolean isEmpty() {
		return getProperties().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getProperties().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getProperties().containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return getProperties().get(key);
	}

	
	@Override
	public Object put(Object key, Object value) {
		return getProperties().put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return getProperties().remove(key);
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		getProperties().putAll(m);
	}

	@Override
	public void clear() {
		getProperties().clear();
	}

	@Override
	public Set<Object> keySet() {
		return getProperties().keySet();
	}

	@Override
	public Collection<Object> values() {
		return getProperties().values();
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return getProperties().entrySet();
	}
	
}