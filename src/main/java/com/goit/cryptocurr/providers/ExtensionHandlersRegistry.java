package com.goit.cryptocurr.providers;

import com.goit.cryptocurr.providers.iterators.CSVIterator;
import com.goit.cryptocurr.providers.iterators.JSONIterator;

import java.util.HashMap;

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
