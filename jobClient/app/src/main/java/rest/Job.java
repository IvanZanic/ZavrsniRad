package rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Job {

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

    private List<Category> categories = new ArrayList<Category>(0);
    private List<Qualification> qualifications = new ArrayList<Qualification>(0);
    private List<JobType> jobTypes = new ArrayList<JobType>(0);
    private List<County> counties = new ArrayList<County>(0);
    private List<JobRating> jobRatings = new ArrayList<JobRating>(0);
    private Set<Skill> techSkills = new HashSet<Skill>(0);


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEmployerOffer() {
        return employerOffer;
    }

    public void setEmployerOffer(String employerOffer) {
        this.employerOffer = employerOffer;
    }

    public String getDrivingLicence() {
        return drivingLicence;
    }

    public void setDrivingLicence(String drivingLicence) {
        this.drivingLicence = drivingLicence;
    }


    public TotalJobRatingView getTotalJobRatingsView() {
        return totalJobRatingsView;
    }

    public void setTotalJobRatingsView(TotalJobRatingView totalJobRatingsView) {
        this.totalJobRatingsView = totalJobRatingsView;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public List<JobType> getJobTypes() {
        return jobTypes;
    }

    public void setJobTypes(List<JobType> jobTypes) {
        this.jobTypes = jobTypes;
    }

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }

    public List<JobRating> getJobRatings() {
        return jobRatings;
    }

    public void setJobRatings(List<JobRating> jobRatings) {
        this.jobRatings = jobRatings;
    }

    public Set<Skill> getTechSkills() {
        return techSkills;
    }

    public void setTechSkills(Set<Skill> techSkills) {
        this.techSkills = techSkills;
    }
}