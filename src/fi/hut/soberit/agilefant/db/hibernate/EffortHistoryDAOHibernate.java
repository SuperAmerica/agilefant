package fi.hut.soberit.agilefant.db.hibernate;

import java.sql.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

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
	public EffortHistory getByDate(Date date){
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getPersistentClass());
		criteria.add(Restrictions.eq("date", date));
		return super.getFirst(super.getHibernateTemplate().findByCriteria(criteria));
	}
}
