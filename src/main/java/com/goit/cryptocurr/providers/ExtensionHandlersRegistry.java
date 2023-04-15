package com.goit.cryptocurr.providers;

import java.util.HashMap;

public class ExtensionHandlersRegistry {

	private static final HashMap<String,
								IExtensionHandlerFactory> registry = new HashMap<>();

	public static void add(String extension, IExtensionHandlerFactory factory) {

		registry.put(extension, factory);
	}

	public static IExtensionHandlerFactory getFactory(String extension) {

		return registry.get(extension);
	}
}
