package com.goit.cryptocurr.providers.iterators;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.ResourcesHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CSVIteratorTest {

	private static Path testDataPath;

	@BeforeAll
	static void init() {

		testDataPath = ResourcesHelper.getTestResourcesRoot();
	}

	@Test
	void correctFileOfTwoRecords() {

		Path path = testDataPath.resolve(Paths.get("DOGE_values.csv"));
		try (IRecordsIterator p = new CSVIterator(path)) {
			assertTrue(p.hasNext());
			CryptoCurrRecord rec = p.next();
			assertEquals("DOGE", rec.getName());
			assertEquals(new BigDecimal("0.1702"), rec.getPrice());
			assertEquals(1641013200000L, rec.getDate().getTime());

			assertTrue(p.hasNext());
			rec = p.next();
			assertEquals("DOGE", rec.getName());
			assertEquals(new BigDecimal("0.1722"), rec.getPrice());
			assertEquals(1641074400000L, rec.getDate().getTime());

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
			Path path = testDataPath.resolve(Paths.get("LTC_values.csv"));
			try (IRecordsIterator p = new CSVIterator(path)) {
				assertFalse(p.hasNext());
			}

			path = testDataPath.resolve(Paths.get("XRP_values.csv"));
			try (IRecordsIterator p = new CSVIterator(path)) {
				assertFalse(p.hasNext());
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}