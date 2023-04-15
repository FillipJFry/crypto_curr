package com.goit.cryptocurr;

import java.util.List;
import java.io.IOException;

public interface IDataProvider {

	boolean currencyExists(String name);
	List<String> getCurrenciesList();
	IRecordsIterator getRecordsIterator(String name) throws IOException;
}
