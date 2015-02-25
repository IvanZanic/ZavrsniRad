package net.domain.Statistics;

import java.math.BigDecimal;

public class DataPercentages {

	private Integer id;
	private BigDecimal percentage;

	public DataPercentages() {}
	
	public DataPercentages(Integer id, BigDecimal percentage) {
		super();
		this.id = id;
		this.percentage = percentage;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
