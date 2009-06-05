package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.model.Assignment;

@Repository("assignmentDAO")
public class AssignmentDAOHibernate extends GenericDAOHibernate<Assignment> implements
        AssignmentDAO {

    public AssignmentDAOHibernate() {
        super(Assignment.class);
    }

}
