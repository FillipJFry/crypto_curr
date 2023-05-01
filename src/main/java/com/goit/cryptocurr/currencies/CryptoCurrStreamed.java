package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IDataProvider;
import com.goit.cryptocurr.PriceConstants;
// i like guava, but in this case, it only complicates things :( 
//import com.google.common.collect.Streams;
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

      //we havent getters and we cann create more readeble 'CryptoCurrRecord::getPrice' :(
			return stream.map(rec -> rec.getPrice())
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

			return stream.map(rec -> rec.getPrice())
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
      double avg = stream
          .filter(rec -> rec.getDate().compareTo(start) >= 0 && rec.getDate().compareTo(end) <= 0)
          .mapToDouble(v->v.getPrice().doubleValue())
          .average().getAsDouble();
      return BigDecimal.valueOf(avg).setScale(PriceConstants.DEF_ACCURACY, RoundingMode.HALF_UP);
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
		return dev(avg(new Date(0), new Date(Long.MAX_VALUE)), "devFromAvg");
	}

	@Override
	public BigDecimal devFromNorm() {
		return dev(norm(), "devFromNorm");
	}

  // 'M' its bad name of var
	private BigDecimal dev(BigDecimal M, String exactMethodName) {
    
    /*
    BigDecimal is meaningless in this case. 
    we are forced to round the values when dividing and get an calculation error. 
    in this case, it makes no sense to complicate the code and use BigDecimal, because when using double we get the same result
    */

		try (Stream<CryptoCurrRecord> stream = provider.getRecordsStream(name)) {      
      double avg = stream
          .map(v->v.getPrice().subtract(M).abs())
          .mapToDouble(v->v.doubleValue())
          .average().getAsDouble();
      return BigDecimal.valueOf(avg).setScale(PriceConstants.DEF_ACCURACY, RoundingMode.HALF_UP);
		}
		catch (Exception e) {
			logger.error("CryptoCurrStreamed::" + exactMethodName + "(): " + e);
			return PriceConstants.NULL_PRICE;
		}
	}

}
