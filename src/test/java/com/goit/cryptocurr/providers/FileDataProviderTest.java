package com.goit.cryptocurr.providers;

import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.providers.ext_handlers.CSVHandler;
import com.goit.cryptocurr.providers.ext_handlers.JSONHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class FileDataProviderTest {

	private static String testDataPath;

	@BeforeAll
	static void init() {

		ExtensionHandlersRegistry.add("csv", new CSVHandler.Factory());
		ExtensionHandlersRegistry.add("txt", new JSONHandler.Factory());
		URL url = FileDataProviderTest.class.getResource("/");
		assert url != null;

		testDataPath = url.getPath();
	}

	@Test
	void currencyExists() {

		try {
			FileDataProvider provider = new FileDataProvider(testDataPath);
			assertTrue(provider.currencyExists("BTC"));
			assertTrue(provider.currencyExists("DOGE"));
			assertTrue(provider.currencyExists("ETH"));
			assertTrue(provider.currencyExists("LTC"));
			assertTrue(provider.currencyExists("XRP"));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void checkNonExistingCurrencies() {

		try {
			FileDataProvider provider = new FileDataProvider(testDataPath);
			assertFalse(provider.currencyExists("Tugrik"));
			assertFalse(provider.currencyExists("XYZ"));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void recordsIteratorForDOGEisCSV() {

		testRecordsIteratorIsCSV("DOGE");
	}

	@Test
	void recordsIteratorForLTCisCSV() {

		testRecordsIteratorIsCSV("LTC");
	}

	@Test
	void recordsIteratorForXRPisCSV() {

		testRecordsIteratorIsCSV("XRP");
	}

	@Test
	void recordsIteratorForBTCisJSON() {

		testRecordsIteratorIsJSON("BTC");
	}

	@Test
	void recordsIteratorForETHisJSON() {

		testRecordsIteratorIsJSON("ETH");
	}

	@AfterAll
	static void resetRegistry() {

		ExtensionHandlersRegistry.reset();
	}

	private void testRecordsIteratorIsCSV(String currency) {

		try {
			FileDataProvider provider = new FileDataProvider(testDataPath);
			try (IRecordsIterator p = provider.getRecordsIterator(currency)) {
				assertTrue(p instanceof CSVHandler);
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	private void testRecordsIteratorIsJSON(String currency) {

		try {
			FileDataProvider provider = new FileDataProvider(testDataPath);
			try (IRecordsIterator p = provider.getRecordsIterator(currency)) {
				assertTrue(p instanceof JSONHandler);
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}