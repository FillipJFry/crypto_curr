package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.CryptoCurrRecord;
import com.goit.cryptocurr.IDataProvider;
import com.goit.cryptocurr.IRecordsIterator;
import com.goit.cryptocurr.PriceConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/*
iterator is a good pattern. the problem is that, according to the technical task, we do not process large amounts of data. 
in any case, if we have large files, using an iterator to read is a bad decision. 
we will spend a lot of time and resources (reading from files is a heavy operation) 
so in this case we must to use a database, not an iterator that reads files.
*/
public class CryptoCurrWithIterator implements ICryptoCurrencyEx {

  private static final Logger logger = LogManager.getRootLogger();
  private final String name;
  private final IDataProvider provider;

  public CryptoCurrWithIterator(String name, IDataProvider provider) {

    this.name = name;
    this.provider = provider;
  }

  @Override
  public String getName() {

    return name;
  }

  @Override
  public BigDecimal min() {

    BigDecimal min = PriceConstants.NULL_PRICE;
    try (IRecordsIterator p = provider.getRecordsIterator(name)) {
      while (p.hasNext()) {
        CryptoCurrRecord rec = p.next();
        if (min.compareTo(rec.getPrice()) > 0 || min.equals(PriceConstants.NULL_PRICE))
          min = rec.getPrice();
      }
    } catch (IOException e) {

      logger.error("CryptoCurrWithIterator::min(): " + e);
      return PriceConstants.NULL_PRICE;
    }
    return min;
  }

  @Override
  public BigDecimal max() {

    BigDecimal max = PriceConstants.NULL_PRICE;
    try (IRecordsIterator p = provider.getRecordsIterator(name)) {
      while (p.hasNext()) {
        CryptoCurrRecord rec = p.next();
        if (max.compareTo(rec.getPrice()) < 0)
          max = rec.getPrice();
      }
    } catch (IOException e) {

      logger.error("CryptoCurrWithIterator::max(): " + e);
      return PriceConstants.NULL_PRICE;
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
				if (rec.getDate().compareTo(start) >= 0 && rec.getDate().compareTo(end) <= 0) {
					avg = avg.add(rec.getPrice());
          i++;
        }
      }
		}
		catch (IOException e) {

      logger.error("CryptoCurrWithIterator::avg(): " + e);
      return PriceConstants.NULL_PRICE;
    }

		if (i == 0) return PriceConstants.NULL_PRICE;
    return avg.divide(BigDecimal.valueOf(i),
      PriceConstants.DEF_ACCURACY, RoundingMode.HALF_UP);
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

    if (M.equals(PriceConstants.NULL_PRICE))
      return PriceConstants.NULL_PRICE;

    BigDecimal dev = BigDecimal.ZERO;
    int i = 0;
    try (IRecordsIterator p = provider.getRecordsIterator(name)) {
      while (p.hasNext()) {
        CryptoCurrRecord rec = p.next();

        BigDecimal rec_dev = rec.getPrice().subtract(M);
        if (rec_dev.compareTo(BigDecimal.ZERO) < 0)
          rec_dev = rec_dev.negate();

        dev = dev.add(rec_dev);
        i++;
      }
    } catch (IOException e) {

      logger.error("CryptoCurrWithIterator::" + exactMethodName + "(): " + e);
      return PriceConstants.NULL_PRICE;
    }

    if (i == 0)
      return PriceConstants.NULL_PRICE;
    return dev.divide(BigDecimal.valueOf(i),
      PriceConstants.DEF_ACCURACY, RoundingMode.HALF_UP);
  }
}
