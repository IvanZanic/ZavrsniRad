package net.controller;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
			@RequestParam Integer[] tehnologyIds,
			@RequestParam Integer countyId) 
			{
		
//		http://localhost:8080/webService/statistics/getTehnologyTrend?tehnologyId=10&startDate=2015-01-23&endDate=2015-02-07
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc;
		String tehnologyIdXML = null;
		try {
			// put skill ids in xml 
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("ids");
			doc.appendChild(rootElement);
			
			for (int i = 0; i < tehnologyIds.length; i++) {
				Element value = doc.createElement("value");
				value.appendChild(doc.createTextNode(tehnologyIds[i].toString()));
				rootElement.appendChild(value);
			}

			// convert xml to string
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			tehnologyIdXML = writer.getBuffer().toString().replaceAll("\n|\r", "");
			
			List<MonthlyTrend> helpList = statisticsDaoManager.getTehnologyTrend(startDate, endDate, tehnologyIdXML, countyId);
			return helpList;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}	
}
