package com.goit.cryptocurr.providers.iterators;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.providers.IExtensionHandlerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Date;
import java.util.function.Consumer;

public class CSVIterator implements IRecordsIterator {

	private static final Logger logger = LogManager.getRootLogger();	
	private final BufferedReader reader;
  private boolean headerIsOK;

  public CSVIterator(Path path) throws IOException {

    reader = new BufferedReader(new FileReader(path.toString()));
    headerIsOK = false;
    if (reader.ready()) {
      /*
       * we can simplify it, but it's still a bad solution. what happens if the columns are swapped?
       * we must to parse files based on the column name and not of its position
       */
      String header = reader.readLine();
      headerIsOK = "timestamp,symbol,price".equals(header);
      if (!headerIsOK)
        logger.error("the csv-file " + path + " has a wrong fields set: " + header);
    }
  }

	@Override
	public boolean hasNext() {

		try {
			return headerIsOK && reader.ready();
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
        /*
        why null? 
        here we must to throw an exception. 
        if the file contains an error, then the analysis of the data will be erroneous. 
        the client should know about it, and not receive a calculation with an error in the future
        */
				return null;
			}
      
			return new CryptoCurrRecord(values[1], new BigDecimal(values[2]),
										new Date(Long.parseLong(values[0])));
		}
		catch (Exception e) {
      //its the same case
			logger.error("CSVHandler::next(): " + e);
			return null;
		}
	}
  
 /*
  what is the reason of overriding the default interface method without changing the default behavior?
  */
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

			return new CSVIterator(path);
		}
	}
}
