package net.dao;

import java.util.List;

import net.domain.Job;
import net.domain.JobRating;
import net.domain.TotalJobRatingView;
import net.domain.help.JobSearchResult;

import org.springframework.stereotype.Repository;

@Repository
public interface JobDao {

	public void saveJob(Job job);

	public void deleteJob(Job job);

	public Job getJobById(int id);

	public List<JobSearchResult> getJobList(Integer categoryId,
			Integer countyId, Integer jobtypeId, Integer qualificationId,
			Integer skillId, String keyword, Integer offset, String sortBy);

	public List<JobSearchResult> getJobsByEmployer(Integer employerId, Integer offset);
	
	public List<JobRating> getJobRatings(Integer jobId);
	
	public void saveJobRating(JobRating jobRating);
}
