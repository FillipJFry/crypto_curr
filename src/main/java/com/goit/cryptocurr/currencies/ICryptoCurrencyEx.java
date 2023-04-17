package com.goit.cryptocurr.currencies;

import com.goit.cryptocurr.ICryptoCurrency;

import java.math.BigDecimal;

public interface ICryptoCurrencyEx extends ICryptoCurrency {

	BigDecimal devFromAvg();
	BigDecimal devFromNorm();
}
