package com.goit.cryptocurr;

import com.goit.cryptocurr.currencies.CryptoCurrFactory;
import com.goit.cryptocurr.currencies.ICryptoCurrencyEx;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class Advisor {

	private final IDataProvider provider;
	private final HashMap<String, ICryptoCurrencyEx> currencies;

	public Advisor(IDataProvider provider) {

		this.provider = provider;
		currencies = new HashMap<>();

		List<String> currNames = provider.getCurrenciesList();
		for (String name : currNames)
			currencies.put(name, CryptoCurrFactory.create(name, provider));
	}

	public Optional<ICryptoCurrency> pickByName(String name) {

		ICryptoCurrencyEx currency = currencies.get(name);
		if (currency == null) {
			assert (!provider.currencyExists(name));
			return Optional.empty();
		}

		return Optional.of(currency);
	}

	public Optional<ICryptoCurrency> pickByMinPrice() {

		return pick(ICryptoCurrency::min, new PriceComparator());
	}

	public Optional<ICryptoCurrency> pickByMaxPrice() {

		return pick(ICryptoCurrency::max, new PriceRevComparator());
	}

	public Optional<ICryptoCurrency> pickClosestToAvg() {

		return pick(ICryptoCurrencyEx::devFromAvg, new PriceComparator());
	}

	public Optional<ICryptoCurrency> pickClosestToNorm() {

		return pick(ICryptoCurrencyEx::devFromNorm, new PriceComparator());
	}

	private Optional<ICryptoCurrency> pick(Function<ICryptoCurrencyEx, BigDecimal> operation,
										   Comparator<BigDecimal> comparator) {

		Iterator<ICryptoCurrencyEx> p = currencies.values().iterator();
		ICryptoCurrencyEx curr = null;
		BigDecimal currValue = null;
		boolean priceIsNull = true;
		while (priceIsNull && p.hasNext()) {

			curr = p.next();
			currValue = operation.apply(curr);
			priceIsNull = currValue.equals(PriceConstants.NULL_PRICE);
		}
		if (priceIsNull) return Optional.empty();

		while (p.hasNext()) {
			ICryptoCurrencyEx item = p.next();
			BigDecimal newValue = operation.apply(item);

			priceIsNull = newValue.equals(PriceConstants.NULL_PRICE);
			if (!priceIsNull && comparator.compare(newValue, currValue) < 0) {
				curr = item;
				currValue = newValue;
			}
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
