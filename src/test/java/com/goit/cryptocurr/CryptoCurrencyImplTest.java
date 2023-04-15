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
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCurrencyImplTest {

	private FileDataProvider provider;

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
			provider = new FileDataProvider(url.getPath());
		}
		catch (Exception e) {

			System.err.println(e.getMessage());
		}
	}

	@Test
	void minOfLTC() {

		testStat("LTC", new BigDecimal("103.4"), ICryptoCurrency::min);
	}

	@Test
	void minOfXRP() {

		testStat("XRP", new BigDecimal("0.5616"), ICryptoCurrency::min);
	}

	@Test
	void maxOfDOGE() {

		testStat("DOGE", new BigDecimal("0.1941"), ICryptoCurrency::max);
	}

	@Test
	void maxOfBTC() {

		testStat("BTC", new BigDecimal(4772266), ICryptoCurrency::max);
	}

	@Test
	void avgOfDOGE() {

		try {
			CryptoCurrencyImpl curr = new CryptoCurrencyImpl("DOGE", provider);
			assertEquals(new BigDecimal("0.15316"),
							curr.avg(new Date(0), new Date(Long.MAX_VALUE)));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void normOfDOGE() {

		testStat("DOGE", new BigDecimal("0.50465"), ICryptoCurrency::norm);
	}

	@Test
	void devFromAvgOfDOGE() {

		testStat("DOGE", new BigDecimal("0.01301"), CryptoCurrencyImpl::devFromAvg);
	}

	@Test
	void devFromNormOfDOGE() {

		testStat("DOGE", new BigDecimal("0.35149"), CryptoCurrencyImpl::devFromNorm);
	}

	@AfterAll
	static void resetRegistry() {

		ExtensionHandlersRegistry.reset();
	}

	private void testStat(String name, BigDecimal expected,
						  Function<CryptoCurrencyImpl, BigDecimal> predicate) {

		try {
			CryptoCurrencyImpl curr = new CryptoCurrencyImpl(name, provider);
			assertEquals(expected, predicate.apply(curr));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}