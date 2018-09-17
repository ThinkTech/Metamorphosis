package org.metamorphosis.core;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;

public class TilesContainerFactory extends org.apache.tiles.factory.TilesContainerFactory {

	public TilesContainer createContainer(Object context) throws TilesException {
		CachingTilesContainer container = new CachingTilesContainer();
		initializeContainer(context, container);
		return container;
	}

}