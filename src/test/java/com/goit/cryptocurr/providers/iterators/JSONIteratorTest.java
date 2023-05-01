package com.goit.cryptocurr.providers.iterators;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.ResourcesHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

class JSONIteratorTest {

	private static Path testDataPath = null;

	@BeforeAll
	static void init() {

		testDataPath = ResourcesHelper.getTestResourcesRoot();
	}

	@Test
	void correctFileOfTwoRecords() {

		Path path = testDataPath.resolve(Paths.get("BTC_values.txt"));
		DateFormat dtFormat = new SimpleDateFormat("yy-MM-dd hh:mm");

		try (IRecordsIterator p = new JSONIterator(path)) {
			assertTrue(p.hasNext());
			CryptoCurrRecord rec = p.next();
			assertEquals("BTC", rec.getName());
			assertEquals(new BigDecimal(4681321), rec.getPrice());
			assertEquals(dtFormat.parse("2022-01-01 04:00"), rec.getDate());

			assertTrue(p.hasNext());
			rec = p.next();
			assertEquals("BTC", rec.getName());
			assertEquals(new BigDecimal(4697961), rec.getPrice());
			assertEquals(dtFormat.parse("2022-01-01 07:00"), rec.getDate());

			assertFalse(p.hasNext());
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void emptyFile() {

		try {
			Path path = testDataPath.resolve(Paths.get("ETH_values.txt"));
			try (IRecordsIterator p = new JSONIterator(path)) {
				assertFalse(p.hasNext());
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}