package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BusinessThemeDAOHibernate extends GenericDAOHibernate<BusinessTheme> implements BusinessThemeDAO {

    public BusinessThemeDAOHibernate() {
        super(BusinessTheme.class);
    }
    
}
