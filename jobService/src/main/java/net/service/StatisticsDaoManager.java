package net.service;

import java.util.List;

import net.dao.StatisticsDao;
import net.domain.Statistics.DataPercentages;
import net.domain.Statistics.MonthlyTrend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsDaoManager {

	@Autowired
	StatisticsDao statisticsDao;
	
	public List<DataPercentages> getPercentages(String startDate, String endDate, String type) {
		return statisticsDao.getPercentages(startDate, endDate, type);
	}
	
	public List<MonthlyTrend> getTehnologyTrend(String startDate, String endDate, String tehnologyIdXML, Integer countyId) {
		return statisticsDao.getTehnologyTrend(startDate, endDate, tehnologyIdXML, countyId);
	}	
}
