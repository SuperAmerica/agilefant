package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.PracticeTemplateDAO;
import fi.hut.soberit.agilefant.model.PracticeTemplate;

/**
 * Hibernate implementation of PracticeTemplateDAO interface using
 * GenericDAOHibernate.
 */
public class PracticeTemplateDAOHibernate extends
        GenericDAOHibernate<PracticeTemplate> implements PracticeTemplateDAO {

    public PracticeTemplateDAOHibernate() {
        super(PracticeTemplate.class);
    }
}
