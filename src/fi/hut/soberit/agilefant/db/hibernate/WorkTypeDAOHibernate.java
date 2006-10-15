package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.WorkType;

public class WorkTypeDAOHibernate extends GenericDAOHibernate<WorkType> implements WorkTypeDAO {

	public WorkTypeDAOHibernate(){
		super(WorkType.class);
	}
}