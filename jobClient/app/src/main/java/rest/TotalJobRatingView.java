package rest;

import java.io.Serializable;
import java.math.BigDecimal;

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

    public int getId() {
        return id;
    }
    
	public void setId(final int id) {
        this.id = id;
    }    

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public Job getJob() {
		return this.job;
	}

	public void setJob(Job job) {
		this.job = job;
	}	
}
