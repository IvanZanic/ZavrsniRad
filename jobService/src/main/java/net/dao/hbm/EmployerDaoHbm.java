package net.dao.hbm;

import java.util.List;

import net.dao.EmployerDao;
import net.domain.Employer;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EmployerDaoHbm implements EmployerDao{
	
	/**
	 * SessionFactory.
	 */
	@Autowired
	private SessionFactory sessionFactory;	
	
	private Logger log = Logger.getLogger(EmployerDaoHbm.class);

	public Employer getEmployerByName (String name) {
		
		Session session = sessionFactory.getCurrentSession();
		List<Employer> employers = null;
		try {
			Criteria searchCriteria = session.createCriteria(Employer.class)
					.setResultTransformer(
							CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			searchCriteria.add(Restrictions.eq("name", name));
			employers = searchCriteria.list();
		} catch (Exception e) {
			log.error("Exception. Method: getEmployerByName: " + e.toString() + ". Parameters: name = " + name);
			e.printStackTrace();
		}
		
		if(employers.size() == 0) {
			return null;
		}
		return employers.get(0);
	}
	
	public Employer getEmployerById (int id) {
		
		Session session = sessionFactory.getCurrentSession();
		try {
			return (Employer) session.get(Employer.class, id);
		}
		catch (Exception e) {
			log.error("Exception. Method: getEmployerById: " + e.toString()
					+ ". Parameters: id = " + id);			
			return null;
		}
	}	
	
	public void saveEmployer(Employer employer) {
		
		Session session = sessionFactory.getCurrentSession();

		try {
			session.save(employer);
		} catch (HibernateException e) {
			log.error("HibernateException. Method: saveEmployer: " + e.toString()
					+ ". Parameters: id = " + employer.getId() + ", name = "
					+ employer.getName());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception. Method: saveEmployer: " + e.toString()
					+ ". Parameters: id = " + employer.getId() + ", name = "
					+ employer.getName());			
			e.printStackTrace();
		}
	}
}
