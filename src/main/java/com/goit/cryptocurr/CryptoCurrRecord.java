package com.goit.cryptocurr;

import java.math.BigDecimal;
import java.util.Date;

public class CryptoCurrRecord {

	public final String name;
	public final BigDecimal price;
	public final Date date;

	public CryptoCurrRecord(String name, BigDecimal price, Date date) {

		this.name = name;
		this.price = price;
		this.date = date;
	}
}
