package com.goit.cryptocurr;

public final class AdvisorFactory {

	IAdvisor create(IDataProvider provider) {

		return new Advisor(provider);
	}
}
