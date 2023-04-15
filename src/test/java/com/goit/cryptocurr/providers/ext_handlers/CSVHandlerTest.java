package com.goit.cryptocurr.providers.ext_handlers;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CSVHandlerTest {

	private static String testDataPath;

	@BeforeAll
	static void init() {

		URL url = CSVHandlerTest.class.getResource("/");
		assert url != null;

		testDataPath = url.getPath();
	}

	@Test
	void correctFileOfTwoRecords() {

		Path path = Paths.get(testDataPath, "DOGE_values.csv");
		try (IRecordsIterator p = new CSVHandler(path)) {
			assertTrue(p.hasNext());
			CryptoCurrRecord rec = p.next();
			assertEquals("DOGE", rec.name);
			assertEquals(new BigDecimal("0.1702"), rec.price);
			assertEquals(1641013200000L, rec.date.getTime());

			assertTrue(p.hasNext());
			rec = p.next();
			assertEquals("DOGE", rec.name);
			assertEquals(new BigDecimal("0.1722"), rec.price);
			assertEquals(1641074400000L, rec.date.getTime());

			assertFalse(p.hasNext());
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void emptyFiles() {

		try {
			Path path = Paths.get(testDataPath, "LTC_values.csv");
			try (IRecordsIterator p = new CSVHandler(path)) {
				assertFalse(p.hasNext());
			}

			path = Paths.get(testDataPath, "XRP_values.csv");
			try (IRecordsIterator p = new CSVHandler(path)) {
				assertFalse(p.hasNext());
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}