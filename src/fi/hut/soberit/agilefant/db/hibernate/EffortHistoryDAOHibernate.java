package fi.hut.soberit.agilefant.db.hibernate;

import java.sql.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
public class EffortHistoryDAOHibernate extends 
		GenericDAOHibernate<EffortHistory> implements 
		EffortHistoryDAO{

	public EffortHistoryDAOHibernate() {
		super(EffortHistory.class);
	}

	/**
	 * @param date Date of the effortHistory to be retrieved
	 * @return  EffortHistory of the seleceted date
	 */
	@SuppressWarnings("unchecked")
	public EffortHistory getByDateAndBacklog(Date date, Backlog backlog) {
		DetachedCriteria criteria = 
			DetachedCriteria.forClass(this.getPersistentClass());
		criteria.add(Restrictions.eq("date", date));
		criteria.add(Restrictions.eq("backlog", backlog));
		return super.getFirst(
				super.getHibernateTemplate().findByCriteria(criteria));
	}
}
