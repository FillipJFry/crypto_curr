package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.ICryptoCurrency;
import com.goit.cryptocurr.ResourcesHelper;
import com.goit.cryptocurr.providers.FileDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CryptoCurrWithIteratorTest {

	private FileDataProvider provider;

	@BeforeEach
	void init() {

		try {
			provider = new FileDataProvider(ResourcesHelper.getResourcesRoot());
		}
		catch (Exception e) {

			assertNull(e);
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
			CryptoCurrWithIterator curr = new CryptoCurrWithIterator("DOGE", provider);
			assertEquals(new BigDecimal("0.15316"),
							curr.avg(new Date(0), new Date(Long.MAX_VALUE)));
		}
		catch (Exception e) {

			assertNull(e);
		}
	}

	@Test
	void normOfDOGE() {

		testStat("DOGE", new BigDecimal("0.50465"), ICryptoCurrency::norm);
	}

	@Test
	void devFromAvgOfDOGE() {

		testStat("DOGE", new BigDecimal("0.01301"), CryptoCurrWithIterator::devFromAvg);
	}

	@Test
	void devFromNormOfDOGE() {

		testStat("DOGE", new BigDecimal("0.35149"), CryptoCurrWithIterator::devFromNorm);
	}

	private void testStat(String name, BigDecimal expected,
						  Function<CryptoCurrWithIterator, BigDecimal> predicate) {

		try {
			CryptoCurrWithIterator curr = new CryptoCurrWithIterator(name, provider);
			assertEquals(expected, predicate.apply(curr));
		}
		catch (Exception e) {

			assertNull(e);
		}
	}
}