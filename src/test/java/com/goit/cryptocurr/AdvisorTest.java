package com.goit.cryptocurr;

import com.goit.cryptocurr.providers.ExtensionHandlersRegistry;
import com.goit.cryptocurr.providers.FileDataProvider;
import com.goit.cryptocurr.providers.ext_handlers.CSVHandler;
import com.goit.cryptocurr.providers.ext_handlers.JSONHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AdvisorTest {

	private Advisor advisor;

	@BeforeAll
	static void initRegistry() {

		ExtensionHandlersRegistry.add("csv", new CSVHandler.Factory());
		ExtensionHandlersRegistry.add("txt", new JSONHandler.Factory());
	}

	@BeforeEach
	void init() {

		URL url = Advisor.class.getResource("/prices");
		assert url != null;

		try {
			FileDataProvider provider = new FileDataProvider(url.getPath());
			advisor = new Advisor(provider);
		}
		catch (Exception e) {

			System.err.println(e.getMessage());
		}
	}

	@Test
	void pickByName() {

		try {
			Optional<ICryptoCurrency> doge = advisor.pickByName("DOGE");
			assertFalse(doge.isEmpty());
			assertEquals("DOGE", doge.get().getName());
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void pickByMinPrice() {

		try {
			Optional<ICryptoCurrency> minCurr = advisor.pickByMinPrice();
			assertFalse(minCurr.isEmpty());
			assertEquals("DOGE", minCurr.get().getName());
			assertEquals(new BigDecimal("0.129"), minCurr.get().min());
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void pickByMaxPrice() {

		try {
			Optional<ICryptoCurrency> maxCurr = advisor.pickByMaxPrice();
			assertFalse(maxCurr.isEmpty());
			assertEquals("BTC", maxCurr.get().getName());
			assertEquals(new BigDecimal(3327658), maxCurr.get().min());
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void pickClosestToAvg() {
	}

	@Test
	void pickClosestToNorm() {
	}

	@AfterAll
	static void resetRegistry() {

		ExtensionHandlersRegistry.reset();
	}
}