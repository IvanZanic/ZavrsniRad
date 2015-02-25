package net.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonManagedReference;

@Entity
@Table(name="job"
    ,catalog="jobDatabase"
)
public class Job implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3057157048008588786L;
	private int id;
	private String title;
	private Employer employer;
	private Date publishDate;
	private Date deadline;
	private String link;
	private String description;
	private String conditions;
	private String yearsOfExperience;
	private String languages;
	private String skills;
	private String employerOffer;
	private String drivingLicence;
	
	private TotalJobRatingView totalJobRatingsView;
	
	private Set<Category> categories = new HashSet<Category>(0);
	private Set<Qualification> qualifications = new HashSet<Qualification>(0);
	private Set<JobType> jobTypes = new HashSet<JobType>(0);
	private Set<County> counties = new HashSet<County>(0);
	private Set<Skill> techSkills = new HashSet<Skill>(0);
	private Set<JobRating> jobRatings = new HashSet<JobRating>(0);
	
	
	public Job () {}

	@Id
	@Column(name="id", unique=true, nullable=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="title", nullable = false, length = 300)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="employer_id", nullable = false)
//	@JsonBackReference
	@JsonManagedReference
	public Employer getEmployer() {
		return employer;
	}

	public void setEmployer(Employer employer) {
		this.employer = employer;
	}

	@Column(name="publish_date", nullable=false)
	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(name="deadline", nullable=false)
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@ManyToMany(fetch=FetchType.LAZY)//, cascade = CascadeType.ALL
	@JoinTable(name="job_category", catalog="jobDatabase", joinColumns = { 
			@JoinColumn(name="job_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
			@JoinColumn(name="category_id", nullable=false, updatable=false) })	
	@JsonManagedReference
	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="job_qualification", catalog="jobDatabase", joinColumns = { 
			@JoinColumn(name="job_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
			@JoinColumn(name="qualification_id", nullable=false, updatable=false) })	
	@JsonManagedReference
	public Set<Qualification> getQualifications() {
		return qualifications;
	}

	public void setQualifications(Set<Qualification> qualifications) {
		this.qualifications = qualifications;
	}

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="job_jobtype", catalog="jobDatabase", joinColumns = { 
			@JoinColumn(name="job_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
			@JoinColumn(name="jobType_id", nullable=false, updatable=false) })	
	@JsonManagedReference
	public Set<JobType> getJobTypes() {
		return jobTypes;
	}

	public void setJobTypes(Set<JobType> jobTypes) {
		this.jobTypes = jobTypes;
	}

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="job_county", catalog="jobDatabase", joinColumns = { 
			@JoinColumn(name="job_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
			@JoinColumn(name="county_id", nullable=false, updatable=false) })	
	@JsonManagedReference
	public Set<County> getCounties() {
		return counties;
	}
	
	public void setCounties(Set<County> counties) {
		this.counties = counties;
	}	
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="job_skill", catalog="jobDatabase", joinColumns = { 
			@JoinColumn(name="job_id", nullable=false, updatable=false) }, inverseJoinColumns = { 
			@JoinColumn(name="skill_id", nullable=false, updatable=false) })	
	@JsonManagedReference	
	public Set<Skill> getTechSkills() {
		return techSkills;
	}

	public void setTechSkills(Set<Skill> techSkills) {
		this.techSkills = techSkills;
	}	
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="job")
	@JsonManagedReference
	public Set<JobRating> getJobRatings() {
		return jobRatings;
	}

	public void setJobRatings(Set<JobRating> jobRatings) {
		this.jobRatings = jobRatings;
	}

	@Column(name="link", nullable = false, length = 400)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(name="description", nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="conditions", nullable = true)
	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	@Column(name="yearsOfExperience", nullable = true, length = 50)
	public String getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(String yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	@Column(name="languages", nullable = true, length = 200)
	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	@Column(name="skills", nullable = true)
	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Column(name="employerOffer", nullable = true)
	public String getEmployerOffer() {
		return employerOffer;
	}

	public void setEmployerOffer(String employerOffer) {
		this.employerOffer = employerOffer;
	}

	@Column(name="drivingLicence", nullable = true, length = 50)
	public String getDrivingLicence() {
		return drivingLicence;
	}

	public void setDrivingLicence(String drivingLicence) {
		this.drivingLicence = drivingLicence;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy="job", cascade=CascadeType.ALL)
	@JsonManagedReference	
	public TotalJobRatingView getTotalJobRatingsView() {
		return totalJobRatingsView;
	}

	public void setTotalJobRatingsView(TotalJobRatingView totalJobRatingsView) {
		this.totalJobRatingsView = totalJobRatingsView;
	}

}
