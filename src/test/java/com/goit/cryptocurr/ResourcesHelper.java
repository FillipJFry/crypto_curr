package com.goit.cryptocurr;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class ResourcesHelper {

	public static Path getTestResourcesRoot() {

		return getResourcesRoot(ResourcesHelper.class, "/");
	}

	public static Path getResourcesRoot() {

		return getResourcesRoot(Advisor.class, "/prices");
	}

	private static <T> Path getResourcesRoot(Class<T> cl, String resBasicPath) {

		URL url = cl.getResource(resBasicPath);
		assertNotNull(url);

		try {
			return Paths.get(url.toURI());
		}
		catch (URISyntaxException e) {

			assertNull(e);
			return null;
		}
	}
}
