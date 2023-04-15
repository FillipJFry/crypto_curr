package com.goit.cryptocurr.providers.ext_handlers;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

public class CSVHandler implements IRecordsIterator {

	private static final Logger logger = LogManager.getRootLogger();
	private final BufferedReader reader;

	public CSVHandler(Path path) throws IOException {

		reader = new BufferedReader(new FileReader(path.toString()));
		if (reader.ready()) {
			String header = reader.readLine();
			String[] fieldNames = header.split(",");

			logger.info("the csv-file: " + path.toString() +
						", fields: " + Arrays.toString(fieldNames));
			assert fieldNames.length == 3;
			assert fieldNames[0].equals("timestamp");
			assert fieldNames[1].equals("symbol");
			assert fieldNames[2].equals("price");
		}
	}

	@Override
	public boolean hasNext() {

		try {
			return reader.ready();
		}
		catch (IOException e) {

			logger.error("CSVHandler::hasNext(): " + e);
			return false;
		}
	}

	@Override
	public CryptoCurrRecord next() {

		try {
			String line = reader.readLine();
			String[] values = line.split(",");
			if (values.length != 3) {
				logger.error("CSVHandler::next(): wrong record: " + line);
				return null;
			}

			return new CryptoCurrRecord(values[1], new BigDecimal(values[2]),
										new Date(Integer.parseInt(values[0])));
		}
		catch (Exception e) {

			logger.error("CSVHandler::next(): " + e);
			return null;
		}
	}

	@Override
	public void remove() {

		throw new UnsupportedOperationException("CSVHandler doesn't support remove()");
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
}
