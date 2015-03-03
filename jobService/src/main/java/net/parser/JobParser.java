package net.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.domain.Category;
import net.domain.County;
import net.domain.Employer;
import net.domain.Job;
import net.domain.JobType;
import net.domain.Qualification;
import net.domain.Skill;
import net.enums.CategoryEnum;
import net.enums.CountyEnum;
import net.enums.JobTypeEnum;
import net.enums.QualificationEnum;
import net.enums.SkillEnum;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class JobParser {
	
	private Logger log = Logger.getLogger(JobParser.class);
	
	private String html;
	private Document doc;
	
	public String getTitle () {
		
		return doc.select("#page-title h1 span").text();
	}	
	
	public Set<County> getCounties () {

		String jobIds = doc.select("#job-position .details dd").eq(1).html();

		County county = null;
		if(jobIds == null || jobIds.equals("")) {
			return null;			
		} else {
			Set<County> counties = new HashSet<>();
			String[] a = jobIds.split("<br />");
			for(int i = 0; i<a.length;i++) {
				county = new County();
				county.setId(CountyEnum.getId(a[i].trim()));
				counties.add(county);
			}
			return counties;
		}
	}
	
	public Set<Category> getCategories () {

		Elements elements = doc.select("#job-position .details dd a");

		Category category = null;
		if(elements.size() == 0) {
			return null;
		} else {
			Set<Category> categories = new HashSet<>();	
			for(Element element : elements) {
				category = new Category();
				category.setId(CategoryEnum.getId(element.text()));
				categories.add(category);
			}
			return categories;
		}
	}
	
	public Set<JobType> getJobTypes() {

		String jobTypesStr = getPropertyString("Vrsta zaposlenja:", "#job-benefits dt");

		JobType jobType = null;
		if(jobTypesStr == null || jobTypesStr.equals("")) {
			return null;			
		} else {
			Set<JobType> jobTypes = new HashSet<>();
			String[] jt = jobTypesStr.split(",");
			for(int i = 0; i<jt.length;i++) {
				jobType = new JobType();
				jobType.setId(JobTypeEnum.getId(jt[i].trim()));
				jobTypes.add(jobType);
			}
			return jobTypes;
		}		
	}	
	
	public Set<Qualification> getQualification () {

		String qual = getPropertyString("Stručna sprema:", "#job-expect dt");

		Qualification qualification = null;
		if(qual == null || qual.equals("")) {
			return null;
		} else {
			Set<Qualification> qualifications = new HashSet<>();
			String[] qualArray = qual.split(",");			
			for(int i = 0; i<qualArray.length;i++) {
				qualification = new Qualification();
				qualification.setId(QualificationEnum.getId(qualArray[i].trim()));
				qualifications.add(qualification);
			}
			return qualifications;
		}
	}	
	
	public Date getApplicationDeadline () {
		
		String appDeadline = doc.select("#page-title .font14 strong").text();
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.MM.yyyy.").parse(appDeadline);
		} catch (ParseException e) {
			log.error("ParseException. Method: getApplicationDeadline: " + e.toString());
			e.printStackTrace();
		}	
		
		return date;
	}
	
	public boolean checkCloseBeforeDeadline () {
		
		return true;
	}
	
	public String getDescription () {
		
		String desc = doc.select("#job-description p").html();
		desc = desc.replace("<br />", "\n");		
		
		return desc;
	}
	
	public String getConditions () {
		
		String cond = doc.select("#job-expect p").html();
		cond = cond.replace("<br />", "\n");
		
		return cond;
	}	
	
	public Set<Skill> getTechSkills () {
		
		String cond = getConditions();
		Set<Skill> techSkills = new HashSet<>();
		
		if(cond == null) {
			return null;	
		}
		else {
			cond = cond.toLowerCase();
		}

		Skill skill = null;
		String skillName = "";
        for (SkillEnum se : SkillEnum.values()) {
        	skillName = se.getName().toLowerCase();
        	if (cond.contains(skillName)) {
        		Pattern pattern = Pattern.compile("([^a-zA-Z]|[ ]|^)"+skillName+"([^a-zA-Z]|[ ]|$|\\r\\n)");
        		Matcher matcher = pattern.matcher(cond);
        		
        		if(matcher.find()) {
            		skill = new Skill();
            		skill.setId(se.getId());
            		techSkills.add(skill);
        		}
        	}
        }
		if(techSkills.size() == 0) {
			return null;	
		}
		
		Iterator<Skill> itr = techSkills.iterator();
		while(itr.hasNext()) {
			System.out.println(SkillEnum.getName(itr.next().getId()));
		}		

		return techSkills;
	}	
	
	public String getYearsOfExperience () {
		
		String years = getPropertyString("Potrebne godine iskustva:", "#job-expect dt");
		return years;
	}
	
	public String getLanguages () {
		
		String languages = getPropertyString("Jezici:", "#job-expect dt");
		return languages;
	}	
	
	public String getDrivingLicense () {
		
		String drivingLicence = getPropertyString("Vozačka dozvola:", "#job-expect dt");
		return drivingLicence;
	}
	
	public String getSkills () {
		
		String skills = getPropertyString("Vještine:", "#job-expect dt");
		return skills;
	}	
	
	private String getPropertyString (String helpStr, String cssQuery) {
		
		Elements elements = doc.select(cssQuery);
		String propertyString = null;
		
		for(Element element : elements) {
			if(element.getElementsByTag("strong").text().equals(helpStr)){
				propertyString = element.nextElementSibling().text();
				break;
			}
		}		
	
		return propertyString;
	}
	
	public String getEmployeeOffer() {
		
		String offer = doc.select("#job-benefits p").html();
		
		if(offer.equals("") || offer == null) {
			return null;
		}		

		offer = offer.replace("<br />", "\n");	
		return offer;		
	}
	
	// gets job id and job link, employer id 
	public List<Job> getJobIdAndLink (List<Job> jobs) {
		Job job = null;
		Employer employer = null;
		Elements elements = doc.select(".searchlist").eq(0).select(".job .details");
		
		for(Element jobElement : elements) {
			
			Elements aElements = jobElement.select("a");
			job = new Job();
			employer = new Employer();
			
			Element aJob = aElements.get(0);
			String linkJob = aJob.attr("href");
			linkJob = linkJob.replaceFirst("www","m");
			job.setLink(linkJob);
			
			linkJob = linkJob.substring(linkJob.indexOf("/")+1, linkJob.length());
			linkJob = linkJob.substring(linkJob.indexOf("/")+1, linkJob.length());
			linkJob = linkJob.substring(linkJob.indexOf("/")+1, linkJob.length());
			linkJob = linkJob.substring(linkJob.indexOf("/")+1, linkJob.length());
			String id = linkJob.substring(0, linkJob.indexOf("/"));
			
			job.setId(Integer.parseInt(id));

			Element aEmpoyer = null;
			try {
				aEmpoyer = aElements.get(1);
				String linkEmployer = aEmpoyer.attr("href");
				employer.setLink(linkEmployer);
				
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				id = linkEmployer.substring(0, linkEmployer.indexOf("/"));
				
				employer.setId(Integer.parseInt(id));
			}
			catch (IndexOutOfBoundsException e) {
				employer.setId(0);
			}
			
			employer.setName(getEmployerName(jobElement, employer.getId()));
			
			job.setEmployer(employer);
			jobs.add(job);
		}
		
		return jobs;
	}
	
	public List<Job> getFeaturedJobIdAndLink (List<Job> jobs) {
		
		Job job = null;
		Employer employer = null;
		Elements elements = doc.select("#featured-jobs li");
		
		for(Element jobElement : elements) {
			
			employer = new Employer();
			Elements empElements = jobElement.select(".logo");
			
			if(empElements.size() == 1) {
				employer.setName(empElements.get(0).attr("title"));
				employer.setId(0);
			} else {
				String linkEmployer = empElements.get(0).attr("href");
				employer.setLink(linkEmployer);
				
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				linkEmployer = linkEmployer.substring(linkEmployer.indexOf("/")+1, linkEmployer.length());
				String id = linkEmployer.substring(0, linkEmployer.indexOf("/"));
				
				employer.setId(Integer.parseInt(id));
				employer.setName(empElements.get(1).attr("title"));
			}
			
			Elements aElements = jobElement.select("span a");
			for(Element aJob : aElements){
				job = new Job();
				String linkJob = aJob.attr("href");
				linkJob = linkJob.replaceFirst("www","m");
				job.setLink(linkJob);
				
				String id = aJob.attr("data-id");
				job.setId(Integer.parseInt(id));		
				
				job.setEmployer(employer);
				jobs.add(job);				
			}
		}	
		return jobs;
	}
	
	public boolean featuredJobsExist () {
		
		Elements elements = doc.select("#featured-jobs");
		
		try {
			elements.get(0);
			return true;
		}
		catch (IndexOutOfBoundsException e) {
			return false;	
		}
	}	
	
	public boolean regionalJobFirst () {
		
		Elements elements = doc.select(".searchlist").eq(0);
		for(Element element : elements) {
			if(element.select("h2 span").text().contains("Regionalni poslovi")) {
				return true;
			}
			else {
				return false;
			}
		}	
		return false;
	}
	
	private String getEmployerName(Element jobElement, int id) {
		
		String name = null;
		if(id == 0) {
			name = jobElement.select("strong").eq(0).text();
		}
		else {
			name = jobElement.select(".employer").text();
		}
		
		return name;
	}
	
	public String getEmployerAddress() {
		
		Elements elements = doc.select("#employer-profile .details li");
		String address = null;
		
		for (Element element : elements) {
			if(element.text().contains("Adresa:")) {
				address = element.text().replace("Adresa: ", ""); 
			}
		}
		
		return address;
	}
	
	public boolean checkIfLastPage() {
		
		Elements elements1 = doc.select(".searchlist").eq(1);
		
		if(elements1.size() == 0) {
			Elements elements2 = doc.select(".searchlist").eq(0);
			for(Element element : elements2) {
				if(element.select("h2 span").text().contains("Regionalni poslovi")) {
					return true;
				}
				else {
					return false;
				}
			}
			return true;	
		}
		else {
			return true;
		}
	}	

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
		doc = Jsoup.parse(this.html);
	}
}
