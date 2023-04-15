package com.goit.cryptocurr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class CryptoCurrencyImpl implements ICryptoCurrency {

	private static final Logger logger = LogManager.getRootLogger();
	private static final BigDecimal NOT_INITIALIZED = new BigDecimal(-1);
	private final String name;
	private final IDataProvider provider;

	public CryptoCurrencyImpl(String name, IDataProvider provider) {

		this.name = name;
		this.provider = provider;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public BigDecimal min() {

		BigDecimal min = NOT_INITIALIZED;
		try (IRecordsIterator p = provider.getRecordsIterator(name)) {
			while (p.hasNext()) {
				CryptoCurrRecord rec = p.next();
				if (min.compareTo(rec.price) > 0)
					min = rec.price;
			}
		}
		catch (IOException e) {

			logger.error("CryptoCurrency::min(): " + e);
			return NOT_INITIALIZED;
		}
		return min;
	}

	@Override
	public BigDecimal max() {

		BigDecimal max = NOT_INITIALIZED;
		try (IRecordsIterator p = provider.getRecordsIterator(name)) {
			while (p.hasNext()) {
				CryptoCurrRecord rec = p.next();
				if (max.compareTo(rec.price) < 0)
					max = rec.price;
			}
		}
		catch (IOException e) {

			logger.error("CryptoCurrency::max(): " + e);
			return NOT_INITIALIZED;
		}
		return max;
	}

	@Override
	public BigDecimal avg(Date start, Date end) {

		BigDecimal avg = BigDecimal.ZERO;
		int i = 0;
		try (IRecordsIterator p = provider.getRecordsIterator(name)) {
			while (p.hasNext()) {
				CryptoCurrRecord rec = p.next();
				if (rec.date.compareTo(start) >= 0 && rec.date.compareTo(end) <= 0) {
					avg = avg.add(rec.price);
					i++;
				}
			}
		}
		catch (IOException e) {

			logger.error("CryptoCurrency::avg(): " + e);
			return NOT_INITIALIZED;
		}

		if (i == 0) return NOT_INITIALIZED;
		return avg.divide(BigDecimal.valueOf(i), RoundingMode.UNNECESSARY)
				.setScale(2, RoundingMode.CEILING);
	}

	@Override
	public BigDecimal norm() {

		BigDecimal min = min();
		if (min.equals(BigDecimal.ZERO) || min == NOT_INITIALIZED)
			return NOT_INITIALIZED;

		BigDecimal max = max();
		if (max == NOT_INITIALIZED)
			return NOT_INITIALIZED;

		BigDecimal norm = max.subtract(min);
		return norm.divide(min, RoundingMode.UNNECESSARY)
				.setScale(2, RoundingMode.CEILING);
	}

	public BigDecimal devFromAvg() {

		BigDecimal avg = avg(new Date(0), new Date(Integer.MAX_VALUE));
		if (avg == NOT_INITIALIZED)
			return NOT_INITIALIZED;

		BigDecimal dev = BigDecimal.ZERO;
		int i = 0;
		try (IRecordsIterator p = provider.getRecordsIterator(name)) {
			while (p.hasNext()) {
				CryptoCurrRecord rec = p.next();

				BigDecimal rec_dev = rec.price.subtract(avg);
				if (rec_dev.compareTo(BigDecimal.ZERO) < 0)
					rec_dev = rec_dev.negate();

				dev = dev.add(rec_dev);
				i++;
			}
		}
		catch (IOException e) {

			logger.error("CryptoCurrency::devFromAvg(): " + e);
			return NOT_INITIALIZED;
		}

		if (i == 0) return NOT_INITIALIZED;
		return dev.divide(BigDecimal.valueOf(i), RoundingMode.UNNECESSARY)
				.setScale(2, RoundingMode.CEILING);
	}

	public BigDecimal devFromNorm() {

		BigDecimal norm = norm();
		if (norm == NOT_INITIALIZED)
			return NOT_INITIALIZED;

		BigDecimal dev = BigDecimal.ZERO;
		int i = 0;
		try (IRecordsIterator p = provider.getRecordsIterator(name)) {
			while (p.hasNext()) {
				CryptoCurrRecord rec = p.next();

				BigDecimal rec_dev = rec.price.subtract(norm);
				if (rec_dev.compareTo(BigDecimal.ZERO) < 0)
					rec_dev = rec_dev.negate();

				dev = dev.add(rec_dev);
				i++;
			}
		}
		catch (IOException e) {

			logger.error("CryptoCurrency::devFromNorm(): " + e);
			return NOT_INITIALIZED;
		}

		if (i == 0) return NOT_INITIALIZED;
		return dev.divide(BigDecimal.valueOf(i), RoundingMode.UNNECESSARY)
				.setScale(2, RoundingMode.CEILING);
	}
}
