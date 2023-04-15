package com.goit.cryptocurr;

import java.math.BigDecimal;
import java.util.Date;

public interface ICryptoCurrency {
	String getName();
	BigDecimal min();
	BigDecimal max();
	BigDecimal avg(Date start, Date end);
	BigDecimal norm();
}