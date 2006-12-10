package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.PracticeAllocationDAO;
import fi.hut.soberit.agilefant.model.PracticeAllocation;

public class PracticeAllocationDAOHibernate extends GenericDAOHibernate<PracticeAllocation> implements PracticeAllocationDAO {

	public PracticeAllocationDAOHibernate(){
		super(PracticeAllocation.class);
	}
}
