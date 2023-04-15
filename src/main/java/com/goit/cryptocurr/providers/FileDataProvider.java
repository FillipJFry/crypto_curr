package com.goit.cryptocurr.providers;

import com.goit.cryptocurr.IDataProvider;
import com.goit.cryptocurr.IRecordsIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDataProvider implements IDataProvider {

	private static final Logger logger = LogManager.getRootLogger();
	private final HashMap<String, Pair> currencyFiles;

	public FileDataProvider(String dir) throws Exception {

		this(Paths.get(dir));
	}

	public FileDataProvider(Path dirPath) throws Exception {

		currencyFiles = new HashMap<>();

		if (!Files.exists(dirPath))
			throw new Exception("the path does not exist: " + dirPath);

		Pattern fileNamePattern = Pattern.compile("([A-Z]+)_values\\.([a-z0-9]+)");
		Matcher m = fileNamePattern.matcher("");

		Files.walkFileTree(dirPath, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(Path file,
											 BasicFileAttributes attrs) throws IOException {

				String fileName = file.getFileName().toString();
				m.reset(fileName);
				if (m.find()) {
					String currency = m.group(1);
					String ext = m.group(2);
					IExtensionHandlerFactory factory = ExtensionHandlersRegistry.getFactory(ext);

					if (factory != null)
						currencyFiles.put(currency, new Pair(file, factory));
					else
						logger.error("a handler for the extension: ." + ext + " is not registered");
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Override
	public boolean currencyExists(String name) {

		return currencyFiles.containsKey(name);
	}

	@Override
	public List<String> getCurrenciesList() {

		return new ArrayList<>(currencyFiles.keySet());
	}

	@Override
	public IRecordsIterator getRecordsIterator(String name) throws IOException {

		assert currencyFiles.containsKey(name);
		Pair p = currencyFiles.get(name);
		return p.factory.create(p.path);
	}

	private static final class Pair {

		Path path;
		IExtensionHandlerFactory factory;

		Pair(Path path, IExtensionHandlerFactory factory) {

			this.path = path;
			this.factory = factory;
		}
	}
}
