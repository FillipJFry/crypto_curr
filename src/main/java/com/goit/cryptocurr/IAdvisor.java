package com.goit.cryptocurr;

import java.util.Optional;

public interface IAdvisor {

	Optional<ICryptoCurrency> pickByName(String name) throws Exception;
	Optional<ICryptoCurrency> pickByMinPrice() throws Exception;
	Optional<ICryptoCurrency> pickByMaxPrice() throws Exception;
	Optional<ICryptoCurrency> pickClosestToAvg() throws Exception;
	Optional<ICryptoCurrency> pickClosestToNorm() throws Exception;
}
