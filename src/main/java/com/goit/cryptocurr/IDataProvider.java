package com.goit.cryptocurr;

import java.util.List;
import java.io.IOException;
import java.util.stream.Stream;

public interface IDataProvider {

	boolean currencyExists(String name);
	List<String> getCurrenciesList();
	IRecordsIterator getRecordsIterator(String name) throws IOException;
	Stream<CryptoCurrRecord> getRecordsStream(String name) throws Exception;
	long getDataTotalSize();
}
