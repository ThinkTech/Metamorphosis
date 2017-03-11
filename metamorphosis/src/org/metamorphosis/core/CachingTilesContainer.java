package org.metamorphosis.core;

import java.util.HashMap;
import java.util.Map;
import org.apache.tiles.Definition;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.definition.DefinitionsFactoryException;

public class CachingTilesContainer extends org.apache.tiles.impl.mgmt.CachingTilesContainer {

	private Map<String, Definition> definitions = new HashMap<String, Definition>();

	public void register(Definition definition) throws DefinitionsFactoryException {
		definitions.put(definition.getName(), definition);
	}

	public Definition getDefinition(String definitionName, TilesRequestContext context) throws DefinitionsFactoryException {
		Definition definition = definitions.get(definitionName);
		return definition != null ? definition : super.getDefinition(definitionName, context);
	}
	

}
