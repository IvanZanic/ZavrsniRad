package net.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.domain.Category;
import net.domain.Employer;
import net.domain.Job;
import net.http.HttpRequestHandling;
import net.parser.JobParser;
import net.service.EmployerDaoManager;
import net.service.JobDaoManager;
import net.util.Utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParserTask {
	private Logger log = Logger.getLogger(ParserTask.class);
	
	@Autowired
	JobDaoManager jobDaoManager;
	
	@Autowired
	EmployerDaoManager employerDaoManager;	
	
	@Autowired
	JobParser jp;

//	@Scheduled(fixedRate= 1000 * 60 * 60 * 24)
	public void getJobsTask() {
		
		List<Job> allJobs = new ArrayList<Job>();	
		boolean isLastPage = false;
		int pageCount = 1;

		// goes through pages and collects job and employer ids and links	
		while(!isLastPage) {
		
			String urlParameters = null;
			
			try {
				urlParameters = "?published=" + URLEncoder.encode("1", "UTF-8") +
						"&page=" + URLEncoder.encode(String.valueOf(pageCount), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("UnsupportedEncodingException: " + e.toString() + ". Parameters: " + urlParameters);
				e.printStackTrace();
			}
			
			// svi poslovi
			String response = null;
			try {
				response = HttpRequestHandling.sendGet("http://www.moj-posao.net/Pretraga-Poslova/", urlParameters);
			} catch (IOException e) {
				log.error("IOException: " + e.toString() + ". Parameters: " + urlParameters);
				e.printStackTrace();
			}

			jp.setHtml(response);
			if(jp.featuredJobsExist()) {
				allJobs = jp.getFeaturedJobIdAndLink(allJobs);	
			}

			isLastPage = jp.checkIfLastPage();
			if(isLastPage) {
				if (jp.regionalJobFirst()) {
					break;
				} else {
					allJobs = jp.getJobIdAndLink(allJobs);
				}
			}
			else {
				allJobs = jp.getJobIdAndLink(allJobs);
			}
			
			pageCount++;
		}
		
		String response = null;
		
		// iterates through jobs and saves them into database if they don't exist
		int count = 0;
		for(Job job : allJobs) {
			
			// if job doesn't exist in database
			if(jobDaoManager.getJobById(job.getId()) == null) {

				Employer employer = null;
				
				// if employer id is undefined on page
				if(job.getEmployer().getId() == 0 || job.getEmployer().getId() >= 10000000) {
					employer = employerDaoManager.getEmployerByName(job.getEmployer().getName());

					// if employer exists in database
					if(employer != null) {
						job.setEmployer(employer);
					}
					else {
						// employer doesn't exist in database
						do {
							int employerId = Utils.generateRandomNumber(10000000, 99999999);
							employer = employerDaoManager.getEmployerById(employerId);
							if(employer == null) {
								job.getEmployer().setId(employerId);
								employerDaoManager.saveEmployer(job.getEmployer());
								break;
							}							
						} while (employer != null);
					}
				}
				else {
					
					employer = employerDaoManager.getEmployerById(job.getEmployer().getId());
					
					// if employer exists in database
					if(employer != null) {
						job.setEmployer(employer);
					}
					else {
						try {
							response = HttpRequestHandling.sendGet(job.getEmployer().getLink(), "");
						} catch (IOException e) {
							log.error("IOException: " + e.toString() + ". Employer link: " + job.getEmployer().getLink());
							e.printStackTrace();
						}

						jp.setHtml(response);
						String address = jp.getEmployerAddress();
						job.getEmployer().setAddress(address);
						
						employerDaoManager.saveEmployer(job.getEmployer());
					}
				}
			
				try {
					response = HttpRequestHandling.sendGet(job.getLink(), "");
				} catch (IOException e) {
					log.error("IOException: " + e.toString() + ". Job link: " + job.getLink());
					e.printStackTrace();
				}

				jp.setHtml(response);
				
				String title = jp.getTitle(); 
				if(title == null || title.equals("")) {
					log.error("Title error = no title, job_id: " + job.getId());
					continue;
				}
				
				Date deadline = jp.getApplicationDeadline();
				if (deadline == null) {
					log.error("Deadline error = no deadline, job_id: " + job.getId());
					continue;
				}
				
				job.setTitle(title);
				job.setDeadline(jp.getApplicationDeadline());
				job.setDescription(jp.getDescription());
				job.setConditions(jp.getConditions());
				job.setQualifications(jp.getQualification());				
				job.setYearsOfExperience(jp.getYearsOfExperience());
				job.setLanguages(jp.getLanguages());
				job.setSkills(jp.getSkills());
				job.setDrivingLicence(jp.getDrivingLicense());
				job.setEmployerOffer(jp.getEmployeeOffer());
				job.setJobTypes(jp.getJobTypes());
				job.setCategories(jp.getCategories());
				job.setCounties(jp.getCounties());
				
				// traženje tehnologija - ovo se radi samo u slučaju da je kategorija posla = IT
				if (job.getCategories() != null) {
					Iterator<Category> itr = job.getCategories().iterator();
					while(itr.hasNext()) {
						Category cat = itr.next();
						if (cat.getId() == 11) {
							job.setTechSkills(jp.getTechSkills());
						}
					}
				}
				
				jobDaoManager.saveJob(job);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("InterruptedException: " + e.toString());
					e.printStackTrace();
				}
				count++;
			}
		}
		System.out.println();
		System.out.println("Gotovo!");
	}
}
