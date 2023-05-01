package com.goit.cryptocurr;

import java.util.List;
import java.util.stream.Stream;

public interface IDataProvider {

	boolean currencyExists(String name);
	List<String> getCurrenciesList();
	IRecordsIterator getRecordsIterator(String name);
	Stream<CryptoCurrRecord> getRecordsStream(String name);
	long getDataTotalSize();
}
