package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.PracticeTemplateDAO;
import fi.hut.soberit.agilefant.model.PracticeTemplate;

public class PracticeTemplateDAOHibernate extends GenericDAOHibernate<PracticeTemplate> implements PracticeTemplateDAO {

	public PracticeTemplateDAOHibernate(){
		super(PracticeTemplate.class);
	}
}
