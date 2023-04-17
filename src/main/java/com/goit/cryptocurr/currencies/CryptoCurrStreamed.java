package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IDataProvider;
import com.goit.cryptocurr.PriceConstants;
import com.google.common.collect.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.stream.Stream;

public class CryptoCurrStreamed implements ICryptoCurrencyEx {

	private static final Logger logger = LogManager.getRootLogger();
	private final String name;
	private final IDataProvider provider;

	public CryptoCurrStreamed(String name, IDataProvider provider) {

		this.name = name;
		this.provider = provider;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public BigDecimal min() {

		try (Stream<CryptoCurrRecord> stream = provider.getRecordsStream(name)) {

			return stream.map(rec -> rec.price)
					.min(BigDecimal::compareTo)
					.orElse(PriceConstants.NULL_PRICE);
		}
		catch (Exception e) {

			logger.error("CryptoCurrStreamed::min(): " + e);
			return PriceConstants.NULL_PRICE;
		}
	}

	@Override
	public BigDecimal max() {

		try (Stream<CryptoCurrRecord> stream = provider.getRecordsStream(name)) {

			return stream.map(rec -> rec.price)
					.max(BigDecimal::compareTo)
					.orElse(PriceConstants.NULL_PRICE);
		}
		catch (Exception e) {

			logger.error("CryptoCurrStreamed::max(): " + e);
			return PriceConstants.NULL_PRICE;
		}
	}

	@Override
	public BigDecimal avg(Date start, Date end) {

		try (Stream<CryptoCurrRecord> stream = provider.getRecordsStream(name)) {

			Stream<CryptoCurrRecord> filtered = stream.filter(rec ->
					rec.date.compareTo(start) >= 0 && rec.date.compareTo(end) <= 0);

			return Streams.zip(filtered, Stream.iterate(1, i -> i + 1),
								(rec, i) -> new Pair(i, rec.price))
						.reduce(Pair::add)
						.map(r -> r.price.divide(BigDecimal.valueOf(r.index),
												PriceConstants.DEF_ACCURACY,
												RoundingMode.HALF_UP))
						.orElse(PriceConstants.NULL_PRICE);
		}
		catch (Exception e) {

			logger.error("CryptoCurrStreamed::avg(): " + e);
			return PriceConstants.NULL_PRICE;
		}
	}

	@Override
	public BigDecimal norm() {

		BigDecimal min = min();
		if (min.equals(BigDecimal.ZERO) || min.equals(PriceConstants.NULL_PRICE))
			return PriceConstants.NULL_PRICE;

		BigDecimal max = max();
		if (max.equals(PriceConstants.NULL_PRICE))
			return PriceConstants.NULL_PRICE;

		BigDecimal norm = max.subtract(min);
		return norm.divide(min, PriceConstants.DEF_ACCURACY, RoundingMode.HALF_UP);
	}

	@Override
	public BigDecimal devFromAvg() {

		BigDecimal avg = avg(new Date(0), new Date(Long.MAX_VALUE));
		return dev(avg, "devFromAvg");
	}

	@Override
	public BigDecimal devFromNorm() {

		BigDecimal norm = norm();
		return dev(norm, "devFromNorm");
	}

	private BigDecimal dev(BigDecimal M, String exactMethodName) {

		try (Stream<CryptoCurrRecord> stream = provider.getRecordsStream(name)) {

			return Streams.zip(stream, Stream.iterate(1, i -> i + 1),
							(rec, i) -> new Pair(i, rec.price))
					.map(pair -> pair.diffByAbs(M))
					.reduce(Pair::add)
					.map(r -> r.price.divide(BigDecimal.valueOf(r.index),
											PriceConstants.DEF_ACCURACY,
											RoundingMode.HALF_UP))
					.orElse(PriceConstants.NULL_PRICE);
		}
		catch (Exception e) {

			logger.error("CryptoCurrStreamed::" + exactMethodName + "(): " + e);
			return PriceConstants.NULL_PRICE;
		}
	}

	private static final class Pair {

		final int index;
		BigDecimal price;

		Pair(int index, BigDecimal price) {

			this.index = index;
			this.price = price;
		}

		Pair add(Pair p) {

			return new Pair(p.index, price.add(p.price));
		}

		Pair diffByAbs(BigDecimal M) {

			price = price.subtract(M);
			if (price.compareTo(BigDecimal.ZERO) < 0)
				price = price.negate();

			return this;
		}
	}
}
