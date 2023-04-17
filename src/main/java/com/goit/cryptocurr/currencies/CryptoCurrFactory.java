package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.IDataProvider;

public class CryptoCurrFactory {

	public static ICryptoCurrencyEx create(String name, IDataProvider provider) {

		long dataSize = provider.getDataTotalSize();
		if (dataSize < 1e6)
			return new CryptoCurrStreamed(name, provider);
		else
			return new CryptoCurrWithIterator(name, provider);
	}
}
