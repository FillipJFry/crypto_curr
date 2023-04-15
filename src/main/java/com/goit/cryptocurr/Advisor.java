package com.goit.cryptocurr;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class Advisor implements IAdvisor {

	private final IDataProvider provider;
	private final HashMap<String, CryptoCurrencyImpl> currencies;

	public Advisor(IDataProvider provider) {

		this.provider = provider;
		currencies = new HashMap<>();

		List<String> currNames = provider.getCurrenciesList();
		for (String name : currNames)
			currencies.put(name, new CryptoCurrencyImpl(name, provider));
	}

	@Override
	public Optional<ICryptoCurrency> pickByName(String name) {

		CryptoCurrencyImpl currency = currencies.get(name);
		if (currency == null) {
			assert (!provider.currencyExists(name));
			return Optional.empty();
		}

		return Optional.of(currency);
	}

	@Override
	public Optional<ICryptoCurrency> pickByMinPrice() {

		return pick(ICryptoCurrency::min, new PriceComparator());
	}

	@Override
	public Optional<ICryptoCurrency> pickByMaxPrice() {

		return pick(CryptoCurrencyImpl::max, new PriceRevComparator());
	}

	@Override
	public Optional<ICryptoCurrency> pickClosestToAvg() {

		return pick(CryptoCurrencyImpl::devFromAvg, new PriceComparator());
	}

	@Override
	public Optional<ICryptoCurrency> pickClosestToNorm() {

		return pick(CryptoCurrencyImpl::devFromNorm, new PriceComparator());
	}

	private Optional<ICryptoCurrency> pick(Function<CryptoCurrencyImpl, BigDecimal> operation,
										   Comparator<BigDecimal> comparator) {

		Iterator<CryptoCurrencyImpl> p = currencies.values().iterator();
		if (!p.hasNext())
			return Optional.empty();

		CryptoCurrencyImpl curr = p.next();
		while (p.hasNext()) {
			CryptoCurrencyImpl item = p.next();
			BigDecimal newValue = operation.apply(item);
			BigDecimal currValue = operation.apply(curr);

			if (comparator.compare(newValue, currValue) < 0)
				curr = item;
		}
		return Optional.of(curr);
	}

	private static class PriceComparator implements Comparator<BigDecimal> {

		@Override
		public int compare(BigDecimal l, BigDecimal r) {

			return l.compareTo(r);
		}
	}

	private static class PriceRevComparator implements Comparator<BigDecimal> {

		@Override
		public int compare(BigDecimal l, BigDecimal r) {

			return r.compareTo(l);
		}
	}
}
