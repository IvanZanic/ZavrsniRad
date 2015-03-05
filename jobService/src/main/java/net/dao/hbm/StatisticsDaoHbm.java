package net.dao.hbm;

import java.util.List;

import net.dao.StatisticsDao;
import net.domain.Statistics.DataPercentages;
import net.domain.Statistics.MonthlyTrend;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StatisticsDaoHbm implements StatisticsDao {

	/**
	 * SessionFactory.
	 */
	@Autowired
	private SessionFactory sessionFactory;

	private Logger log = Logger.getLogger(EmployerDaoHbm.class);

	@SuppressWarnings("unchecked")
	@Transactional
	public List<DataPercentages> getPercentages(String startDate,
			String endDate, String type) {

		Session session = sessionFactory.getCurrentSession();

		try {

			String procedure = "";
			if (type.equals("category")) {
				procedure = "{ call categoryPercentage(:startDate, :endDate) }";
			} else if (type.equals("county")) {
				procedure = "{ call countyPercentage(:startDate, :endDate) }";
			} else if (type.equals("jobtype")) {
				procedure = "{ call jobtypePercentage(:startDate, :endDate) }";
			} else if (type.equals("qualification")) {
				procedure = "{ call qualificationPercentage(:startDate, :endDate) }";
			} else if (type.equals("skill")) {
				procedure = "{ call skillPercentage(:startDate, :endDate) }";
			}

			return session
					.createSQLQuery(procedure)
					.setResultTransformer(
							Transformers.aliasToBean(DataPercentages.class))
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate).list();

		} catch (Exception e) {
			log.error("Exception. StatisticsDaoHbm => Method: getPercentages: "
					+ e.toString() + ". Parameters: startDate = " + startDate
					+ ", endDate = " + endDate + ", type = " + type);
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<MonthlyTrend> getTehnologyTrend(String startDate,
			String endDate, String tehnologyIdXML, Integer countyId) {

		Session session = sessionFactory.getCurrentSession();

		try {

			return session
					.createSQLQuery(
							"{ call skillTrend(:xml, :startDate, :endDate, :countyId) }")
					.addScalar("counter", Hibernate.LONG)
					.addScalar("month", Hibernate.STRING)
					.addScalar("id", Hibernate.LONG)
					.setResultTransformer(
							Transformers.aliasToBean(MonthlyTrend.class))
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate)
					.setParameter("xml", tehnologyIdXML)
					.setParameter("countyId", countyId).list();

		} catch (Exception e) {
			log.error("Exception. StatisticsDaoHbm => Method: getTehnologyTrend: "
					+ e.toString()
					+ ". Parameters: startDate = "
					+ startDate
					+ ", endDate = "
					+ endDate
					+ ", tehnologyIds = "
					+ tehnologyIdXML + ", countyId = " + countyId);
			e.printStackTrace();
		}

		return null;
	}
}
