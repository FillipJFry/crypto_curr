package com.goit.cryptocurr;

import java.io.IOException;

public interface IDataProvider {

	boolean currencyExists(String name);
	IRecordsIterator getRecordsIterator(String name) throws IOException;
}
