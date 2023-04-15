package com.goit.cryptocurr.providers;

import com.goit.cryptocurr.IRecordsIterator;

import java.io.IOException;
import java.nio.file.Path;

public interface IExtensionHandlerFactory {

	IRecordsIterator create(Path path) throws IOException;
}
