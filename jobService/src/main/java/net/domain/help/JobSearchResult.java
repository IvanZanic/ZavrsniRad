package net.domain.help;

import java.math.BigDecimal;
import java.util.Date;

public class JobSearchResult {

	private int id;
	private String jobTitle;
	private String employerName;
	private Date deadline;
	private BigDecimal totalRating;
	
	public JobSearchResult(int id, String jobTitle, Date deadline, String employerName, BigDecimal totalRating) {
		this.id=id;
		this.jobTitle=jobTitle;
		this.deadline=deadline;
		this.employerName=employerName;
		this.totalRating = totalRating;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getEmployerName() {
		return employerName;
	}
	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public BigDecimal getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(BigDecimal totalRating) {
		this.totalRating = totalRating;
	}
}
