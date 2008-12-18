package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.model.Assignment;

public class AssignmentDAOHibernate extends GenericDAOHibernate<Assignment> implements
        AssignmentDAO {

    public AssignmentDAOHibernate() {
        super(Assignment.class);
    }

}
