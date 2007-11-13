package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.PracticeDAO;
import fi.hut.soberit.agilefant.model.Practice;

/**
 * Hibernate implementation of PracticeDAO interface using GenericDAOHibernate.
 */
public class PracticeDAOHibernate extends GenericDAOHibernate<Practice>
        implements PracticeDAO {

    public PracticeDAOHibernate() {
        super(Practice.class);
    }
}
