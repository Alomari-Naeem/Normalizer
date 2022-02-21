package com.progressoft.tools;

import java.math.BigDecimal;

public class ScoringSummaryImpl implements ScoringSummary{

	private BigDecimal mean;
	private BigDecimal standardDeviation;
	private BigDecimal variance;
	private BigDecimal median;
	private BigDecimal min;
	private BigDecimal max;
	
	
	public ScoringSummaryImpl(BigDecimal mean, BigDecimal standardDeviation, BigDecimal variance, BigDecimal median,
			BigDecimal min, BigDecimal max) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.variance = variance;
		this.median = median;
		this.min = min;
		this.max = max;
	}


	@Override
	public BigDecimal mean() {
		// TODO Auto-generated method stub
		return this.mean;
	}

	@Override
	public BigDecimal standardDeviation() {
		// TODO Auto-generated method stub
		return this.standardDeviation;
	}

	@Override
	public BigDecimal variance() {
		// TODO Auto-generated method stub
		return this.variance;
	}

	@Override
	public BigDecimal median() {
		// TODO Auto-generated method stub
		return this.median;
	}

	@Override
	public BigDecimal min() {
		// TODO Auto-generated method stub
		return this.min;
	}

	@Override
	public BigDecimal max() {
		// TODO Auto-generated method stub
		return this.max;
	}

}
