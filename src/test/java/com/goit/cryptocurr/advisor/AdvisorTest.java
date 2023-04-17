package com.goit.cryptocurr.advisor;

import com.goit.cryptocurr.ICryptoCurrency;
import com.goit.cryptocurr.ResourcesHelper;
import com.goit.cryptocurr.providers.ExtensionHandlersRegistry;
import com.goit.cryptocurr.providers.FileDataProvider;
import com.goit.cryptocurr.providers.iterators.CSVIterator;
import com.goit.cryptocurr.providers.iterators.JSONIterator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AdvisorTest {

	private Advisor advisor;

	@BeforeAll
	static void initRegistry() {

		ExtensionHandlersRegistry.add("csv", new CSVIterator.Factory());
		ExtensionHandlersRegistry.add("txt", new JSONIterator.Factory());
	}

	@BeforeEach
	void init() {

		try {
			FileDataProvider provider = new FileDataProvider(ResourcesHelper.getResourcesRoot());
			advisor = new Advisor(provider);
		}
		catch (Exception e) {

			assertNull(e);
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
		}
	}

	@Test
	void pickByMaxPrice() {

		try {
			Optional<ICryptoCurrency> maxCurr = advisor.pickByMaxPrice();
			assertFalse(maxCurr.isEmpty());
			assertEquals("BTC", maxCurr.get().getName());
			assertEquals(new BigDecimal(4772266), maxCurr.get().max());
		}
		catch (Exception e) {

			assertNull(e);
		}
	}

	@Test
	void pickClosestToAvg() {

		try {
			Optional<ICryptoCurrency> avgCurr = advisor.pickClosestToAvg();
			assertFalse(avgCurr.isEmpty());
			assertEquals("DOGE", avgCurr.get().getName());
			assertEquals(new BigDecimal("0.15316"),
						avgCurr.get().avg(new Date(0), new Date(Long.MAX_VALUE)));
		}
		catch (Exception e) {

			assertNull(e);
		}
	}

	@Test
	void pickClosestToNorm() {

		try {
			Optional<ICryptoCurrency> normCurr = advisor.pickClosestToNorm();
			assertFalse(normCurr.isEmpty());
			assertEquals("XRP", normCurr.get().getName());
			assertEquals(new BigDecimal("0.50605"), normCurr.get().norm());
		}
		catch (Exception e) {

			assertNull(e);
		}
	}

	@AfterAll
	static void resetRegistry() {

		ExtensionHandlersRegistry.reset();
	}
}