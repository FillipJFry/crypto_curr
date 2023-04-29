package com.goit.cryptocurr.providers;

import com.goit.cryptocurr.providers.iterators.CSVIterator;
import com.goit.cryptocurr.providers.iterators.JSONIterator;

import java.util.HashMap;

/*
It seems to me that this is an overly complex object creation system.
besides, I would prefer to have a factory that will create all parser instances by itself 
as soon as new parser classes are added (no changes to other classes and the factory itself)
*/
public class ExtensionHandlersRegistry {

	private static final HashMap<String,
								IExtensionHandlerFactory> registry = new HashMap<>();
      
	static {

		registry.put("csv", new CSVIterator.Factory());
		registry.put("txt", new JSONIterator.Factory());
  }
  
	public static IExtensionHandlerFactory getFactory(String extension) {

		return registry.get(extension);
  }
}
