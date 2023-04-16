package com.goit.cryptocurr.providers.iterators;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.providers.IExtensionHandlerFactory;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class JSONIterator implements IRecordsIterator {

	private static final Logger logger = LogManager.getRootLogger();
	private final BufferedReader reader;
	private final Gson gson;
	private final DateFormat dtFormat;

	public JSONIterator(Path path) throws IOException {

		reader = new BufferedReader(new FileReader(path.toString()));
		gson = new Gson();
		dtFormat = new SimpleDateFormat("yy-MM-dd hh:mm");
	}

	@Override
	public boolean hasNext() {

		try {
			return reader.ready();
		}
		catch (IOException e) {

			logger.error("JSONHandler::hasNext(): " + e);
			return false;
		}
	}

	@Override
	public CryptoCurrRecord next() {

		try {
			String line = reader.readLine();
			JSONRecord rec = gson.fromJson(line, JSONRecord.class);
			Date dt = dtFormat.parse(rec.timestamp.replace('T', ' '));
			return new CryptoCurrRecord(rec.symbol, rec.price, dt);
		}
		catch (Exception e) {

			logger.error("JSONHandler::next(): " + e);
			return null;
		}
	}

	@Override
	public void remove() {

		throw new UnsupportedOperationException("JSONHandler doesn't support remove()");
	}

	@Override
	public void forEachRemaining(Consumer<? super CryptoCurrRecord> action) {

		while (hasNext())
			action.accept(next());
	}

	@Override
	public void close() throws IOException {

		reader.close();
	}

	public static class Factory implements IExtensionHandlerFactory {

		@Override
		public IRecordsIterator create(Path path) throws IOException {

			return new JSONIterator(path);
		}
	}

	private static class JSONRecord {

		String symbol;
		BigDecimal price;
		String timestamp;
	}
}
