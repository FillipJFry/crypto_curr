package com.goit.cryptocurr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class Advisor implements IAdvisor {

	private final IDataProvider provider;
	private final HashMap<String, CryptoCurrencyImpl> currencies;

	public Advisor(IDataProvider provider) {

		this.provider = provider;
		currencies = new HashMap<>();
	}

	@Override
	public Optional<ICryptoCurrency> pickByName(String name) throws Exception {

		CryptoCurrencyImpl currency = currencies.get(name);
		if (currency == null) {
			if (!provider.currencyExists(name))
				return Optional.empty();

			currency = new CryptoCurrencyImpl(name, provider);
			currencies.put(name, currency);
		}

		return Optional.of(currency);
	}

	@Override
	public Optional<ICryptoCurrency> pickByMinPrice() throws Exception {

		Iterator<CryptoCurrencyImpl> p = currencies.values().iterator();
		if (!p.hasNext())
			return Optional.empty();

		CryptoCurrencyImpl minPriceCurr = p.next();
		while (p.hasNext()) {
			CryptoCurrencyImpl curr = p.next();
			if (curr.min().compareTo(minPriceCurr.min()) > 0)
				minPriceCurr = curr;
		}
		return Optional.of(minPriceCurr);
	}

	@Override
	public Optional<ICryptoCurrency> pickByMaxPrice() throws Exception {

		Iterator<CryptoCurrencyImpl> p = currencies.values().iterator();
		if (!p.hasNext())
			return Optional.empty();

		CryptoCurrencyImpl maxPriceCurr = p.next();
		while (p.hasNext()) {
			CryptoCurrencyImpl curr = p.next();
			if (curr.min().compareTo(maxPriceCurr.min()) < 0)
				maxPriceCurr = curr;
		}
		return Optional.of(maxPriceCurr);
	}

	@Override
	public Optional<ICryptoCurrency> pickClosestToAvg() {

		Iterator<CryptoCurrencyImpl> p = currencies.values().iterator();
		if (!p.hasNext())
			return Optional.empty();

		CryptoCurrencyImpl closest = p.next();
		while (p.hasNext()) {
			CryptoCurrencyImpl curr = p.next();
			if (curr.devFromAvg().compareTo(closest.devFromAvg()) < 0)
				closest = curr;
		}
		return Optional.of(closest);
	}

	@Override
	public Optional<ICryptoCurrency> pickClosestToNorm() {

		Iterator<CryptoCurrencyImpl> p = currencies.values().iterator();
		if (!p.hasNext())
			return Optional.empty();

		CryptoCurrencyImpl closest = p.next();
		while (p.hasNext()) {
			CryptoCurrencyImpl curr = p.next();
			if (curr.devFromNorm().compareTo(closest.devFromNorm()) < 0)
				closest = curr;
		}
		return Optional.of(closest);
	}
}
