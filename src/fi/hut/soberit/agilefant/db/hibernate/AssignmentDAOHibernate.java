package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.User;

public class AssignmentDAOHibernate extends GenericDAOHibernate<Assignment> implements
        AssignmentDAO {

    public AssignmentDAOHibernate() {
        super(Assignment.class);
    }

}
