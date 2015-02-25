package net.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import net.domain.Job;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="total_job_rating"
    ,catalog="jobDatabase"
)
public class TotalJobRatingView implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4743175188687575075L;

    private int id;
	private BigDecimal rating;
	private Job job;
	
	public TotalJobRatingView () {}
	
    public TotalJobRatingView(int id, BigDecimal rating, Job job) {
		super();
		this.id = id;
		this.rating = rating;
		this.job = job;
	}

    @Id
    @Column(name="job_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@Parameter(name="property", value="job"))
    public int getId() {
        return id;
    }
    
	public void setId(final int id) {
        this.id = id;
    }    

    @Column(name="rating")
	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}
	
	@OneToOne()
	@PrimaryKeyJoinColumn
	@JsonBackReference
	public Job getJob() {
		return this.job;
	}

	public void setJob(Job job) {
		this.job = job;
	}	
}
