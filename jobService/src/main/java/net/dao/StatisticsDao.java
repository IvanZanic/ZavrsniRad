package net.dao;

import java.util.List;

import net.domain.Statistics.DataPercentages;
import net.domain.Statistics.MonthlyTrend;

public interface StatisticsDao {

	public List<DataPercentages> getPercentages(String startDate, String endDate, String type);
	
	public List<MonthlyTrend> getTehnologyTrend(String startDate, String endDate, String tehnologyIdXML, Integer countyId);
}
