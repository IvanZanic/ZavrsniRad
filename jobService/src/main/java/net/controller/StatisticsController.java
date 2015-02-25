package net.controller;

import java.util.List;

import net.domain.Statistics.DataPercentages;
import net.domain.Statistics.MonthlyTrend;
import net.service.StatisticsDaoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {

	@Autowired
	StatisticsDaoManager statisticsDaoManager;

	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/getPercentages")
	public @ResponseBody List<DataPercentages> getPercentages(@RequestParam(required=false) String startDate,
			@RequestParam String endDate, 
			@RequestParam String type) 
			{
		
//		http://localhost:8080/webService/statistics/getPercentages?type=category&startDate=2014-12-25&endDate=2015-01-15		
		
		return statisticsDaoManager.getPercentages(startDate, endDate, type);

	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/getTehnologyTrend")
	public @ResponseBody List<MonthlyTrend> getTehnologyTrend(@RequestParam(required=false) String startDate,
			@RequestParam String endDate, 
			@RequestParam Integer tehnologyId) 
			{
		
//		http://localhost:8080/webService/statistics/getTehnologyTrend?tehnologyId=10&startDate=2015-01-23&endDate=2015-02-07		
		
		return statisticsDaoManager.getTehnologyTrend(startDate, endDate, tehnologyId);

	}	
}
