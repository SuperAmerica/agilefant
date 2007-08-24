package fi.hut.soberit.agilefant.db.hibernate;

import java.sql.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
public class EffortHistoryDAOHibernate extends 
		GenericDAOHibernate<EffortHistory> implements 
		EffortHistoryDAO{

	private Log logger = LogFactory.getLog(getClass());
	
	public EffortHistoryDAOHibernate() {
		super(EffortHistory.class);
	}

	@SuppressWarnings("unchecked")
	public EffortHistory getByDateAndBacklog(Date date, Backlog backlog) {
		DetachedCriteria criteria = 
			DetachedCriteria.forClass(this.getPersistentClass());
		criteria.add(Restrictions.eq("date", date));
		criteria.add(Restrictions.eq("backlog", backlog));
		return super.getFirst(
				super.getHibernateTemplate().findByCriteria(criteria));
	}
	
	@SuppressWarnings("unchecked")
	public EffortHistory getMostRecent(Date date, Backlog backlog) {
		
		List<EffortHistory> resultList;
		HibernateTemplate ht = super.getHibernateTemplate();
		String[] queryParams = new String[] {"date", "backlog"};
		Object[] queryValues = new Object[] {date, backlog};
		
		String query3 = "from EffortHistory e " +
		"where e.backlog = :backlog and " +
		"e.date <= :date and " +
		"e.originalEstimate != null";

		String query2 = "select max(e.date) " + query3 + "";

		String query = "select f from EffortHistory f " +
		"where f.date = (" + query2 + ") and " +
		"f.backlog = :backlog " +
		"order by f.originalEstimate desc";
				
		resultList = (List<EffortHistory>)
			ht.findByNamedParam(query, queryParams, queryValues);
		
		if(resultList.isEmpty()) {
			return null;
		}
		return resultList.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public EffortHistory getLatest(Date startDate, Date endDate, 
			Backlog backlog) {
		
		List<EffortHistory> resultList;
		HibernateTemplate ht = super.getHibernateTemplate();
		String[] queryParams = 
			new String[] {"startDate", "endDate", "backlog"};
		Object[] queryValues =
			new Object[] {startDate, endDate, backlog};
		
		String query3 = "from EffortHistory e " +
		"where e.backlog = :backlog and " +
		"e.date >= :startDate and " +
		"e.date <= :endDate and " +
		"e.originalEstimate != null";

		String query2 = "select min(e.date) " + query3 + "";

		String query = "select f from EffortHistory f " +
		"where f.date = (" + query2 + ") and " +
		"f.backlog = :backlog " +
		"order by f.originalEstimate desc";
				
		resultList = (List<EffortHistory>)
			ht.findByNamedParam(query, queryParams, queryValues);
		
		if(resultList.isEmpty()) {
			return null;
		}
		return resultList.get(0);
	}
}
