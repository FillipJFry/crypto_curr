package com.goit.cryptocurr;

import java.math.BigDecimal;
import java.util.Date;

public class CryptoCurrRecord {

  //even if the fields are final, you shouldn't make it public. 
  //for example, lot of frameworks, libraries, etc. assume the presence of getters and private fields as a standard. 
  //Also, when someone tries to read this code, is it also expecting a call like cryptoCurrRecord.getNeam() and not like cryptoCurrRecord.name (reduces readability)
  //Gettr and setter can be use like consumer and supplier
  private final String name;
	private final BigDecimal price;
  //class Date in fact is deprecated. why don't we use LocalDate (and another classes from java.time.*)?
	private final Date date;

	public CryptoCurrRecord(String name, BigDecimal price, Date date) {
		this.name = name;
		this.price = price;
		this.date = date;
	}

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Date getDate() {
    return date;
  }
  
}
