package net.dao.hbm;

import java.util.List;

import net.dao.JobDao;
import net.domain.Job;
import net.domain.JobRating;
import net.domain.TotalJobRatingView;
import net.domain.help.JobSearchResult;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JobDaoHbm implements JobDao {

	/**
	 * SessionFactory.
	 */
	@Autowired
	private SessionFactory sessionFactory;

	private Logger log = Logger.getLogger(JobDaoHbm.class);

	@Transactional
	public void saveJob(Job job) {

		Session session = sessionFactory.getCurrentSession();

		try {
			session.save(job);
		} catch (HibernateException e) {
			log.error("HibernateException. JobDaoHbm => Method: saveJob: "
					+ e.toString() + ". Parameters: id = " + job.getId()
					+ ", employer_id = " + job.getEmployer().getId()
					+ ", deadline = " + job.getDeadline() + ", link = "
					+ job.getLink());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@Transactional
	public void deleteJob(Job job) {

		Session session = sessionFactory.getCurrentSession();

		try {
			session.delete(job);
		} catch (HibernateException e) {
			log.error("HibernateException. JobDaoHbm => Method: deleteJob: "
					+ e.toString() + ". Parameters: id = " + job.getId()
					+ ", employer_id = " + job.getEmployer().getId()
					+ ", deadline = " + job.getDeadline() + ", link = "
					+ job.getLink());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: deleteJob: "
					+ e.toString() + ". Parameters: id = " + job.getId()
					+ ", employer_id = " + job.getEmployer().getId()
					+ ", deadline = " + job.getDeadline() + ", link = "
					+ job.getLink());
			e.printStackTrace();
		}
	}

	@Transactional
	public Job getJobById(int id) {

		Session session = sessionFactory.getCurrentSession();
		Job job = null;
		try {
			job = (Job) session.get(Job.class, id);
		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: getJobById: "
					+ e.toString() + ". Parameters: id = " + id);
			job = null;
			e.printStackTrace();
		}
		return job;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<JobSearchResult> getJobList(Integer categoryId,
			Integer countyId, Integer jobtypeId, Integer qualificationId,
			Integer skillId, String keyword, Integer offset, String sortBy) {

		Session session = sessionFactory.getCurrentSession();
		List<JobSearchResult> jobList = null;
		StringBuffer hql = new StringBuffer();
		StringBuffer hqlHelp = new StringBuffer();

		try {

			hql.append("select new net.domain.help.JobSearchResult(job.id, job.title, job.deadline, emp.name, totalRat.rating) from Job job join job.employer emp ");

			if (categoryId != null) {
				hql.append("join job.categories cat ");
				hqlHelp.append("cat.id=:categoryId AND ");
			}
			if (countyId != null) {
				hql.append("join job.counties coun ");
				hqlHelp.append("coun.id=:countyId AND ");
			}
			if (jobtypeId != null) {
				hql.append("join job.jobTypes jobT ");
				hqlHelp.append("jobT.id=:jobtypeId AND ");
			}
			if (qualificationId != null) {
				hql.append("join job.qualifications qual ");
				hqlHelp.append("qual.id=:qualificationId AND ");
			}
			if (skillId != null) {
				hql.append("join job.techSkills sk ");
				hqlHelp.append("sk.id=:skillId AND ");
			}			
			
			
//			join job.totalJobRatingView total 
			hql.append("left join job.totalJobRatingsView totalRat where ");
			if (keyword != null) {
				hql.append("job.title like :keyword AND ");
			}
			
//			select new net.domain.help.JobSearchResult(job.id, job.title, job.deadline, emp.name, totalRat.rating) 
//			from Job job join job.employer emp join job.totalJobRatingsView totalRat 
//			where date(deadline) >= date(now()) order by job.publishDate desc 			
			
			hql.append(hqlHelp);
			hql.append("date(deadline) >= date(now()) ");
			
			if(sortBy.equals("publishDate")) {
				hql.append("order by job.publishDate desc ");
				
			} else if(sortBy.equals("totalRating")) {
				hql.append("order by totalRat.rating desc ");
				
			} else if(sortBy.equals("deadline")) {
				hql.append("order by job.deadline ");
			}			

			Query query = session.createQuery(hql.toString()); // .setResultTransformer(Transformers.aliasToBean(JobSearchResult.class));

			if (categoryId != null) {
				query.setInteger("categoryId", categoryId);
			}
			if (countyId != null) {
				query.setInteger("countyId", countyId);
			}
			if (jobtypeId != null) {
				query.setInteger("jobtypeId", jobtypeId);
			}
			if (qualificationId != null) {
				query.setInteger("qualificationId", qualificationId);
			}
			if (skillId != null) {
				query.setInteger("skillId", skillId);
			}			
			if (keyword != null) {
				query.setString("keyword", '%' + keyword + '%');
			}
			if (offset != null) {
				query.setFirstResult(offset);
				query.setMaxResults(20);
			} else {
				query.setFirstResult(0);
				query.setMaxResults(20);
			}
			
			jobList = query.list();
			return jobList;

		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: getJobList: "
					+ e.toString() + ". Parameters: categoryId = " + categoryId
					+ ", countyId = " + countyId + ", jobtypeId = " + jobtypeId
					+ ", qualificationId = " + qualificationId + "skillId = "
					+ skillId + ", keyword = " + keyword + ", offset = "
					+ offset);
			e.printStackTrace();
		} finally {
			// try { session.close(); } catch (Exception e) { log.error(e); }
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<JobSearchResult> getJobsByEmployer(Integer employerId, Integer offset) {

		Session session = sessionFactory.getCurrentSession();
		List<JobSearchResult> jobList = null;

		try {
			Query query = session
					.createQuery("select new net.domain.help.JobSearchResult(job.id, job.title, job.deadline, emp.name, totalRat.rating) "
							+ "from Job job "
							+ "join job.employer emp "
							+ "left join job.totalJobRatingsView totalRat "
							+ "where emp.id=:employerId "
							+ "order by job.publishDate desc");
			query.setInteger("employerId", employerId);
			
			if (offset != null) {
				query.setFirstResult(offset);
				query.setMaxResults(20);
			} else {
				query.setFirstResult(0);
				query.setMaxResults(20);
			}			

			jobList = query.list();
			return jobList;
			
		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: getJobById: "
					+ e.toString() + ". Parameters: employerId = " + employerId);
			e.printStackTrace();
		}

		return null;
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<JobRating> getJobRatings(Integer jobId) {
		
		Session session = sessionFactory.getCurrentSession();
		List<JobRating> ratingList = null;
		
		try {
			
			Query query = session.createQuery("from JobRating jr where jr.job.id=:jobId");
			query.setInteger("jobId", jobId);	
			
			ratingList = query.list();
			return ratingList;
			
		} catch (HibernateException e) {
			log.error("HibernateException. JobDaoHbm => Method: getJobRatings: "
					+ e.toString() + ". Parameters: jobId = " + jobId);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: getJobRatings: "
					+ e.toString() + ". Parameters: jobId = " + jobId);
			e.printStackTrace();
		}
		
		return null;
	}

	@Transactional
	public void saveJobRating(JobRating jobRating) {
		
		Session session = sessionFactory.getCurrentSession();
		
		try {
			session.saveOrUpdate(jobRating);
		} catch (HibernateException e) {
			log.error("HibernateException. JobDaoHbm => Method: saveJobRating: "
					+ e.toString() + ". Parameters: jobRating = " + jobRating);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception. JobDaoHbm => Method: saveJobRating: "
					+ e.toString() + ". Parameters: jobRating = " + jobRating);
			e.printStackTrace();
		}
	}
}

