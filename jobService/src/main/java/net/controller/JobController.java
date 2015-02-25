package net.controller;

import java.util.List;
import java.util.Map;

import net.domain.Job;
import net.domain.JobRating;
import net.domain.RatingType;
import net.domain.help.JobSearchResult;
import net.service.JobDaoManager;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/job")
public class JobController {
	
	@Autowired
	JobDaoManager jobDaoManager;

	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/get/{jobId}")
	public @ResponseBody
	Job getJob(@PathVariable int jobId) {

		Job job = jobDaoManager.getJobById(jobId);

		Hibernate.initialize(job.getCategories());
		Hibernate.initialize(job.getCounties());
		Hibernate.initialize(job.getQualifications());
		Hibernate.initialize(job.getJobTypes());
		Hibernate.initialize(job.getTechSkills());
		Hibernate.initialize(job.getJobRatings());
		Hibernate.initialize(job.getTotalJobRatingsView());

		return job;
	}

	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/getList")
	public @ResponseBody
	List<JobSearchResult> getJobs(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) Integer countyId,
			@RequestParam(required = false) Integer jobtypeId,
			@RequestParam(required = false) Integer qualificationId,
			@RequestParam(required = false) Integer skillId, 		
			@RequestParam(required = false) Integer offset,
			@RequestParam String sortBy) {
		
//		http://localhost:8080/webService/job/getList?sortBy=totalRating

		List<JobSearchResult> jobList = jobDaoManager.getJobList(categoryId,
				countyId, jobtypeId, qualificationId, skillId, keyword, offset, sortBy);

		return jobList;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.GET, value = "/getListByEmployer")
	public @ResponseBody
	List<JobSearchResult> getJobsByEmployer(@RequestParam Integer employerId,
			@RequestParam(required = false) Integer offset) {

		List<JobSearchResult> jobList = jobDaoManager.getJobsByEmployer(
				employerId, offset);

		return jobList;
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/rateJob")
	public @ResponseBody String evaluateJob(
			@RequestParam Map<String,String> map) {			
		
		Integer descRating = null;
		Integer jobRating = null;
		Integer empRequest = null;
		Integer jobId = null;
		
		if (map.containsKey("jobId")) {
			jobId = Integer.parseInt(map.get("jobId"));
		} else {
			return "Ne postoji parametar jobId!";
		}
			
		if (map.containsKey("descRating")) {
			descRating = Integer.parseInt(map.get("descRating"));
		}
		if (map.containsKey("jobRating")) {
			jobRating = Integer.parseInt(map.get("jobRating"));
		}
		if (map.containsKey("empRequest")) {
			empRequest = Integer.parseInt(map.get("empRequest"));
		}
		
		if (descRating == null && jobRating == null && empRequest == null) {
			return "Nema parametara za ocjenjivanje!";
		}
		
		List<JobRating> jobRatingList = jobDaoManager.getJobRatings(jobId);
		
		boolean descRatingIsSaved =false;
		boolean jobRatingIsSaved =false;
		boolean empRequestIsSaved =false;
		if (jobRatingList.size() != 0) {	
			for (JobRating jr : jobRatingList) {
				if (jr.getRatingType().getId() == 1) {
					if (descRating != null) {
						jr.setRatingSum(jr.getRatingSum() + descRating);
						jr.setRatingNum(jr.getRatingNum() + 1);
						jobDaoManager.saveJobRating(jr);
						descRatingIsSaved = true;
					}
				}
				if (jr.getRatingType().getId() == 2) {
					if (jobRating != null) {
						jr.setRatingSum(jr.getRatingSum() + jobRating);
						jr.setRatingNum(jr.getRatingNum() + 1);
						jobDaoManager.saveJobRating(jr);
						jobRatingIsSaved =true;
					}
				}
				if (jr.getRatingType().getId() == 3) {
					if (empRequest != null) {
						jr.setRatingSum(jr.getRatingSum() + empRequest);
						jr.setRatingNum(jr.getRatingNum() + 1);
						jobDaoManager.saveJobRating(jr);
						empRequestIsSaved =true;
					}
				}
			}
		} 
			
		Job job = jobDaoManager.getJobById(jobId);
		RatingType ratingType = new RatingType();
			
		if (descRating != null && !descRatingIsSaved) {
			JobRating jobRatingSave1 = new JobRating();
			jobRatingSave1.setJob(job);
			jobRatingSave1.setRatingNum(1);		
			ratingType.setId(1);
			jobRatingSave1.setRatingSum(descRating);
			jobRatingSave1.setRatingType(ratingType);
			jobDaoManager.saveJobRating(jobRatingSave1);	
			descRatingIsSaved = true;
		}
		
		if (jobRating != null && !jobRatingIsSaved) {
			JobRating jobRatingSave2 = new JobRating();
			jobRatingSave2.setJob(job);
			jobRatingSave2.setRatingNum(1);				
			ratingType.setId(2);
			jobRatingSave2.setRatingSum(jobRating);
			jobRatingSave2.setRatingType(ratingType);
			jobDaoManager.saveJobRating(jobRatingSave2);
			jobRatingIsSaved = true;
		}
		
		if (empRequest != null && !empRequestIsSaved) {
			JobRating jobRatingSave3 = new JobRating();
			jobRatingSave3.setJob(job);
			jobRatingSave3.setRatingNum(1);				
			ratingType.setId(3);
			jobRatingSave3.setRatingSum(empRequest);
			jobRatingSave3.setRatingType(ratingType);
			jobDaoManager.saveJobRating(jobRatingSave3);
			empRequestIsSaved = true;
		}
		
		if (!descRatingIsSaved && !jobRatingIsSaved && !empRequestIsSaved) {
			return "0";
		} else {
			return "1";
		}
	}	
}
