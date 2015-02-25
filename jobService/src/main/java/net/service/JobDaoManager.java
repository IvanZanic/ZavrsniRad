package net.service;

import java.util.List;

import net.dao.JobDao;
import net.domain.Job;
import net.domain.JobRating;
import net.domain.TotalJobRatingView;
import net.domain.help.JobSearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobDaoManager {

	@Autowired
	JobDao jobDao;

	public void saveJob(Job job) {
		jobDao.saveJob(job);
	}

	public void deleteJob(Job job) {
		jobDao.deleteJob(job);
	}

	public Job getJobById(int id) {
		return jobDao.getJobById(id);
	}

	public List<JobSearchResult> getJobList(Integer categoryId,
			Integer countyId, Integer jobtypeId, Integer qualificationId,
			Integer skillId, String keyword, Integer offset, String sortBy) {
		
		return jobDao.getJobList(categoryId, countyId, jobtypeId,
				qualificationId, skillId, keyword, offset, sortBy);
	}

	public List<JobSearchResult> getJobsByEmployer(Integer employerId, Integer offset) {
		return jobDao.getJobsByEmployer(employerId, offset);
	}
	
	public List<JobRating> getJobRatings(Integer jobId) {
		return jobDao.getJobRatings(jobId);
	}
	
	public void saveJobRating(JobRating jobRating) {
		jobDao.saveJobRating(jobRating);
	}
}
