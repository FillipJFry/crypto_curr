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
	private final static Gson gson = new Gson();
	private final static DateFormat dtFormat = new SimpleDateFormat("yy-MM-dd hh:mm");
  private BufferedReader reader;

  public JSONIterator(Path path) {
      if (reader == null) {
        try {
          reader = new BufferedReader(new FileReader(path.toString()));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
  }

	@Override
	public boolean hasNext() {

		try {
			return reader.ready();
		}
		catch (IOException e) {

      /*
      this way of logging greatly complicates the code.
      It's a bad idea to pass method names in method parameters and then output them to the logs.
      instead of tis will be better to use another logger method for logging errors: logger.error("some custom comments ", e);
      (coma instead of plus in this case we can see all stacktrace in console)
      the same for all cases!
      */
			logger.error("JSONHandler::hasNext(): " + e);
			return false;
		}
	}

	@Override
	public CryptoCurrRecord next() {

		try {
			String line = reader.readLine();
			JSONRecord rec = gson.fromJson(line, JSONRecord.class);
      /*
      in this case, it is necessary to configure the gson (with appropriate annotations over the model field)
      instead of creating a 'JSONRecord' class that is needed only to parse the date manually
      */
			Date dt = dtFormat.parse(rec.timestamp.replace('T', ' '));
			return new CryptoCurrRecord(rec.symbol, rec.price, dt);
		}
		catch (Exception e) {
      //same problem as class CSVIterator
			logger.error("JSONHandler::next(): " + e);
			return null;
		}
	}

  //same problem as class CSVIterator
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

  //this class is redundant
	private static class JSONRecord {

		String symbol;
		BigDecimal price;
		String timestamp;
	}
}
