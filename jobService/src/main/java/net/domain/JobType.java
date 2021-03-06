package net.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonBackReference;

@Entity
@Table(name="jobtype"
    ,catalog="jobDatabase"
)
public class JobType implements Serializable{

	private static final long serialVersionUID = -4405204191773471174L;
	private int id;
	private String name;
	
	private Set<Job> jobs = new HashSet<Job>(0);
	
	public JobType () {}

	@Id
	@Column(name="id", unique=true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="name", nullable=false, length=50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonBackReference
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "jobTypes")
	public Set<Job> getJobs() {
		return jobs;
	}

	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}
}
